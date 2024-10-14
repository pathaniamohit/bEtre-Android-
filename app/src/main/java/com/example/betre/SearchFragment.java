package com.example.betre;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.betre.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private TextView searchResult;
    private Button btnGo;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private Map<String, String> userIdToUsernameMap;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        searchResult = view.findViewById(R.id.searchResult);
        btnGo = view.findViewById(R.id.btnGo);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        userIdToUsernameMap = new HashMap<>();

        fetchPosts("");

        fetchUsers();

        // Set up "Go" button click listener to trigger the filtering (search functionality)
        btnGo.setOnClickListener(v -> {
            String query = searchView.getQuery().toString();
            if (!query.isEmpty()) {
                searchResult.setText("Searching for: " + query);
                fetchPosts(query);
            } else {
                searchResult.setText("Please enter something to search.");
                fetchPosts("");
            }
        });

        return view;
    }

    // Fetch and cache all users' usernames to minimize repetitive database calls
    private void fetchUsers() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userIdToUsernameMap.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    String username = userSnapshot.child("username").getValue(String.class);

                    if (username != null && userId != null) {
                        userIdToUsernameMap.put(userId, username.toLowerCase()); // Store username in lowercase
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SearchFragment", "User fetch error: " + databaseError.getMessage());
            }
        });
    }

    // Fetch posts from Firebase Realtime Database with optional filtering by username or location
    private void fetchPosts(String query) {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    if (post == null) continue;

                    String lowerCaseQuery = query.toLowerCase();

                    if (query.isEmpty()) {
                        postList.add(post);
                    } else {
                        if (post.getLocation() != null && post.getLocation().toLowerCase().contains(lowerCaseQuery)) {
                            postList.add(post);
                        } else {
                            String postUserId = post.getUserId();
                            String username = userIdToUsernameMap.get(postUserId);
                            if (username != null && username.contains(lowerCaseQuery)) {
                                postList.add(post);
                            }
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SearchFragment", "Database error: " + databaseError.getMessage());
            }
        });
    }

    // RecyclerView Adapter for displaying posts
    public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
        private List<Post> posts;

        public PostAdapter(List<Post> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new PostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PostViewHolder holder, int position) {
            Post post = posts.get(position);
            Glide.with(getContext())
                    .load(post.getImageUrl())
                    .into(holder.postImage);
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public class PostViewHolder extends RecyclerView.ViewHolder {
            public ImageView postImage;

            public PostViewHolder(View itemView) {
                super(itemView);
                postImage = itemView.findViewById(R.id.image_item);
            }
        }
    }
}
