package com.example.betre.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.example.betre.UserProfileFragment;
import com.example.betre.models.Comment;
import com.example.betre.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostPagerAdapter extends RecyclerView.Adapter<PostPagerAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public PostPagerAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        String postOwnerId = post.getUserId();  // User ID of the person who made the post
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user details from Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(postOwnerId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String imageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    holder.userName.setText(username);
                    holder.userEmail.setText(email);

                    // Load user profile image using Glide
                    Glide.with(context)
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(holder.userProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error fetching user data: " + databaseError.getMessage());
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
                .placeholder(R.drawable.sign1)
                .into(holder.postImage);

        // Check if the user has liked the post and set the correct like icon
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes")
                .child(post.getPostId()).child(currentUserId);

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    holder.likeIcon.setImageResource(R.drawable.ic_redlike);
                } else {
                    holder.likeIcon.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error fetching like status: " + databaseError.getMessage());
            }
        });

        //report user
        holder.reportIcon.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                openReportDialog(post.getPostId(), holder);
            } else {
                Log.e("PostPagerAdapter", "Cannot report: postId is null");
            }
        });

        // Set OnClickListener for the user profile layout to open UserProfileFragment
        holder.userProfileImage.setOnClickListener(v -> {
            openUserProfileFragment(postOwnerId);
        });

        holder.userProfileLayout.setOnClickListener(v -> {
            openUserProfileFragment(postOwnerId);
        });


        holder.likeIcon.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                handleLikeClick(post.getPostId(), holder, currentUserId);
            } else {
                Log.e("PostPagerAdapter", "Cannot like: postId is null");
            }
        });

        // Reference to the following node for the current user
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(currentUserId).child(postOwnerId);

        // Check if the current user is already following the post owner
        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class)) {
                    holder.followButton.setText("Unfollow");
                } else {
                    holder.followButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error checking follow status: " + databaseError.getMessage());
            }
        });

        // Follow/Unfollow button click listener
        holder.followButton.setOnClickListener(v -> {
            followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getValue(Boolean.class)) {
                        unfollowUser(currentUserId, postOwnerId);
                        holder.followButton.setText("Follow");
                    } else {
                        followUser(currentUserId, postOwnerId);
                        holder.followButton.setText("Unfollow");
                        notifyUserAboutFollow(postOwnerId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PostPagerAdapter", "Error changing follow status: " + databaseError.getMessage());
                }
            });
        });

        // Comment icon click listener to open comment dialog
        holder.commentIcon.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                openCommentPopup(post.getPostId(), holder);
            } else {
                Log.e("PostPagerAdapter", "Cannot add comment: postId is null");
            }
        });
    }

    // Notify user about follow
    private void notifyUserAboutFollow(String followedUserId) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(followedUserId);
        String notificationId = notificationsRef.push().getKey();

        HashMap<String, Object> notificationMap = new HashMap<>();
        notificationMap.put("type", "follow");
        notificationMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        notificationMap.put("timestamp", System.currentTimeMillis());

        notificationsRef.child(notificationId).setValue(notificationMap);
    }

    private void openUserProfileFragment(String userId) {
        UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(userId);

        ((AppCompatActivity) context).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_content, userProfileFragment)
                .addToBackStack(null)
                .commit();
    }


    private void openReportDialog(String postId, PostViewHolder holder) {
        if (postId == null) {
            Log.e("PostPagerAdapter", "Cannot open report dialog: postId is null");
            return;
        }

        // Create the report dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Post");

        final EditText input = new EditText(context);
        input.setHint("Enter reason for reporting (optional)");
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reportReason = input.getText().toString().trim();
            if (reportReason.isEmpty()) {
                reportReason = "Inappropriate content";
            }
            submitReportToFirebase(postId, reportReason, holder);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Submit report to Firebase
    private void submitReportToFirebase(String postId, String reportReason, PostViewHolder holder) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(postId).child(currentUserId);

        reportRef.setValue(reportReason).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PostPagerAdapter", "Report successfully submitted.");
                markPostAsReported(postId, holder);
            } else {
                Log.e("PostPagerAdapter", "Failed to submit report: " + task.getException().getMessage());
            }
        });
    }

    private void markPostAsReported(String postId, PostViewHolder holder) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);

        postRef.child("is_reported").setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("PostPagerAdapter", "Post marked as reported.");
                Toast.makeText(context, "Post reported", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("PostPagerAdapter", "Failed to mark post as reported: " + task.getException().getMessage());
            }
        });
    }

    // Function to handle like/unlike functionality
    private void handleLikeClick(String postId, PostViewHolder holder, String currentUserId) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes")
                .child(postId).child(currentUserId);

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    likeRef.removeValue();
                    decrementLikeCount(postId, holder);
                    holder.likeIcon.setImageResource(R.drawable.ic_like);
                } else {
                    likeRef.setValue(true);
                    incrementLikeCount(postId, holder);
                    holder.likeIcon.setImageResource(R.drawable.ic_redlike);
                    notifyUserAboutLike(postId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error toggling like: " + databaseError.getMessage());
            }
        });
    }

    // Increment the like count for a post
    private void incrementLikeCount(String postId, PostViewHolder holder) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.child("count_like").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentCount = mutableData.getValue(Integer.class);
                if (currentCount == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentCount + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e("PostPagerAdapter", "Error incrementing like count: " + databaseError.getMessage());
                } else {
                    Integer updatedCount = dataSnapshot.getValue(Integer.class);
                    if (updatedCount != null) {
                        holder.likeCount.setText(String.valueOf(updatedCount));
                    }
                }
            }
        });
    }

    // Decrement the like count for a post
    private void decrementLikeCount(String postId, PostViewHolder holder) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.child("count_like").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentCount = mutableData.getValue(Integer.class);
                if (currentCount != null && currentCount > 0) {
                    mutableData.setValue(currentCount - 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e("PostPagerAdapter", "Error decrementing like count: " + databaseError.getMessage());
                } else {
                    Integer updatedCount = dataSnapshot.getValue(Integer.class);
                    if (updatedCount != null) {
                        holder.likeCount.setText(String.valueOf(updatedCount));
                    }
                }
            }
        });
    }

    // Notify post owner about the like
    private void notifyUserAboutLike(String postId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String postOwnerId = dataSnapshot.child("userId").getValue(String.class);
                    if (postOwnerId != null && !postOwnerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(postOwnerId);
                        String notificationId = notificationsRef.push().getKey();

                        HashMap<String, Object> notificationMap = new HashMap<>();
                        notificationMap.put("type", "like");
                        notificationMap.put("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());  // Current user's ID
                        notificationMap.put("postId", postId);
                        notificationMap.put("timestamp", System.currentTimeMillis());

                        notificationsRef.child(notificationId).setValue(notificationMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error notifying user: " + databaseError.getMessage());
            }
        });
    }

    private void openCommentPopup(String postId, PostViewHolder holder) {
        if (postId == null) {
            Log.e("PostPagerAdapter", "Cannot open comment popup: postId is null");
            return;
        }

        // Inflate custom comment dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Initialize views in the dialog
        RecyclerView commentRecyclerView = dialogView.findViewById(R.id.commentRecyclerView);
        EditText commentInput = dialogView.findViewById(R.id.commentInput);
        ImageView sendButton = dialogView.findViewById(R.id.sendButton);

        // Setup RecyclerView for displaying comments
        List<Comment> commentList = new ArrayList<>();
        CommentAdapter commentAdapter = new CommentAdapter(context, commentList);
        commentRecyclerView.setAdapter(commentAdapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Fetch existing comments for the post
        loadComments(postId, commentAdapter, commentList);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle sending new comments
        sendButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addCommentToFirebase(postId, commentText, holder);
                commentInput.setText("");
            }
        });
    }
//loads comments
    private void loadComments(String postId, CommentAdapter commentAdapter, List<Comment> commentList) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId);

        // Listen for changes in the comments node
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();  // Clear the existing list to avoid duplications
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        commentList.add(comment);
                    }
                }
                // Notify the adapter that the data has changed
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error loading comments: " + databaseError.getMessage());
            }
        });
    }


    // Function to add comment to Firebase
    private void addCommentToFirebase(String postId, String commentText, PostViewHolder holder) {
        if (postId == null) {
            Log.e("PostPagerAdapter", "Cannot add comment: postId is null");
            return;
        }

        Log.d("PostPagerAdapter", "Adding comment for postId: " + postId);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                Log.d("PostPagerAdapter", "Retrieved username: " + username);

                DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId);
                String commentId = commentsRef.push().getKey();  // Generate a new comment ID
                Log.d("PostPagerAdapter", "Generated commentId: " + commentId);

                Comment comment = new Comment(currentUserId, username, commentText, System.currentTimeMillis());
                commentsRef.child(commentId).setValue(comment).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("PostPagerAdapter", "Comment successfully added.");
                        incrementCommentCount(postId, holder);
                        // Notify the post owner about the new comment
                        notifyUserAboutComment(postId, currentUserId, commentText);
                    } else {
                        Log.e("PostPagerAdapter", "Failed to add comment: " + task.getException().getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error adding comment: " + databaseError.getMessage());
                Toast.makeText(context, "Failed to add comment: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void notifyUserAboutComment(String postId, String commenterUserId, String commentText) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String postOwnerId = dataSnapshot.child("userId").getValue(String.class);
                    String commenterUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                    if (postOwnerId != null && !postOwnerId.equals(commenterUserId)) {
                        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(postOwnerId);
                        String notificationId = notificationsRef.push().getKey();

                        HashMap<String, Object> notificationMap = new HashMap<>();
                        notificationMap.put("type", "comment");
                        notificationMap.put("userId", commenterUserId);
                        notificationMap.put("username", commenterUsername);
                        notificationMap.put("content", commentText);
                        notificationMap.put("postId", postId);
                        notificationMap.put("timestamp", System.currentTimeMillis());

                        notificationsRef.child(notificationId).setValue(notificationMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error notifying user: " + databaseError.getMessage());
            }
        });
    }

    // Function to increment comment count
    private void incrementCommentCount(String postId, PostViewHolder holder) {
        if (postId == null) {
            Log.e("PostPagerAdapter", "Cannot increment comment count: postId is null");
            return;
        }

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);

        postRef.child("count_comment").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentCount = mutableData.getValue(Integer.class);
                if (currentCount == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentCount + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e("PostPagerAdapter", "Error incrementing comment count: " + databaseError.getMessage());
                } else {
                    Integer updatedCount = dataSnapshot.getValue(Integer.class);
                    Log.d("PostPagerAdapter", "Updated comment count: " + updatedCount);
                    holder.commentCount.setText(String.valueOf(updatedCount));  // Update the comment count in the UI
                }
            }
        });
    }

    private void followUser(String currentUserId, String postOwnerId) {
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("followers")
                .child(postOwnerId).child(currentUserId);
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(currentUserId).child(postOwnerId);

        followersRef.setValue(true);
        followingRef.setValue(true);
    }

    private void unfollowUser(String currentUserId, String postOwnerId) {
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("followers")
                .child(postOwnerId).child(currentUserId);
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(currentUserId).child(postOwnerId);

        followersRef.removeValue();
        followingRef.removeValue();

        notifyUserAboutUnfollow(currentUserId, postOwnerId);
    }

    // Notify user about unfollow
    private void notifyUserAboutUnfollow(String currentUserId, String unfollowedUserId) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(unfollowedUserId);
        String notificationId = notificationsRef.push().getKey();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String unfollowingUsername = dataSnapshot.child("username").getValue(String.class);  // Get the unfollower's username

                // Create the notification structure
                HashMap<String, Object> notificationMap = new HashMap<>();
                notificationMap.put("type", "unfollow");
                notificationMap.put("userId", currentUserId);
                notificationMap.put("username", unfollowingUsername);
                notificationMap.put("timestamp", System.currentTimeMillis());

                // Notification text will be handled in the NotificationAdapter (Unfollow message)
                notificationsRef.child(notificationId).setValue(notificationMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error retrieving user data for unfollow: " + databaseError.getMessage());
            }
        });
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, postImage, commentIcon, likeIcon, reportIcon;
        Button followButton;
        LinearLayout userProfileLayout;
        TextView userName, userEmail, postDescription, likeCount, commentCount, postLocation;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            followButton = itemView.findViewById(R.id.follow_button);
            postImage = itemView.findViewById(R.id.post_image);
            commentIcon = itemView.findViewById(R.id.comment_icon);
            likeIcon = itemView.findViewById(R.id.like_icon);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            postDescription = itemView.findViewById(R.id.post_description);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            postLocation = itemView.findViewById(R.id.post_location);
            reportIcon = itemView.findViewById(R.id.report_icon);
            userProfileLayout = itemView.findViewById(R.id.user_info);

        }
    }
}
