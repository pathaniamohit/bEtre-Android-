package com.example.betre;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserAnalyticsFragment extends Fragment {

    private static final String TAG = "UserAnalyticsFragment";
    private String userId;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private TextView usernameTextView, total_comments_value_got, photosCount, commentsCountTextView, likesCountTextView, emailTextView, followersCountTextView, followingCountTextView;
    private ImageView profilePictureImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userId = getArguments().getString("user_id");
            Log.d(TAG, "Received user_id: " + userId);
        } else {
            Log.e(TAG, "No user_id passed to the fragment.");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_analytics, container, false);

        profilePictureImageView = view.findViewById(R.id.profile_picture);
        usernameTextView = view.findViewById(R.id.profile_name);
        emailTextView = view.findViewById(R.id.profile_email);
        followersCountTextView = view.findViewById(R.id.total_followers_value);
        followingCountTextView = view.findViewById(R.id.total_followings_value);
        likesCountTextView = view.findViewById(R.id.total_likes_value);
        photosCount = view.findViewById(R.id.total_posts_value);
        commentsCountTextView = view.findViewById(R.id.total_comments_value);
        total_comments_value_got = view.findViewById(R.id.total_comments_value_got);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        if (userId != null) {
            fetchUserDetails();
            fetchFollowersCount();
            fetchFollowingCount();
            fetchUserLikesCount(userId);
            fetchUserPostsCount(userId);
            fetchUserCommentsCount(userId);
            fetchUserCommentsCountGot(userId);
        } else {
            Toast.makeText(getContext(), "User ID not found.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchUserCommentsCountGot(String selectedUserId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments");
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        int[] commentsCountGot = {0};

        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot commentsSnapshot) {
                for (DataSnapshot commentSnapshot : commentsSnapshot.getChildren()) {
                    String postId = commentSnapshot.child("post_Id").getValue(String.class);
                    if (postId != null) {
                        postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                String postUserId = postSnapshot.child("userId").getValue(String.class);
                                if (selectedUserId.equals(postUserId)) {
                                    commentsCountGot[0]++;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("Firebase", "Failed to fetch post details: " + databaseError.getMessage());
                            }
                        });
                    }
                }

//                total_comments_value_got.setText();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Log.d("CommentsCount", "Total matching comments: " + commentsCountGot[0]);
                    total_comments_value_got.setText(String.valueOf(commentsCountGot[0]));
                }, 2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to fetch comments: " + databaseError.getMessage());
            }
        });
    }

    private void fetchUserDetails() {
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String profilePicturePath = snapshot.child("users").getValue(String.class);

                    usernameTextView.setText(username != null ? username : "N/A");
                    emailTextView.setText(email != null ? email : "N/A");
                    Log.d(TAG, "Username: " + username);
                    Log.d(TAG, "Email: " + email);
                    Log.d(TAG, "Profile picture path: " + profilePicturePath);

                    fetchProfilePictureFromStorage();

                    Log.d(TAG, "User details fetched successfully.");
                } else {
                    Log.e(TAG, "User details not found for userId: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching user details: " + error.getMessage());
            }
        });
    }

    private void fetchProfilePictureFromStorage() {
        String profilePicturePath = "users/" + userId + "/profile.jpg";

        StorageReference profilePictureRef = storageReference.child(profilePicturePath);

        profilePictureRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(requireContext())
                    .load(uri)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(profilePictureImageView);

            Log.d(TAG, "Profile picture loaded successfully: " + uri.toString());
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading profile picture: " + e.getMessage());
            profilePictureImageView.setImageResource(R.drawable.ic_profile_placeholder);
        });
    }

    private void fetchFollowersCount() {
        databaseReference.child("followers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long followersCount = snapshot.getChildrenCount();

                followersCountTextView.setText(String.valueOf(followersCount));

                Log.d(TAG, "Followers count fetched: " + followersCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching followers count: " + error.getMessage());
            }
        });
    }

    private void fetchFollowingCount() {
        databaseReference.child("following").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long followingCount = snapshot.getChildrenCount();

                followingCountTextView.setText(String.valueOf(followingCount));

                Log.d(TAG, "Following count fetched: " + followingCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching following count: " + error.getMessage());
            }
        });
    }

    private void fetchUserLikesCount(String userId) {
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference("likes");

        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long likesCount = 0;

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String ownerId = postSnapshot.child("ownerId").getValue(String.class);

                    if (userId.equals(ownerId)) {
                        likesCount++;
                    }
                }

                Log.d("UserLikes", "Total likes by user: " + likesCount);
                likesCountTextView.setText(String.valueOf(likesCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserLikes", "Error fetching likes: " + error.getMessage());
            }
        });
    }

    private void fetchUserPostsCount(String userId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int postCount = 0;

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String postUserId = postSnapshot.child("userId").getValue(String.class);

                    if (userId.equals(postUserId)) {
                        postCount++;
                    }
                }

                Log.d("UserPosts", "Total posts by user: " + postCount);
                photosCount.setText(String.valueOf(postCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserPosts", "Error fetching posts: " + error.getMessage());
            }
        });
    }

    private void fetchUserCommentsCount(String userId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments");

        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commentCount = 0;

                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    String commentUserId = commentSnapshot.child("userId").getValue(String.class);

                    if (userId.equals(commentUserId)) {
                        commentCount++;
                    }
                }

                Log.d("UserComments", "Total comments by user: " + commentCount);
                commentsCountTextView.setText(String.valueOf(commentCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserComments", "Error fetching comments: " + error.getMessage());
            }
        });
    }

}
