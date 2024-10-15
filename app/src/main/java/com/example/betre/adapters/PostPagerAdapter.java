package com.example.betre.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.example.betre.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        // Fetch user details from Realtime Database (existing functionality)
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(postOwnerId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the user name and email
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    holder.userName.setText(username);
                    holder.userEmail.setText(email);

                    // Fetch profile image from Firebase Storage (existing functionality)
                    StorageReference profileImageRef = FirebaseStorage.getInstance()
                            .getReference("profile_pictures/" + postOwnerId + ".jpg");

                    // Load profile image using Glide
                    Glide.with(context)
                            .load(profileImageRef)
                            .placeholder(R.drawable.ic_profile_placeholder) // Placeholder image
                            .into(holder.userProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors
            }
        });

        // Set post details (existing functionality)
        holder.likeCount.setText(String.valueOf(post.getCount_like()));
        holder.commentCount.setText(String.valueOf(post.getCount_comment()));
        holder.postDescription.setText(post.getContent());
        holder.postLocation.setText(post.getLocation());

        // Load post image from Firebase Storage using Glide (existing functionality)
        Glide.with(context)
                .load(post.getImageUrl())
                .placeholder(R.drawable.sign1)
                .into(holder.postImage);

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
                // Handle possible errors
            }
        });

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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
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
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, postImage;
        Button followButton;
        TextView userName, userEmail, postDescription, likeCount, commentCount, postLocation;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            followButton = itemView.findViewById(R.id.follow_button);
            postImage = itemView.findViewById(R.id.post_image);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            postDescription = itemView.findViewById(R.id.post_description);
            likeCount = itemView.findViewById(R.id.like_count);
            commentCount = itemView.findViewById(R.id.comment_count);
            postLocation = itemView.findViewById(R.id.post_location);
        }
    }
}
