package com.example.betre;

import android.os.Bundle;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betre.adapters.ImageAdapter_profile;
import com.example.betre.models.Post;
import com.example.betre.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserProfileFragment extends Fragment {
    private static final String TAG = "UserProfileFragment";

    private ImageView profileImage;
    private TextView profileName, profileEmail, photosCount, followersCount, followsCount;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView postsRecyclerView;
    private ImageAdapter_profile postAdapter;
    private List<Post> postList;
    private String userId;
    ImageView back_button;


    public UserProfileFragment() {}

    public static UserProfileFragment newInstance(String userId) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);  // Pass userId to fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");  // Get userId passed to the fragment
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);  // Inflate user profile layout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profile_picture);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        photosCount = view.findViewById(R.id.photos_count);
        followersCount = view.findViewById(R.id.followers_count);
        followsCount = view.findViewById(R.id.follows_count);
        postsRecyclerView = view.findViewById(R.id.images_grid);
        back_button = view.findViewById(R.id.back_button);


        postsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        postList = new ArrayList<>();
        postAdapter = new ImageAdapter_profile(getContext(), postList, false);
        postsRecyclerView.setAdapter(postAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Load the user's profile and data based on the passed userId
        if (userId != null) {
            loadUserProfile(userId);
            loadUserPosts(userId);
            loadPhotosCount(userId);
            loadFollowersCount(userId);
            loadFollowsCount(userId);
        } else {
            Log.w(TAG, "No userId passed to UserProfileFragment.");
        }

//        back_button.setOnClickListener(v -> {
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.home_content, new Fragment())
//                    .addToBackStack(null)
//                    .commit();
//        });

        back_button.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

    }

    private void loadUserProfile(String userId) {
        Log.d(TAG, "loadUserProfile: Fetching user profile data for userId: " + userId);
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        profileName.setText(user.getUsername() != null ? user.getUsername() : "Username");
                        profileEmail.setText(user.getEmail() != null ? user.getEmail() : "Email");

                        // Load profile image (if exists)
                        Glide.with(UserProfileFragment.this)
                                .load(user.getProfileImageUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(profileImage);
                    }
                } else {
                    showToast("User data not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch user data: " + databaseError.getMessage());
                showToast("Failed to fetch user data");
            }
        });
    }

    private void loadPhotosCount(String userId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    count++;
                }
                photosCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load photos count: " + error.getMessage());
            }
        });
    }

    private void loadFollowersCount(String userId) {
        mDatabase.child("followers").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                followersCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch followers count: " + error.getMessage());
            }
        });
    }

    private void loadFollowsCount(String userId) {
        mDatabase.child("following").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = (int) snapshot.getChildrenCount();
                followsCount.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch follows count: " + error.getMessage());
            }
        });
    }

    private void loadUserPosts(String userId) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        postsRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading posts: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}


