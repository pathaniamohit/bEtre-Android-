//////package com.example.betre.adapters;
////////
////////import android.content.Context;
////////import android.view.LayoutInflater;
////////import android.view.View;
////////import android.view.ViewGroup;
////////import android.widget.TextView;
////////
////////import androidx.annotation.NonNull;
////////import androidx.recyclerview.widget.RecyclerView;
////////
////////import com.example.betre.R;
////////import com.example.betre.models.User;
////////
////////import java.util.List;
////////
////////public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
////////    private List<User> userList;
////////    private Context context;
////////
////////    public UserAdapter(List<User> userList, Context context) {
////////        this.userList = userList;
////////        this.context = context;
////////    }
////////
////////    @NonNull
////////    @Override
////////    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////////        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
////////        return new UserViewHolder(view);
////////    }
////////
////////    @Override
////////    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
////////        User user = userList.get(position);
////////        holder.username.setText(user.getUsername());
////////        holder.email.setText(user.getEmail());
//////////        holder.phone.setText(user.getPhone());
//////////        holder.gender.setText(user.getGender());
////////    }
////////
////////    @Override
////////    public int getItemCount() {
////////        return userList.size();
////////    }
////////
////////    static class UserViewHolder extends RecyclerView.ViewHolder {
////////        TextView username, email, phone, gender;
////////
////////        public UserViewHolder(@NonNull View itemView) {
////////            super(itemView);
////////            username = itemView.findViewById(R.id.usernameTextView);
////////            email = itemView.findViewById(R.id.emailTextView);
//////////            phone = itemView.findViewById(R.id.phoneTextView);
//////////            gender = itemView.findViewById(R.id.genderTextView);
////////        }
////////    }
////////}
////////
//////import android.content.Context;
//////import android.view.LayoutInflater;
//////import android.view.View;
//////import android.view.ViewGroup;
//////import android.widget.TextView;
//////import androidx.annotation.NonNull;
//////import androidx.recyclerview.widget.RecyclerView;
//////import com.example.betre.R;
//////import com.example.betre.models.User;
//////import java.util.List;
//////
//////public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
//////    private List<User> userList;
//////    private Context context;
//////
//////    public UserAdapter(List<User> userList, Context context) {
//////        this.userList = userList;
//////        this.context = context;
//////    }
//////
//////    @NonNull
//////    @Override
//////    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//////        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
//////        return new UserViewHolder(view);
//////    }
//////
//////    @Override
//////    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
//////        User user = userList.get(position);
//////        holder.username.setText(user.getUsername());
//////        holder.email.setText(user.getEmail());
//////    }
//////
//////    @Override
//////    public int getItemCount() {
//////        return userList.size();
//////    }
//////
//////    public void updateList(List<User> newList) {
//////        userList = newList;
//////        notifyDataSetChanged();
//////    }
//////
//////    static class UserViewHolder extends RecyclerView.ViewHolder {
//////        TextView username, email;
//////
//////        public UserViewHolder(@NonNull View itemView) {
//////            super(itemView);
//////            username = itemView.findViewById(R.id.usernameTextView);
//////            email = itemView.findViewById(R.id.emailTextView);
//////        }
//////    }
//////}
////package com.example.betre.adapters;
////
////import android.content.Context;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.Switch;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.recyclerview.widget.RecyclerView;
////import com.example.betre.R;
////import com.example.betre.models.User;
////import com.google.firebase.database.DatabaseReference;
////import com.google.firebase.database.FirebaseDatabase;
////
////import java.util.List;
////
////import android.widget.CompoundButton;
////
////
////public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
////    private List<User> userList;
////    private Context context;
////    private DatabaseReference usersDB;
////
////    public UserAdapter(List<User> userList, Context context) {
////        this.userList = userList;
////        this.context = context;
////        usersDB = FirebaseDatabase.getInstance().getReference("users");
////    }
////
////    @NonNull
////    @Override
////    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
////        return new UserViewHolder(view);
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
////        User user = userList.get(position);
////
////        holder.username.setText(user.getUsername());
////        holder.email.setText(user.getEmail());
////
////        // Set the switch state based on user's suspended status
////        boolean isSuspended = user.getSuspended();
////        holder.suspendSwitch.setChecked(isSuspended);
////
////        // Set switch text
////        updateSwitchText(holder.suspendSwitch, isSuspended);
////
////        // Remove any existing listener to prevent unwanted behavior
////        holder.suspendSwitch.setOnCheckedChangeListener(null);
////
////        // Define the listener
////        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
////            boolean isListenerActive = true;
////
////            @Override
////            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
////                if (!isListenerActive) return; // Prevent recursion
////
////                // Update the suspended status in Firebase
////                String userId = user.getUserId();
////                if (userId == null) {
////                    // If userId is null, you can retrieve it from the adapter's data
////                    userId = getUserIdByPosition(position);
////                }
////
////                if (userId != null) {
////                    usersDB.child(userId).child("suspended").setValue(isChecked)
////                            .addOnCompleteListener(task -> {
////                                if (task.isSuccessful()) {
////                                    Toast.makeText(context, "User " + (isChecked ? "suspended" : "activated"), Toast.LENGTH_SHORT).show();
////                                    user.setSuspended(isChecked);
////                                    updateSwitchText(holder.suspendSwitch, isChecked);
////                                } else {
////                                    Toast.makeText(context, "Failed to update user status.", Toast.LENGTH_SHORT).show();
////
////                                    // Revert the switch to its previous state
////                                    isListenerActive = false; // Temporarily deactivate listener
////                                    holder.suspendSwitch.setChecked(!isChecked);
////                                    isListenerActive = true; // Reactivate listener
////                                }
////                            });
////                } else {
////                    Toast.makeText(context, "User ID not found.", Toast.LENGTH_SHORT).show();
////                    // Revert the switch to its previous state
////                    isListenerActive = false; // Temporarily deactivate listener
////                    holder.suspendSwitch.setChecked(!isChecked);
////                    isListenerActive = true; // Reactivate listener
////                }
////            }
////        };
////
////        // Set the listener
////        holder.suspendSwitch.setOnCheckedChangeListener(listener);
////    }
////
////    private void updateSwitchText(Switch suspendSwitch, boolean isSuspended) {
////        if (isSuspended) {
////            suspendSwitch.setText("Suspended");
////        } else {
////            suspendSwitch.setText("Active");
////        }
////    }
////
////    // Helper method to retrieve user ID if not stored in User object
////    private String getUserIdByPosition(int position) {
////        // Implement this method based on how you store user IDs
////        // For example, if you have a map of user IDs to positions
////        return userList.get(position).getUserId();
////    }
////
////    @Override
////    public int getItemCount() {
////        return userList.size();
////    }
////
////    public void updateList(List<User> newList) {
////        userList = newList;
////        notifyDataSetChanged();
////    }
////
////    static class UserViewHolder extends RecyclerView.ViewHolder {
////        TextView username, email;
////        Switch suspendSwitch;
////
////        public UserViewHolder(@NonNull View itemView) {
////            super(itemView);
////            username = itemView.findViewById(R.id.usernameTextView);
////            email = itemView.findViewById(R.id.emailTextView);
////            suspendSwitch = itemView.findViewById(R.id.suspendSwitch);
////        }
////    }
////}
//
//// File: app/src/main/java/com/example/betre/adapters/UserAdapter.java
//package com.example.betre.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.betre.R;
//import com.example.betre.models.User;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.List;
//
//import android.widget.CompoundButton;
//
//public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
//    private List<User> userList;
//    private Context context;
//    private DatabaseReference usersDB;
//
//    public UserAdapter(List<User> userList, Context context) {
//        this.userList = userList;
//        this.context = context;
//        usersDB = FirebaseDatabase.getInstance().getReference("users");
//    }
//
//    @NonNull
//    @Override
//    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
//        return new UserViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
//        // It's better to use holder.getAdapterPosition() within listeners
//        User user = userList.get(position);
//
//        holder.username.setText(user.getUsername());
//        holder.email.setText(user.getEmail());
//
//        // Set the switch state based on user's suspended status
//        boolean isSuspended = user.getSuspended() != null && user.getSuspended();
//        holder.suspendSwitch.setChecked(isSuspended);
//
//        // Set switch text
//        updateSwitchText(holder.suspendSwitch, isSuspended);
//
//        // Remove any existing listener to prevent unwanted behavior
//        holder.suspendSwitch.setOnCheckedChangeListener(null);
//
//        // Define the listener
//        holder.suspendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            boolean isListenerActive = true;
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isListenerActive) return; // Prevent recursion
//
//                int currentPosition = holder.getAdapterPosition();
//                if (currentPosition == RecyclerView.NO_POSITION) {
//                    // Item has been removed or moved
//                    return;
//                }
//
//                User currentUser = userList.get(currentPosition);
//                String userId = currentUser.getUserId();
//
//                if (userId != null) {
//                    usersDB.child(userId).child("suspended").setValue(isChecked)
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(context, "User " + (isChecked ? "suspended" : "activated"), Toast.LENGTH_SHORT).show();
//                                    currentUser.setSuspended(isChecked);
//                                    updateSwitchText(holder.suspendSwitch, isChecked);
//                                } else {
//                                    Toast.makeText(context, "Failed to update user status.", Toast.LENGTH_SHORT).show();
//
//                                    // Revert the switch to its previous state
//                                    isListenerActive = false; // Temporarily deactivate listener
//                                    holder.suspendSwitch.setChecked(!isChecked);
//                                    isListenerActive = true; // Reactivate listener
//                                }
//                            });
//                } else {
//                    Toast.makeText(context, "User ID not found.", Toast.LENGTH_SHORT).show();
//                    // Revert the switch to its previous state
//                    isListenerActive = false; // Temporarily deactivate listener
//                    holder.suspendSwitch.setChecked(!isChecked);
//                    isListenerActive = true; // Reactivate listener
//                }
//            }
//        });
//    }
//
//    private void updateSwitchText(Switch suspendSwitch, boolean isSuspended) {
//        if (isSuspended) {
//            suspendSwitch.setText("Suspended");
//        } else {
//            suspendSwitch.setText("Active");
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return userList.size();
//    }
//
//    /**
//     * Optionally, implement methods to update the user list dynamically.
//     */
//    public void updateList(List<User> newList) {
//        userList = newList;
//        notifyDataSetChanged();
//    }
//
//    static class UserViewHolder extends RecyclerView.ViewHolder {
//        TextView username, email;
//        Switch suspendSwitch;
//
//        public UserViewHolder(@NonNull View itemView) {
//            super(itemView);
//            username = itemView.findViewById(R.id.usernameTextView);
//            email = itemView.findViewById(R.id.emailTextView);
//            suspendSwitch = itemView.findViewById(R.id.suspendSwitch);
//        }
//    }
//}
//package com.example.betre.adapters;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CompoundButton;
//import android.widget.ImageButton;
//import android.widget.Switch;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.betre.R;
//import com.example.betre.models.User;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.functions.FirebaseFunctions;
//import com.google.firebase.functions.HttpsCallableResult;
//
//import java.util.HashMap;
//import java.util.List;
//
//public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
//    private List<User> userList;
//    private Context context;
//    private DatabaseReference usersDB;
//    private String currentUserRole; // To determine if the current user is an admin
//    private FirebaseFunctions mFunctions;
//
//    public UserAdapter(List<User> userList, Context context, String currentUserRole) {
//        this.userList = userList;
//        this.context = context;
//        this.currentUserRole = currentUserRole;
//        usersDB = FirebaseDatabase.getInstance().getReference("users");
//        mFunctions = FirebaseFunctions.getInstance();
//    }
//
//    @NonNull
//    @Override
//    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
//        return new UserViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
//        // It's better to use holder.getAdapterPosition() within listeners
//        User user = userList.get(position);
//
//        holder.username.setText(user.getUsername());
//        holder.email.setText(user.getEmail());
//
//        // Set the switch state based on user's suspended status
//        boolean isSuspended = user.getSuspended() != null && user.getSuspended();
//        holder.suspendSwitch.setChecked(isSuspended);
//
//        // Set switch text
//        updateSwitchText(holder.suspendSwitch, isSuspended);
//
//        // Handle Delete Button Visibility
//        if ("admin".equalsIgnoreCase(currentUserRole)) {
//            holder.deleteUserButton.setVisibility(View.VISIBLE);
//        } else {
//            holder.deleteUserButton.setVisibility(View.GONE);
//        }
//
//        // Remove any existing listener to prevent unwanted behavior
//        holder.suspendSwitch.setOnCheckedChangeListener(null);
//
//        // Define the listener for suspension
//        holder.suspendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            boolean isListenerActive = true;
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isListenerActive) return; // Prevent recursion
//
//                int currentPosition = holder.getAdapterPosition();
//                if (currentPosition == RecyclerView.NO_POSITION) {
//                    // Item has been removed or moved
//                    return;
//                }
//
//                User currentUser = userList.get(currentPosition);
//                String userId = currentUser.getUserId();
//
//                if (userId != null) {
//                    usersDB.child(userId).child("suspended").setValue(isChecked)
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(context, "User " + (isChecked ? "suspended" : "activated"), Toast.LENGTH_SHORT).show();
//                                    currentUser.setSuspended(isChecked);
//                                    updateSwitchText(holder.suspendSwitch, isChecked);
//                                } else {
//                                    Toast.makeText(context, "Failed to update user status.", Toast.LENGTH_SHORT).show();
//
//                                    // Revert the switch to its previous state
//                                    isListenerActive = false; // Temporarily deactivate listener
//                                    holder.suspendSwitch.setChecked(!isChecked);
//                                    isListenerActive = true; // Reactivate listener
//                                }
//                            });
//                } else {
//                    Toast.makeText(context, "User ID not found.", Toast.LENGTH_SHORT).show();
//                    // Revert the switch to its previous state
//                    isListenerActive = false; // Temporarily deactivate listener
//                    holder.suspendSwitch.setChecked(!isChecked);
//                    isListenerActive = true; // Reactivate listener
//                }
//            }
//        });
//
//        // Handle Delete Button Click
//        holder.deleteUserButton.setOnClickListener(v -> {
//            confirmDeleteUser(user, holder.getAdapterPosition());
//        });
//    }
//
//    private void updateSwitchText(Switch suspendSwitch, boolean isSuspended) {
//        if (isSuspended) {
//            suspendSwitch.setText("Suspended");
//        } else {
//            suspendSwitch.setText("Active");
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return userList.size();
//    }
//
//    /**
//     * Confirm deletion of a user with an AlertDialog.
//     * @param user The User object to delete.
//     * @param position The position of the user in the list.
//     */
//    private void confirmDeleteUser(User user, int position) {
//        new androidx.appcompat.app.AlertDialog.Builder(context)
//                .setTitle("Delete User")
//                .setMessage("Are you sure you want to delete the user \"" + user.getUsername() + "\"?")
//                .setPositiveButton("Yes", (dialog, which) -> {
//                    deleteUser(user, position);
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }
//
//    /**
//     * Delete the user from Firebase Realtime Database and Authentication.
//     * @param user The User object to delete.
//     * @param position The position of the user in the list.
//     */
//    private void deleteUser(User user, int position) {
//        String userId = user.getUserId();
//        if (userId == null) {
//            Toast.makeText(context, "User ID not found.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Remove user data from Realtime Database
//        usersDB.child(userId).removeValue()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(context, "User data deleted successfully.", Toast.LENGTH_SHORT).show();
//                    userList.remove(position);
//                    notifyItemRemoved(position);
//                    notifyItemRangeChanged(position, userList.size());
//
//                    // Proceed to delete user from Firebase Authentication via Cloud Function
//                    deleteUserFromAuth(userId);
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(context, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                });
//    }
//
//    /**
//     * Invoke Cloud Function to delete user from Firebase Authentication.
//     * @param userId The UID of the user to delete.
//     */
//    private void deleteUserFromAuth(String userId) {
//        // Create data payload
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("uid", userId);
//
//        // Call the Cloud Function
//        mFunctions
//                .getHttpsCallable("deleteUser")
//                .call(data)
//                .addOnSuccessListener((HttpsCallableResult result) -> {
//                    String message = (String) result.getData();
//                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(context, "Failed to delete user from Authentication: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    Log.e("UserAdapter", "Error deleting user from Auth:", e);
//                });
//    }
//
//    /**
//     * Optionally, implement methods to update the user list dynamically.
//     */
//    public void updateList(List<User> newList) {
//        userList = newList;
//        notifyDataSetChanged();
//    }
//
//    static class UserViewHolder extends RecyclerView.ViewHolder {
//        TextView username, email;
//        Switch suspendSwitch;
//        ImageButton deleteUserButton;
//
//        public UserViewHolder(@NonNull View itemView) {
//            super(itemView);
//            username = itemView.findViewById(R.id.usernameTextView);
//            email = itemView.findViewById(R.id.emailTextView);
//            suspendSwitch = itemView.findViewById(R.id.suspendSwitch);
//            deleteUserButton = itemView.findViewById(R.id.deleteUserButton);
//        }
//    }
//}


// File: app/src/main/java/com/example/betre/adapters/UserAdapter.java
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

    public UserAdapter(List<User> userList, Context context, String currentUserRole) {
        this.userList = userList;
        this.context = context;
        this.currentUserRole = currentUserRole;
        usersDB = FirebaseDatabase.getInstance().getReference("users");
        mFunctions = FirebaseFunctions.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final int currentPosition = position;
        User user = userList.get(currentPosition);

        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());

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

        // Remove any existing listener to prevent unwanted behavior
        holder.suspendSwitch.setOnCheckedChangeListener(null);

        // Define the listener for suspension
        holder.suspendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean isListenerActive = true;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isListenerActive) return; // Prevent recursion

                String userId = user.getUserId();

                if (userId != null) {
                    usersDB.child(userId).child("suspended").setValue(isChecked)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "User " + (isChecked ? "suspended" : "activated"), Toast.LENGTH_SHORT).show();
                                    user.setSuspended(isChecked);
                                    updateSwitchText(holder.suspendSwitch, isChecked);
                                } else {
                                    Toast.makeText(context, "Failed to update user status.", Toast.LENGTH_SHORT).show();

                                    // Revert the switch to its previous state
                                    isListenerActive = false; // Temporarily deactivate listener
                                    holder.suspendSwitch.setChecked(!isChecked);
                                    isListenerActive = true; // Reactivate listener
                                }
                            });
                } else {
                    Toast.makeText(context, "User ID not found.", Toast.LENGTH_SHORT).show();
                    // Revert the switch to its previous state
                    isListenerActive = false; // Temporarily deactivate listener
                    holder.suspendSwitch.setChecked(!isChecked);
                    isListenerActive = true; // Reactivate listener
                }
            }
        });

        // Handle Delete Button Click
        holder.deleteUserButton.setOnClickListener(v -> {
            confirmDeleteUser(user, currentPosition);
        });
    }

//    @Override
//    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
//        // Retrieve the current adapter position
//        int currentPosition = holder.getAdapterPosition();
//        if (currentPosition == RecyclerView.NO_POSITION) {
//            return; // Item has been removed or moved
//        }
//
//        User user = userList.get(currentPosition);
//
//        holder.username.setText(user.getUsername());
//        holder.email.setText(user.getEmail());
//
//        // Set the switch state based on user's suspended status
//        boolean isSuspended = user.getSuspended() != null && user.getSuspended();
//        holder.suspendSwitch.setChecked(isSuspended);
//
//        // Set switch text
//        updateSwitchText(holder.suspendSwitch, isSuspended);
//
//        // Handle Delete Button Visibility
//        if ("admin".equalsIgnoreCase(currentUserRole)) {
//            holder.deleteUserButton.setVisibility(View.VISIBLE);
//        } else {
//            holder.deleteUserButton.setVisibility(View.GONE);
//        }
//
//        // Remove any existing listener to prevent unwanted behavior
//        holder.suspendSwitch.setOnCheckedChangeListener(null);
//
//        // Define the listener for suspension
//        holder.suspendSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            boolean isListenerActive = true;
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (!isListenerActive) return; // Prevent recursion
//
//                User currentUser = userList.get(holder.getAdapterPosition());
//                String userId = currentUser.getUserId();
//
//                if (userId != null) {
//                    usersDB.child(userId).child("suspended").setValue(isChecked)
//                            .addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(context, "User " + (isChecked ? "suspended" : "activated"), Toast.LENGTH_SHORT).show();
//                                    currentUser.setSuspended(isChecked);
//                                    updateSwitchText(holder.suspendSwitch, isChecked);
//                                } else {
//                                    Toast.makeText(context, "Failed to update user status.", Toast.LENGTH_SHORT).show();
//
//                                    // Revert the switch to its previous state
//                                    isListenerActive = false; // Temporarily deactivate listener
//                                    holder.suspendSwitch.setChecked(!isChecked);
//                                    isListenerActive = true; // Reactivate listener
//                                }
//                            });
//                } else {
//                    Toast.makeText(context, "User ID not found.", Toast.LENGTH_SHORT).show();
//                    // Revert the switch to its previous state
//                    isListenerActive = false; // Temporarily deactivate listener
//                    holder.suspendSwitch.setChecked(!isChecked);
//                    isListenerActive = true; // Reactivate listener
//                }
//            }
//        });
//
//        // Handle Delete Button Click
//        holder.deleteUserButton.setOnClickListener(v -> {
//            confirmDeleteUser(user, holder.getAdapterPosition());
//        });
//    }

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
//    private void confirmDeleteUser(User user, int position) {
//        new androidx.appcompat.app.AlertDialog.Builder(context)
//                .setTitle("Delete User")
//                .setMessage("Are you sure you want to delete the user \"" + user.getUsername() + "\"?")
//                .setPositiveButton("Yes", (dialog, which) -> {
//                    deleteUser(user, position);
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }

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
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete user from Authentication: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("UserAdapter", "Error deleting user from Auth:", e);
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
