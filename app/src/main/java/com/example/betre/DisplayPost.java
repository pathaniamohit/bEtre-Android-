package com.example.betre;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DisplayPost extends Fragment {

    private ViewPager2 viewPager;
    private PostPagerAdapter postPagerAdapter;
    private List<Post> postList;

    private DatabaseReference postsReference;
    private ValueEventListener postsListener;

    // Key for passing the userId
    private static final String ARG_USER_ID = "userId";
    private String userId; // This will store the passed userId

    public DisplayPost() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of DisplayPost
     *
     * @param userId The userId to filter posts by
     * @return A new instance of fragment DisplayPost
     */
    public static DisplayPost newInstance(String userId) {
        DisplayPost fragment = new DisplayPost();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId); // Pass userId through arguments
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

        // Initialize the reference to the "posts" in Realtime Database
        postsReference = FirebaseDatabase.getInstance().getReference("posts");

        // Retrieve the passed userId from the arguments
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_display_post, container, false);

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

    // Method to load posts for the given userId
    private void loadPosts() {
        postsListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);

                    if (post != null && post.getUserId().equals(userId)) {
                        post.setPostId(postSnapshot.getKey());
                        postList.add(post);
                    }
                }

                if (postList.isEmpty() && isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "No posts available for this user.", Toast.LENGTH_SHORT).show();
                }

                postPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Fetch posts for the given userId
        postsReference.addListenerForSingleValueEvent(postsListener);
    }

    // Detach the listener when the fragment is not visible
    private void detachListener() {
        if (postsListener != null) {
            postsReference.removeEventListener(postsListener);
        }
    }
}
