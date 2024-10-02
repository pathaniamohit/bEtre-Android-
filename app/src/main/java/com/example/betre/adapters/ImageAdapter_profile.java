package com.example.betre.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.example.betre.models.Post;

import java.util.List;

public class ImageAdapter_profile extends RecyclerView.Adapter<ImageAdapter_profile.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public ImageAdapter_profile(Context context, List<Post> postList) {
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

        holder.userName.setText(post.getUserName());
        holder.userEmail.setText(post.getUserEmail());
        holder.displayUserInfoLayout.setVisibility(View.GONE);

        Glide.with(context)
                .load(post.getUserProfileImage())
                .placeholder(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(holder.userProfileImage);

        Glide.with(context)
                .load(post.getImageUrl())
                .placeholder(R.drawable.sign1)
                .into(holder.postImage);

        holder.likeCount.setText(String.valueOf(post.getCount_like()));
        holder.commentCount.setText(String.valueOf(post.getCount_comment()));

        holder.postLocation.setText(post.getLocation());

        holder.postDescription.setText(post.getContent());

        holder.followButton.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, postImage;
        TextView userName, userEmail, postDescription, likeCount, commentCount, postLocation;
        Button followButton;
        public LinearLayout displayUserInfoLayout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            postImage = itemView.findViewById(R.id.post_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            postDescription = itemView.findViewById(R.id.post_description);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            postLocation = itemView.findViewById(R.id.post_location);
            followButton = itemView.findViewById(R.id.follow_button);
            displayUserInfoLayout = itemView.findViewById(R.id.display_user_info);

        }
    }
}
