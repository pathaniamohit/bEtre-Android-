//package com.example.betre.adapters;
//
//import android.content.Context;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.betre.Edit_Post_Fragment;
//import com.example.betre.R;
//import com.example.betre.models.Post;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.List;
//
//public class ImageAdapter_profile extends RecyclerView.Adapter<ImageAdapter_profile.ImageViewHolder> {
//
//    private Context context;
//    private List<Post> postList;
//    private boolean isOwner;
//    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
//    private static final long TRIPLE_CLICK_TIME_DELTA = 500;  // Time interval for detecting triple-click
//    private long lastClickTime = 0;
//    private int clickCount = 0;
//    private long startTime = 0;
//    private Handler handler;
//
//    public ImageAdapter_profile(Context context, List<Post> postList, boolean isOwner) {
//        this.context = context;
//        this.postList = postList;
//        this.isOwner = isOwner;
//        handler = new Handler();
//    }
//
//    @NonNull
//    @Override
//    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
//        return new ImageViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
//        Post post = postList.get(position);
//
//        if (post.getPostId() == null) {
//            Log.e("ImageAdapter_profile", "postId is null for position: " + position);
//        }
//        Log.e("ImageAdapter_profile", "postId : " + post.getPostId());
//
//
//        holder.displayUserInfoLayout.setVisibility(View.GONE);
//
//        Glide.with(context)
//                .load(post.getImageUrl())
//                .placeholder(R.drawable.sign1)
//                .into(holder.postImage);
//
//        holder.postDescription.setText(post.getContent());
//        holder.likeCount.setText(String.valueOf(post.getCount_like()));
//        holder.commentCount.setText(String.valueOf(post.getCount_comment()));
//        holder.postLocation.setText(post.getLocation());
//
//
//        holder.postImage.setOnClickListener(v -> {
//            long currentTime = System.currentTimeMillis();
//
//            if (startTime == 0) {
//                startTime = currentTime;
//            }
//
//            clickCount++;
//
//            handler.postDelayed(() -> {
//                if (clickCount == 2 && currentTime - startTime < DOUBLE_CLICK_TIME_DELTA) {
//                    // Handle double-click event (show delete dialog if owner)
//                    if (isOwner || post.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        showDeleteDialog(post);
//                    }
//                } else if (clickCount == 3 && currentTime - startTime < TRIPLE_CLICK_TIME_DELTA) {
//                    // Handle triple-click event (show confirmation dialog)
//                    if (isOwner || post.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//                        showConfirmationDialog(post);
//                    } else {
//                        Toast.makeText(context, "You are not the owner of this post", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                clickCount = 0;
//                startTime = 0;
//            }, TRIPLE_CLICK_TIME_DELTA);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return postList.size();
//    }
//
//    public static class ImageViewHolder extends RecyclerView.ViewHolder {
//        public ImageView postImage;
//        public TextView postDescription, likeCount, commentCount, postLocation;
//        public LinearLayout displayUserInfoLayout;
//
//        public ImageViewHolder(@NonNull View itemView) {
//            super(itemView);
//            postImage = itemView.findViewById(R.id.post_image);
//            postDescription = itemView.findViewById(R.id.post_description);
//            likeCount = itemView.findViewById(R.id.like_count);
//            commentCount = itemView.findViewById(R.id.comment_count);
//            postLocation = itemView.findViewById(R.id.post_location);
//            displayUserInfoLayout = itemView.findViewById(R.id.display_user_info);
//        }
//    }
//
//    private void showDeleteDialog(Post post) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Post Options")
//                .setItems(new String[]{"Delete"}, (dialog, which) -> {
//                    if (which == 0) {
//                        showDeleteConfirmationDialog(post);
//                    }
//                })
//                .show();
//    }
//
//    private void showDeleteConfirmationDialog(Post post) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Delete Post")
//                .setMessage("Are you sure you want to delete this post?")
//                .setPositiveButton("Yes", (dialog, which) -> deletePost(post))
//                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                .show();
//    }
//
//    private void deletePost(Post post) {
//        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
//        postsRef.child(post.getPostId()).removeValue()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
//                    postList.remove(post);
//                    notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(context, "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    // Show confirmation dialog on triple click
//    private void showConfirmationDialog(Post post) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Edit Post")
//                .setMessage("Do you want to edit this post?")
//                .setPositiveButton("Yes", (dialog, which) -> {
//                    openEditPostFragment(post.getPostId());
//                })
//                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
//                .show();
//    }
//
//    // Method to navigate to Edit_Post_Fragment
//    private void openEditPostFragment(String postId) {
//        if (postId != null && !postId.isEmpty()) {
//            Edit_Post_Fragment editPostFragment = Edit_Post_Fragment.newInstance(postId);
//            ((AppCompatActivity) context).getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.home_content, editPostFragment)
//                    .addToBackStack(null)
//                    .commit();
//        } else {
//            Log.e("ImageAdapter_profile", "postId is null or empty when opening Edit_Post_Fragment");
//        }
//    }
//}

package com.example.betre.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // For Follow/Unfollow
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betre.Edit_Post_Fragment;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageAdapter_profile extends RecyclerView.Adapter<ImageAdapter_profile.ImageViewHolder> {

    private Context context;
    private List<Post> postList;
    private boolean isOwner;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;
    private static final long TRIPLE_CLICK_TIME_DELTA = 500;  // Time interval for detecting triple-click
    private long lastClickTime = 0;
    private int clickCount = 0;
    private long startTime = 0;

    public ImageAdapter_profile(Context context, List<Post> postList, boolean isOwner) {
        this.context = context;
        this.postList = postList;
        this.isOwner = isOwner;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
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

                    holder.userName.setText(username != null ? username : "Username");
                    holder.userEmail.setText(email != null ? email : "Email");

                    // Load user profile image using Glide
                    Glide.with(context)
                            .load(imageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(holder.userProfileImage);
                } else {
                    holder.userName.setText("Username");
                    holder.userEmail.setText("Email");
                    holder.userProfileImage.setImageResource(R.drawable.ic_profile_placeholder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ImageAdapter_profile", "Error fetching user data: " + databaseError.getMessage());
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
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.likeIcon.setImageResource(R.drawable.ic_redlike);
                } else {
                    holder.likeIcon.setImageResource(R.drawable.ic_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ImageAdapter_profile", "Error fetching like status: " + error.getMessage());
            }
        });

        // Report post
        holder.reportIcon.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                openReportDialog(post.getPostId(), holder);
            } else {
                Log.e("ImageAdapter_profile", "Cannot report: postId is null");
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
                Log.e("ImageAdapter_profile", "Cannot like: postId is null");
            }
        });

        // Reference to the following node for the current user
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(currentUserId).child(postOwnerId);

        // Check if the current user is already following the post owner
        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {
                    holder.followButton.setText("Unfollow");
                } else {
                    holder.followButton.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ImageAdapter_profile", "Error checking follow status: " + databaseError.getMessage());
            }
        });

        // Follow/Unfollow button click listener
        holder.followButton.setOnClickListener(v -> {
            followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {
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
                    Log.e("ImageAdapter_profile", "Error changing follow status: " + databaseError.getMessage());
                }
            });
        });

        // Comment icon click listener to open comment dialog
        holder.commentIcon.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                openCommentPopup(post.getPostId(), holder);
            } else {
                Log.e("ImageAdapter_profile", "Cannot add comment: postId is null");
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, postImage, commentIcon, likeIcon, reportIcon;
        Button followButton;
        LinearLayout userProfileLayout;
        TextView userName, userEmail, postDescription, likeCount, commentCount, postLocation;

        public ImageViewHolder(@NonNull View itemView) {
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

    private void openReportDialog(String postId, ImageViewHolder holder) {
        if (postId == null) {
            Log.e("ImageAdapter_profile", "Cannot open report dialog: postId is null");
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
    private void submitReportToFirebase(String postId, String reportReason, ImageViewHolder holder) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reportcomment");
        String reportId = reportRef.push().getKey();

        if (reportId == null) {
            Toast.makeText(context, "Failed to report post. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        reportData.put("postId", postId);
        reportData.put("reportedBy", currentUserId);
        reportData.put("reportReason", reportReason);
        reportData.put("reportTimestamp", System.currentTimeMillis());

        // Optionally, you can add more details like post content, user info, etc.

        reportRef.child(reportId).setValue(reportData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ImageAdapter_profile", "Report successfully submitted.");
                    markPostAsReported(postId, holder);
                })
                .addOnFailureListener(e -> {
                    Log.e("ImageAdapter_profile", "Failed to submit report: " + e.getMessage());
                    Toast.makeText(context, "Failed to report post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void markPostAsReported(String postId, ImageViewHolder holder) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);

        postRef.child("is_reported").setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ImageAdapter_profile", "Post marked as reported.");
                Toast.makeText(context, "Post reported", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("ImageAdapter_profile", "Failed to mark post as reported: " + task.getException().getMessage());
                Toast.makeText(context, "Failed to report post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to handle like/unlike functionality
    private void handleLikeClick(String postId, ImageViewHolder holder, String currentUserId) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes")
                .child(postId).child(currentUserId);

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
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
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ImageAdapter_profile", "Error toggling like: " + error.getMessage());
            }
        });
    }

    // Increment the like count for a post
    private void incrementLikeCount(String postId, ImageViewHolder holder) {
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
                    Log.e("ImageAdapter_profile", "Error incrementing like count: " + databaseError.getMessage());
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
    private void decrementLikeCount(String postId, ImageViewHolder holder) {
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
                    Log.e("ImageAdapter_profile", "Error decrementing like count: " + databaseError.getMessage());
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
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String postOwnerId = snapshot.child("userId").getValue(String.class);
                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (postOwnerId != null && !postOwnerId.equals(currentUserId)) {
                        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications").child(postOwnerId);
                        String notificationId = notificationsRef.push().getKey();

                        HashMap<String, Object> notificationMap = new HashMap<>();
                        notificationMap.put("type", "like");
                        notificationMap.put("userId", currentUserId);  // Current user's ID
                        notificationMap.put("postId", postId);
                        notificationMap.put("timestamp", System.currentTimeMillis());

                        if (notificationId != null) {
                            notificationsRef.child(notificationId).setValue(notificationMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ImageAdapter_profile", "Error notifying user: " + error.getMessage());
            }
        });
    }

    private void openCommentPopup(String postId, ImageViewHolder holder) {
        if (postId == null) {
            Log.e("ImageAdapter_profile", "Cannot open comment popup: postId is null");
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
        CommentAdapter commentAdapter = new CommentAdapter(context, commentList, postId);
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

    // Function to load comments
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
                        comment.setCommentId(commentSnapshot.getKey());
                        commentList.add(comment);
                    }
                }
                // Notify the adapter that the data has changed
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ImageAdapter_profile", "Error loading comments: " + databaseError.getMessage());
            }
        });
    }

    // Function to add comment to Firebase
    private void addCommentToFirebase(String postId, String commentText, ImageViewHolder holder) {
        if (postId == null) {
            Log.e("ImageAdapter_profile", "Cannot add comment: postId is null");
            return;
        }

        Log.d("ImageAdapter_profile", "Adding comment for postId: " + postId);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                if (username == null) {
                    username = "Anonymous";
                }
                Log.d("ImageAdapter_profile", "Retrieved username: " + username);

                DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments").child(postId);
                String commentId = commentsRef.push().getKey();  // Generate a new comment ID
                Log.d("ImageAdapter_profile", "Generated commentId: " + commentId);

                if (commentId == null) {
                    Toast.makeText(context, "Failed to add comment. Try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Comment comment = new Comment(commentId, currentUserId, username, commentText, System.currentTimeMillis());

                commentsRef.child(commentId).setValue(comment).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ImageAdapter_profile", "Comment successfully added.");
                        incrementCommentCount(postId, holder);
                        // Notify the post owner about the new comment
                        notifyUserAboutComment(postId, currentUserId, commentText);
                    } else {
                        Log.e("ImageAdapter_profile", "Failed to add comment: " + task.getException().getMessage());
                        Toast.makeText(context, "Failed to add comment: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ImageAdapter_profile", "Error adding comment: " + databaseError.getMessage());
                Toast.makeText(context, "Failed to add comment: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void notifyUserAboutComment(String postId, String commenterUserId, String commentText) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String postOwnerId = snapshot.child("userId").getValue(String.class);
                    String commenterUsername = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    if (commenterUsername == null) {
                        commenterUsername = "Anonymous";
                    }

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

                        if(notificationId != null){
                            notificationsRef.child(notificationId).setValue(notificationMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ImageAdapter_profile", "Error notifying user: " + error.getMessage());
            }
        });
    }

    // Function to increment comment count
    private void incrementCommentCount(String postId, ImageViewHolder holder) {
        if (postId == null) {
            Log.e("ImageAdapter_profile", "Cannot increment comment count: postId is null");
            return;
        }

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);

        postRef.child("count_comment").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentCount = mutableData.getValue(Integer.class);
                if (currentCount == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentCount + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.e("ImageAdapter_profile", "Error incrementing comment count: " + databaseError.getMessage());
                } else {
                    Integer updatedCount = dataSnapshot.getValue(Integer.class);
                    if (updatedCount != null) {
                        holder.commentCount.setText(String.valueOf(updatedCount));  // Update the comment count in the UI
                    }
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
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String unfollowingUsername = snapshot.child("username").getValue(String.class);  // Get the unfollower's username
                if (unfollowingUsername == null) {
                    unfollowingUsername = "Anonymous";
                }

                // Create the notification structure
                HashMap<String, Object> notificationMap = new HashMap<>();
                notificationMap.put("type", "unfollow");
                notificationMap.put("userId", currentUserId);
                notificationMap.put("username", unfollowingUsername);
                notificationMap.put("timestamp", System.currentTimeMillis());

                if(notificationId != null){
                    notificationsRef.child(notificationId).setValue(notificationMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ImageAdapter_profile", "Error retrieving user data for unfollow: " + error.getMessage());
            }
        });
    }

    // Function to open the Edit_Post_Fragment
    private void openEditPostFragment(String postId) {
        if (postId != null && !postId.isEmpty()) {
            Edit_Post_Fragment editPostFragment = Edit_Post_Fragment.newInstance(postId);
            ((AppCompatActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_content, editPostFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Log.e("ImageAdapter_profile", "postId is null or empty when opening Edit_Post_Fragment");
        }
    }

    // Function to show delete dialog
    private void showDeleteDialog(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Post Options")
                .setItems(new String[]{"Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        showDeleteConfirmationDialog(post);
                    }
                })
                .show();
    }

    // Show delete confirmation dialog
    private void showDeleteConfirmationDialog(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes", (dialog, which) -> deletePost(post))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Function to delete post
    private void deletePost(Post post) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        postsRef.child(post.getPostId()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                    postList.remove(post);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete post: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Function to handle reporting posts (already included above)

}
