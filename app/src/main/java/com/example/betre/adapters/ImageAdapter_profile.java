package com.example.betre.adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betre.Edit_Post_Fragment;
import com.example.betre.R;
import com.example.betre.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private Handler handler;

    public ImageAdapter_profile(Context context, List<Post> postList, boolean isOwner) {
        this.context = context;
        this.postList = postList;
        this.isOwner = isOwner;
        handler = new Handler();
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

        if (post.getPostId() == null) {
            Log.e("ImageAdapter_profile", "postId is null for position: " + position);
        }
        Log.e("ImageAdapter_profile", "postId : " + post.getPostId());


        holder.displayUserInfoLayout.setVisibility(View.GONE);

        Glide.with(context)
                .load(post.getImageUrl())
                .placeholder(R.drawable.sign1)
                .into(holder.postImage);

        holder.postDescription.setText(post.getContent());
        holder.likeCount.setText(String.valueOf(post.getCount_like()));
        holder.commentCount.setText(String.valueOf(post.getCount_comment()));
        holder.postLocation.setText(post.getLocation());

        holder.postImage.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();

            if (startTime == 0) {
                startTime = currentTime;
            }

            clickCount++;

            handler.postDelayed(() -> {
                if (clickCount == 2 && currentTime - startTime < DOUBLE_CLICK_TIME_DELTA) {
                    // Handle double-click event (show delete dialog if owner)
                    if (isOwner || post.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        showDeleteDialog(post);
                    }
                } else if (clickCount == 3 && currentTime - startTime < TRIPLE_CLICK_TIME_DELTA) {
                    // Handle triple-click event (show confirmation dialog)
                    if (isOwner || post.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        showConfirmationDialog(post);
                    } else {
                        Toast.makeText(context, "You are not the owner of this post", Toast.LENGTH_SHORT).show();
                    }
                }
                clickCount = 0;
                startTime = 0;
            }, TRIPLE_CLICK_TIME_DELTA);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView postImage;
        public TextView postDescription, likeCount, commentCount, postLocation;
        public LinearLayout displayUserInfoLayout;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            postDescription = itemView.findViewById(R.id.post_description);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            postLocation = itemView.findViewById(R.id.post_location);
            displayUserInfoLayout = itemView.findViewById(R.id.display_user_info);
        }
    }

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

    private void showDeleteConfirmationDialog(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Yes", (dialog, which) -> deletePost(post))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

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

    // Show confirmation dialog on triple click
    private void showConfirmationDialog(Post post) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Post")
                .setMessage("Do you want to edit this post?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    openEditPostFragment(post.getPostId());
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to navigate to Edit_Post_Fragment
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
}
