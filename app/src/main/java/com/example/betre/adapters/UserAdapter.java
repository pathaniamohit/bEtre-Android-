package com.example.betre.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betre.R;
import com.example.betre.models.User;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private DatabaseReference usersDB;
    private String currentUserRole; // To determine if the current user is an admin
    private FirebaseFunctions mFunctions;
    private static final String TAG = "UserAdapter";

    public UserAdapter(List<User> userList, Context context, String currentUserRole) {
        this.userList = userList;
        this.context = context;
        this.currentUserRole = currentUserRole;
        this.usersDB = FirebaseDatabase.getInstance().getReference("users");
        this.mFunctions = FirebaseFunctions.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Bind user data
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());

        // Remove any existing listener to prevent unwanted behavior
        holder.suspendSwitch.setOnCheckedChangeListener(null);

        // Set the switch state based on user's suspended status
        boolean isSuspended = user.getSuspended() != null && user.getSuspended();
        holder.suspendSwitch.setChecked(isSuspended);

        // Set switch text
        updateSwitchText(holder.suspendSwitch, isSuspended);

        // Handle Delete Button Visibility
        if ("admin".equalsIgnoreCase(currentUserRole)) {
            holder.deleteUserButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteUserButton.setVisibility(View.GONE);
        }

        // Define the listener for suspension
        holder.suspendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Get the current adapter position
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    Log.w(TAG, "onCheckedChanged: Invalid adapter position.");
                    return;
                }

                User currentUser = userList.get(adapterPosition);
                String userId = currentUser.getUserId();

                if (userId != null) {
                    // Update user status in Firebase
                    usersDB.child(userId).child("suspended").setValue(isChecked)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String action = isChecked ? "suspended" : "activated";
                                    Toast.makeText(context, "User " + action, Toast.LENGTH_SHORT).show();
                                    // Update the local list and notify the adapter
                                    currentUser.setSuspended(isChecked);
                                    updateSwitchText(holder.suspendSwitch, isChecked);
                                    notifyItemChanged(adapterPosition);
                                    Log.d(TAG, "User " + currentUser.getUsername() + " " + action + " successfully.");
                                } else {
                                    Toast.makeText(context, "Failed to update user status.", Toast.LENGTH_SHORT).show();
                                    // Revert the switch to its previous state
                                    holder.suspendSwitch.setOnCheckedChangeListener(null);
                                    holder.suspendSwitch.setChecked(!isChecked);
                                    holder.suspendSwitch.setOnCheckedChangeListener(this);
                                    Log.e(TAG, "Failed to update user status for " + currentUser.getUsername(), task.getException());
                                }
                            });
                } else {
                    Toast.makeText(context, "User ID not found.", Toast.LENGTH_SHORT).show();
                    // Revert the switch to its previous state
                    holder.suspendSwitch.setOnCheckedChangeListener(null);
                    holder.suspendSwitch.setChecked(!isChecked);
                    holder.suspendSwitch.setOnCheckedChangeListener(this);
                    Log.e(TAG, "User ID is null for position " + adapterPosition);
                }
            }
        });

        // Handle Delete Button Click
        holder.deleteUserButton.setOnClickListener(v -> {
            confirmDeleteUser(user, holder.getAdapterPosition());
        });
    }

    private void updateSwitchText(Switch suspendSwitch, boolean isSuspended) {
        if (isSuspended) {
            suspendSwitch.setText("Suspended");
        } else {
            suspendSwitch.setText("Active");
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * Confirm deletion of a user with an AlertDialog.
     * @param user The User object to delete.
     * @param position The position of the user in the list.
     */
    private void confirmDeleteUser(User user, int position) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete the user \"" + user.getUsername() + "\"?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteUser(user, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Delete the user from Firebase Realtime Database and Authentication.
     * @param user The User object to delete.
     * @param position The position of the user in the list.
     */
    private void deleteUser(User user, int position) {
        String userId = user.getUserId();
        if (userId == null) {
            Toast.makeText(context, "User ID not found.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "deleteUser: User ID is null for user " + user.getUsername());
            return;
        }

        // Remove user data from Realtime Database
        usersDB.child(userId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "User data deleted successfully.", Toast.LENGTH_SHORT).show();
                    userList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, userList.size());

                    // Proceed to delete user from Firebase Authentication via Cloud Function
                    deleteUserFromAuth(userId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "deleteUser: Failed to delete user data for " + user.getUsername(), e);
                });
    }

    /**
     * Invoke Cloud Function to delete user from Firebase Authentication.
     * @param userId The UID of the user to delete.
     */
    private void deleteUserFromAuth(String userId) {
        // Create data payload
        HashMap<String, Object> data = new HashMap<>();
        data.put("uid", userId);

        // Call the Cloud Function
        mFunctions
                .getHttpsCallable("deleteUser")
                .call(data)
                .addOnSuccessListener((HttpsCallableResult result) -> {
                    String message = (String) result.getData();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "deleteUserFromAuth: " + message);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete user from Authentication: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "deleteUserFromAuth: Error deleting user from Auth:", e);
                });
    }

    /**
     * Optionally, implement methods to update the user list dynamically.
     */
    public void updateList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username, email;
        Switch suspendSwitch;
        ImageButton deleteUserButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.usernameTextView);
            email = itemView.findViewById(R.id.emailTextView);
            suspendSwitch = itemView.findViewById(R.id.suspendSwitch);
            deleteUserButton = itemView.findViewById(R.id.deleteUserButton);
        }
    }
}