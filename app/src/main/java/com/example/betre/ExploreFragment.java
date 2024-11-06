package com.example.betre;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.betre.adapters.PostPagerAdapter;
import com.example.betre.models.Post;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExploreFragment extends Fragment {

    private ViewPager2 viewPager;
    private PostPagerAdapter postPagerAdapter;
    private List<Post> postList;

    private DatabaseReference postsReference;
    private DatabaseReference followingReference;
    private ValueEventListener postsListener;

    public ExploreFragment() {
        // Required empty public constructor
    }

    public static ExploreFragment newInstance(String param1, String param2) {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase and App Check
        FirebaseApp.initializeApp(requireContext());
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        // Initialize the reference to the "posts" and "following" in Realtime Database
        postsReference = FirebaseDatabase.getInstance().getReference("posts");
        followingReference = FirebaseDatabase.getInstance().getReference("following");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        postList = new ArrayList<>();
        postPagerAdapter = new PostPagerAdapter(getContext(), postList, false);
        viewPager.setAdapter(postPagerAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPosts();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachListener();
    }

    private void loadPosts() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // First, get the list of user IDs that the current user is following
        followingReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot followingSnapshot) {
                List<String> followingUserIds = new ArrayList<>();
                for (DataSnapshot userSnapshot : followingSnapshot.getChildren()) {
                    Boolean isFollowing = userSnapshot.getValue(Boolean.class);
                    if (isFollowing != null && isFollowing) {
                        followingUserIds.add(userSnapshot.getKey());
                    }
                }

                // Check if the user follows any others
                if (followingUserIds.isEmpty()) {
                    // Display all posts if not following anyone, except the user's own posts
                    loadAllPostsExceptCurrentUser(currentUserId);
                } else {
                    // Display posts only from followed users
                    loadPostsFromFollowedUsers(followingUserIds);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadAllPostsExceptCurrentUser(String currentUserId) {
        postsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);

                    // Check if the post exists and is not from the current user
                    if (post != null && !post.getUserId().equals(currentUserId)) {
                        post.setPostId(postSnapshot.getKey());
                        postList.add(post);
                    }
                }
                Collections.shuffle(postList);
                postPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        postsReference.addListenerForSingleValueEvent(postsListener);
    }

    private void loadPostsFromFollowedUsers(List<String> followingUserIds) {
        postsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);

                    // Check if the post exists and is from a followed user
                    if (post != null && followingUserIds.contains(post.getUserId())) {
                        post.setPostId(postSnapshot.getKey());
                        postList.add(post);
                    }
                }
                Collections.shuffle(postList);
                postPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        postsReference.addListenerForSingleValueEvent(postsListener);
    }

    private void detachListener() {
        if (postsListener != null) {
            postsReference.removeEventListener(postsListener);
        }
    }
}
