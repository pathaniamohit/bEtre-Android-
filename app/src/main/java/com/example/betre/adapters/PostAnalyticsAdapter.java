package com.example.betre.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.example.betre.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAnalyticsAdapter extends RecyclerView.Adapter<PostAnalyticsAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;
    private static final String TAG = "PostAnalyticsAdapter";
    private DatabaseReference usersRef;

    public PostAnalyticsAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.usersRef = FirebaseDatabase.getInstance().getReference("users"); // Reference to users data
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_analytics, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Set post details
        holder.postTitle.setText(post.getContent());
        holder.postLikes.setText("Likes: " + post.getCount_like());
        holder.postComments.setText("Comments: " + post.getCount_comment());

        // Fetch user details based on userId
        if (post.getUserId() != null) {
            usersRef.child(post.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.child("username").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);

                        holder.postUsername.setText(username != null ? username : "Unknown User");
                        holder.postEmail.setText(email != null ? email : "No Email");
                    } else {
                        holder.postUsername.setText("Unknown User");
                        holder.postEmail.setText("No Email");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Error fetching user details: " + error.getMessage());
                    Toast.makeText(context, "Failed to load user details", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            holder.postUsername.setText("Unknown User");
            holder.postEmail.setText("No Email");
        }

        // Load image using Glide with system placeholder images
        Glide.with(context)
                .load(post.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_report_image) // Default placeholder image
                .error(android.R.drawable.ic_dialog_alert) // Default error image
                .into(holder.postImage);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postLikes, postComments, postUsername, postEmail;
        ImageView postImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.post_title);
            postLikes = itemView.findViewById(R.id.post_likes);
            postComments = itemView.findViewById(R.id.post_comments);
            postImage = itemView.findViewById(R.id.post_image);
            postUsername = itemView.findViewById(R.id.post_username);
            postEmail = itemView.findViewById(R.id.post_email);
        }
    }
}
