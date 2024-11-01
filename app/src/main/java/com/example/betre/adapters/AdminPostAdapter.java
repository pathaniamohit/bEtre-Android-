package com.example.betre.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.example.betre.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

/**
 * Adapter for admin to manage posts.
 */
public class AdminPostAdapter extends RecyclerView.Adapter<AdminPostAdapter.AdminPostViewHolder> {

    private Context context;
    private List<Post> postList;
    private DatabaseReference usersReference;

    public AdminPostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.usersReference = FirebaseDatabase.getInstance().getReference("users");
    }

    @NonNull
    @Override
    public AdminPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_admin, parent, false);
        return new AdminPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPostViewHolder holder, int position) {
        Post post = postList.get(position);
        String postOwnerId = post.getUserId();  // User ID of the person who made the post

        // Fetch user details from Realtime Database
        usersReference.child(postOwnerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String imageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    holder.userName.setText(username != null ? username : "Unknown User");
                    holder.userEmail.setText(email != null ? email : "No Email");

                    // Load user profile image using Glide
                    Glide.with(context)
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(holder.userProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AdminPostAdapter", "Error fetching user data: " + error.getMessage());
            }
        });

        // Set post details
        holder.likeCount.setText(String.valueOf(post.getCount_like()));
        holder.commentCount.setText(String.valueOf(post.getCount_comment()));
        holder.postDescription.setText(post.getContent());
        holder.postLocation.setText(post.getLocation());

        // Load post image using Glide
        Glide.with(context)
                .load(post.getImageUrl())
//                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.postImage);

        // Handle Delete Button
        holder.btnDelete.setOnClickListener(v -> {
            confirmDeletePost(post.getPostId(), position);
        });

        // Handle Edit Button
        holder.btnEdit.setOnClickListener(v -> {
            openEditPostDialog(post.getPostId(), holder.postDescription, post);
        });

        // Handle Warn Button
        holder.btnWarn.setOnClickListener(v -> {
            openWarnUserDialog(post.getUserId(), post.getPostId());
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    /**
     * Confirm deletion of a post with an AlertDialog.
     * @param postId The ID of the post to delete.
     * @param position The position of the post in the list.
     */
    private void confirmDeletePost(String postId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deletePost(postId, position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Delete the post from Firebase Realtime Database.
     * @param postId The ID of the post to delete.
     * @param position The position of the post in the list.
     */
    private void deletePost(String postId, int position) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Post deleted successfully.", Toast.LENGTH_SHORT).show();
                    postList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, postList.size());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Open a dialog to edit the post description.
     * @param postId The ID of the post to edit.
     * @param postDescriptionView The TextView displaying the post description.
     * @param post The Post object.
     */
    private void openEditPostDialog(String postId, TextView postDescriptionView, Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Post");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(post.getContent());
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedContent = input.getText().toString().trim();
            if(!updatedContent.isEmpty()){
                updatePostDescription(postId, updatedContent, postDescriptionView, post);
            } else {
                Toast.makeText(context, "Post description cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Update the post description in Firebase Realtime Database.
     * @param postId The ID of the post to update.
     * @param updatedContent The new description.
     * @param postDescriptionView The TextView displaying the post description.
     * @param post The Post object.
     */
    private void updatePostDescription(String postId, String updatedContent, TextView postDescriptionView, Post post) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.child("content").setValue(updatedContent)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Post updated successfully.", Toast.LENGTH_SHORT).show();
                    postDescriptionView.setText(updatedContent);
                    post.setContent(updatedContent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to update post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Open a dialog to warn the user for a specific post.
     * @param userId The ID of the user to warn.
     * @param postId The ID of the post.
     */
    private void openWarnUserDialog(String userId, String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Warn User");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("Enter warning message");
        builder.setView(input);

        builder.setPositiveButton("Warn", (dialog, which) -> {
            String warningMessage = input.getText().toString().trim();
            if(!warningMessage.isEmpty()){
                warnUser(userId, postId, warningMessage);
            } else {
                Toast.makeText(context, "Warning message cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Warn the user by updating their warning count and sending a notification.
     * @param userId The ID of the user to warn.
     * @param postId The ID of the post associated with the warning.
     * @param warningMessage The warning message.
     */
    private void warnUser(String userId, String postId, String warningMessage) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("warnings");
//        userRef.runTransaction(new com.google.firebase.database.Transaction.Handler() {
//            @NonNull
//            @Override
//            public com.google.firebase.database.Transaction.Result doTransaction(@NonNull com.google.firebase.database.MutableData mutableData) {
//                Integer currentWarnings = mutableData.getValue(Integer.class);
//                if(currentWarnings == null){
//                    mutableData.setValue(1);
//                } else {
//                    mutableData.setValue(currentWarnings + 1);
//                }
//                return com.google.firebase.database.Transaction.success(mutableData);
//            }
        userRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentWarnings = mutableData.getValue(Integer.class);
                if(currentWarnings == null){
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentWarnings + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if(error != null){
                    Toast.makeText(context, "Failed to warn user: " + error.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    sendWarningNotification(userId, warningMessage, postId);
                    Toast.makeText(context, "User warned successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Send a warning notification to the user.
     * @param userId The ID of the user to notify.
     * @param warningMessage The warning message.
     * @param postId The ID of the post associated with the warning.
     */
    private void sendWarningNotification(String userId, String warningMessage, String postId) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId);
        String notificationId = notificationsRef.push().getKey();

        // Get admin's user ID (assuming only one admin, else modify accordingly)
        String adminUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create the notification structure
        HashMap<String, Object> notificationMap = new HashMap<>();

        notificationMap.put("type", "warning");
        notificationMap.put("userId", adminUserId);
        notificationMap.put("message", warningMessage);
        notificationMap.put("postId", postId);
        notificationMap.put("timestamp", System.currentTimeMillis());

        notificationsRef.child(notificationId).setValue(notificationMap)
                .addOnSuccessListener(aVoid -> {
                    // Notification sent successfully
                    Log.d("AdminPostAdapter", "Warning notification sent to user: " + userId);
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminPostAdapter", "Failed to send warning notification: " + e.getMessage());
                });
    }

    /**
     * ViewHolder class for admin post items.
     */
    public class AdminPostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, postImage;
        TextView userName, userEmail, postDescription, postLocation, likeCount, commentCount;
        ImageButton btnEdit, btnDelete, btnWarn;

        public AdminPostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            postImage = itemView.findViewById(R.id.post_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            postDescription = itemView.findViewById(R.id.post_description);
            postLocation = itemView.findViewById(R.id.post_location);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnWarn = itemView.findViewById(R.id.btnWarn);
        }
    }
}

