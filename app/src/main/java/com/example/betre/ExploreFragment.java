package com.example.betre;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.betre.adapters.PostPagerAdapter;
import com.example.betre.models.Post;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExploreFragment extends Fragment {

    private ViewPager2 viewPager;
    private ImageView message_icon;
    private PostPagerAdapter postPagerAdapter;
    private List<Post> postList;

    private DatabaseReference postsReference;
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
        FirebaseApp.initializeApp(requireContext());  // Use requireContext() in Fragment
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());

        // Initialize the reference to the "posts" in Realtime Database
        postsReference = FirebaseDatabase.getInstance().getReference("posts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        viewPager = view.findViewById(R.id.view_pager);
        message_icon = view.findViewById(R.id.message_icon);
        postList = new ArrayList<>();
        postPagerAdapter = new PostPagerAdapter(getContext(), postList);
        viewPager.setAdapter(postPagerAdapter);

        message_icon.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.home_content, new MessageFragment())
                    .addToBackStack(null)
                    .commit();
        });

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

        postsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);

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

    private void detachListener() {
        if (postsListener != null) {
            postsReference.removeEventListener(postsListener);
        }
    }
}


