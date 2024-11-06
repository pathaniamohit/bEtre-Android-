package com.example.betre.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.example.betre.models.Comment;
import com.example.betre.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostPagerAdapter extends RecyclerView.Adapter<PostPagerAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        String postOwnerId = post.getUserId();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Load user details
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(postOwnerId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    holder.userName.setText(username != null ? username : "Username");

                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);
                    Glide.with(context)
                            .load(profileImageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .into(holder.userProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostPagerAdapter", "Error loading user data: " + databaseError.getMessage());
            }
        });

        // Set up post image
        Glide.with(context).load(post.getImageUrl()).placeholder(R.drawable.sign1).into(holder.postImage);

        // Like functionality
        holder.likeIcon.setOnClickListener(v -> toggleLike(post.getPostId(), postOwnerId, holder, currentUserId));

        // Comment functionality
        holder.commentIcon.setOnClickListener(v -> {
            if (post.getPostId() != null) {
                openCommentView(post.getPostId());
            } else {
                Log.e("PostPagerAdapter", "Cannot open comments: postId is null");
            }
        });

        // Check follow status and update the button text
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(currentUserId).child(postOwnerId);
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
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostPagerAdapter", "Error checking follow status: " + error.getMessage());
            }
        });

        // Report functionality
        holder.reportIcon.setOnClickListener(v -> openReportDialog(post.getPostId()));
        holder.followButton.setOnClickListener(v -> toggleFollow(postOwnerId, holder));
        // Display post details
        holder.postDescription.setText(post.getContent());
        holder.likeCount.setText(String.valueOf(post.getCount_like()));
        holder.commentCount.setText(String.valueOf(post.getCount_comment()));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, postImage, likeIcon, commentIcon, reportIcon;
        TextView userName, postDescription, likeCount, commentCount;
        Button followButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            postImage = itemView.findViewById(R.id.post_image);
            likeIcon = itemView.findViewById(R.id.like_icon);
            commentIcon = itemView.findViewById(R.id.comment_icon);
            reportIcon = itemView.findViewById(R.id.report_icon);
            userName = itemView.findViewById(R.id.user_name);
            postDescription = itemView.findViewById(R.id.post_description);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            followButton = itemView.findViewById(R.id.follow_button); // Assume this button is in the layout
        }
    }

    private void toggleFollow(String postOwnerId, PostViewHolder holder) {
        DatabaseReference followingRef = FirebaseDatabase.getInstance().getReference("following")
                .child(currentUserId).child(postOwnerId);
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference("followers")
                .child(postOwnerId).child(currentUserId);

        followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && Boolean.TRUE.equals(snapshot.getValue(Boolean.class))) {
                    // Unfollow
                    followingRef.setValue(false);
                    followersRef.setValue(false);
                    holder.followButton.setText("Follow");
                    Toast.makeText(context, "Unfollowed", Toast.LENGTH_SHORT).show();
                } else {
                    // Follow
                    followingRef.setValue(true);
                    followersRef.setValue(true);
                    holder.followButton.setText("Unfollow");
                    Toast.makeText(context, "Followed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostPagerAdapter", "Error toggling follow: " + error.getMessage());
            }
        });
    }

    private void toggleLike(String postId, String postOwnerId, PostViewHolder holder, String currentUserId) {
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("likes")
                .child(postId).child("users").child(currentUserId);

        likeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    likeRef.removeValue();
                    updateLikeCount(postId, false, holder);
                } else {
                    likeRef.child("likedAt").setValue(System.currentTimeMillis());
                    FirebaseDatabase.getInstance().getReference("likes").child(postId)
                            .child("ownerId").setValue(postOwnerId);
                    updateLikeCount(postId, true, holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostPagerAdapter", "Error toggling like: " + error.getMessage());
            }
        });
    }

    private void updateLikeCount(String postId, boolean increment, PostViewHolder holder) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("count_like");
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer count = currentData.getValue(Integer.class);
                if (count == null) count = 0;
                currentData.setValue(increment ? count + 1 : Math.max(count - 1, 0));
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (error == null && snapshot.getValue(Integer.class) != null) {
                    holder.likeCount.setText(snapshot.getValue(Integer.class).toString());
                }
            }
        });
    }

    private void openCommentDialog(String postId, PostViewHolder holder) {
        // Code for opening comment dialog, fetching existing comments,
        // adding new comments, and updating the comment count
    }

    private void openReportDialog(String postId) {
        EditText input = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle("Report Post")
                .setMessage("Enter the reason for reporting this post.")
                .setView(input)
                .setPositiveButton("Submit", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    submitPostReport(postId, reason.isEmpty() ? "Inappropriate content" : reason);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitPostReport(String postId, String reason) {
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").push();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Retrieve post details to add more context in the report
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String content = snapshot.child("content").getValue(String.class);

                    HashMap<String, Object> reportData = new HashMap<>();
                    reportData.put("postId", postId);
                    reportData.put("reportedBy", currentUserId);
                    reportData.put("reason", reason);
                    reportData.put("timestamp", System.currentTimeMillis());
                    reportData.put("status", "pending");
                    reportData.put("content", content != null ? content : "No content provided");

                    // Save report to 'reports' node
                    reportRef.setValue(reportData)
                            .addOnSuccessListener(aVoid -> Toast.makeText(context, "Report submitted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Log.e("PostPagerAdapter", "Failed to submit report: " + e.getMessage()));
                } else {
                    Log.e("PostPagerAdapter", "Failed to retrieve post content for report.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostPagerAdapter", "Error fetching post details: " + error.getMessage());
            }
        });
    }

    private void openCommentView(String postId) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Initialize views in the dialog
        RecyclerView commentRecyclerView = dialogView.findViewById(R.id.commentRecyclerView);
        EditText commentInput = dialogView.findViewById(R.id.commentInput);
        ImageView sendButton = dialogView.findViewById(R.id.sendButton);

        // Set up RecyclerView for comments
        List<Comment> commentList = new ArrayList<>();
        CommentAdapter commentAdapter = new CommentAdapter(context, commentList, postId);  // Make sure CommentAdapter is implemented
        commentRecyclerView.setAdapter(commentAdapter);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Load comments for the post
        loadComments(postId, commentAdapter, commentList);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle sending new comments
        sendButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addCommentToFirebase(postId, commentText);
                commentInput.setText("");
            }
        });
    }

    private void loadComments(String postId, CommentAdapter commentAdapter, List<Comment> commentList) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments");

        // Query to filter comments by post_Id
        commentsRef.orderByChild("post_Id").equalTo(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();  // Clear the existing list to avoid duplication
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    // Map each snapshot to the Comment class
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        comment.setCommentId(commentSnapshot.getKey());  // Set comment ID if needed
                        commentList.add(comment);
                    }
                }
                // Notify the adapter that the data has changed
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostPagerAdapter", "Error loading comments: " + error.getMessage());
            }
        });
    }

    private void addCommentToFirebase(String postId, String commentText) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments");

        // Generate a unique ID for the comment
        String commentId = commentsRef.push().getKey();

        // Retrieve the username of the current user to include it in the comment
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                if (username == null) {
                    username = "Anonymous";
                }

                // Create a comment object with the required fields
                HashMap<String, Object> commentData = new HashMap<>();
                commentData.put("content", commentText);
                commentData.put("timestamp", System.currentTimeMillis());
                commentData.put("userId", currentUserId);
                commentData.put("username", username);
                commentData.put("post_Id", postId);

                // Save the comment data under its unique ID in the comments node
                if (commentId != null) {
                    commentsRef.child(commentId).setValue(commentData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("PostPagerAdapter", "Comment added successfully");
                        } else {
                            Log.e("PostPagerAdapter", "Failed to add comment: " + task.getException().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PostPagerAdapter", "Error fetching username: " + error.getMessage());
            }
        });
    }

}
