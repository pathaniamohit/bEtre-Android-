package com.example.betre;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.SearchView; // Use androidx.appcompat.widget.SearchView

import com.example.betre.adapters.PostPagerAdapter;
import com.example.betre.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityAdminFragment extends Fragment {

    private ViewPager2 viewPager;
    private PostPagerAdapter adapter;
    private List<Post> postList;
    private List<Post> filteredPostList; // For search functionality
    private DatabaseReference postsRef;
    private SearchView searchView; // Updated import to androidx.appcompat.widget.SearchView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_admin, container, false);

        viewPager = view.findViewById(R.id.reported_posts_recycler_view);
        postList = new ArrayList<>();
        filteredPostList = new ArrayList<>();

        // Initialize PostPagerAdapter with isProfile set to false for admin view
        adapter = new PostPagerAdapter(getContext(), filteredPostList, false);
        viewPager.setAdapter(adapter);

        searchView = view.findViewById(R.id.searchView);

        // Reference the "posts" node directly
        postsRef = FirebaseDatabase.getInstance().getReference("posts");

        fetchAllPosts();
        setupSearchView();

        return view;
    }

    private void fetchAllPosts() {
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot postsSnapshot) {
                postList.clear();
                filteredPostList.clear(); // Clear filtered list initially

                for (DataSnapshot postSnapshot : postsSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(postSnapshot.getKey());
                        postList.add(post);
                        filteredPostList.add(post); // Initially, filtered list contains all posts
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ActivityAdminFragment", "Error fetching posts: " + databaseError.getMessage());
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterPostsByLocation(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterPostsByLocation(newText); // Filter as you type
                return true;
            }
        });
    }

    private void filterPostsByLocation(String query) {
        filteredPostList.clear();
        if (query.isEmpty()) {
            filteredPostList.addAll(postList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Post post : postList) {
                boolean matchesLocation = post.getLocation() != null && post.getLocation().toLowerCase().contains(lowerCaseQuery);
                if (matchesLocation) {
                    filteredPostList.add(post);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}