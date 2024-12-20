package com.example.betre;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betre.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private TextView searchResult;
    private Button btnGo;
    private RecyclerView recyclerView, suggestionsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private Map<String, String> userIdToUsernameMap;
    private LinearLayout tagsLayout;

    private List<Suggestion> suggestionList;
    private SuggestionsAdapter suggestionsAdapter;
    private DatabaseReference followingReference;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = view.findViewById(R.id.searchView);
        searchResult = view.findViewById(R.id.searchResult);
        btnGo = view.findViewById(R.id.btnGo);
        recyclerView = view.findViewById(R.id.recyclerView);
        tagsLayout = view.findViewById(R.id.tagsLayout);
        suggestionsRecyclerView = view.findViewById(R.id.suggestionsRecyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        // Set up suggestions RecyclerView
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        suggestionList = new ArrayList<>();
        suggestionsAdapter = new SuggestionsAdapter(suggestionList);
        suggestionsRecyclerView.setAdapter(suggestionsAdapter);
        followingReference = FirebaseDatabase.getInstance().getReference("following");


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

        // Listen to search query changes
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchPosts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSuggestions(newText);
                return false;
            }
        });

        // Fetch and display random tags for locations and users
        displayRandomTags();
        followingReference = FirebaseDatabase.getInstance().getReference("following");

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
//    private void fetchPosts(String query) {
//        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
//
//        postsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                postList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Post post = snapshot.getValue(Post.class);
//
//                    if (post == null) continue;
//
//                    String lowerCaseQuery = query.toLowerCase();
//
//                    if (query.isEmpty()) {
//                        postList.add(post);
//                    } else {
//                        if (post.getLocation() != null && post.getLocation().toLowerCase().contains(lowerCaseQuery)) {
//                            postList.add(post);
//                        } else {
//                            String postUserId = post.getUserId();
//                            String username = userIdToUsernameMap.get(postUserId);
//                            if (username != null && username.contains(lowerCaseQuery)) {
//                                postList.add(post);
//                            }
//                        }
//                    }
//                }
//                postAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("SearchFragment", "Database error: " + databaseError.getMessage());
//            }
//        });
//    }

    private void fetchPosts(String query) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // First, get the list of user IDs that the current user is following
        followingReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot followingSnapshot) {
                Set<String> followingUserIds = new HashSet<>();
                for (DataSnapshot userSnapshot : followingSnapshot.getChildren()) {
                    Boolean isFollowing = userSnapshot.getValue(Boolean.class);
                    if (isFollowing != null && isFollowing) {
                        followingUserIds.add(userSnapshot.getKey());
                    }
                }

                // Now, fetch posts from users that the current user is NOT following
                DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
                postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        postList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post post = snapshot.getValue(Post.class);

                            if (post == null) continue;

                            String postUserId = post.getUserId();
                            // Skip posts from users the current user is following
                            if (followingUserIds.contains(postUserId) || postUserId.equals(currentUserId)) {
                                continue;
                            }

                            String lowerCaseQuery = query.toLowerCase();

                            if (query.isEmpty()) {
                                postList.add(post);
                            } else {
                                if (post.getLocation() != null && post.getLocation().toLowerCase().contains(lowerCaseQuery)) {
                                    postList.add(post);
                                } else {
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SearchFragment", "Error fetching following users: " + databaseError.getMessage());
            }
        });
    }


    // Filter the usernames and locations based on search query
    private void filterSuggestions(String query) {
        suggestionList.clear();
        if (!query.isEmpty()) {
            String lowerCaseQuery = query.toLowerCase();
            for (String userId : userIdToUsernameMap.keySet()) {
                String username = userIdToUsernameMap.get(userId);
                if (username.contains(lowerCaseQuery)) {
                    suggestionList.add(new Suggestion(username, SuggestionType.USERNAME));
                }
            }

            // Add locations to suggestions
            DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
            postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String location = snapshot.child("location").getValue(String.class);
                        if (location != null && location.toLowerCase().contains(lowerCaseQuery)) {
                            suggestionList.add(new Suggestion(location, SuggestionType.LOCATION));
                        }
                    }
                    suggestionsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("SearchFragment", "Location fetch error: " + databaseError.getMessage());
                }
            });
        }

        if (suggestionList.isEmpty()) {
            suggestionsRecyclerView.setVisibility(View.GONE);
            tagsLayout.setVisibility(View.VISIBLE); // Show tags when no suggestions
        } else {
            suggestionsRecyclerView.setVisibility(View.VISIBLE);
            tagsLayout.setVisibility(View.GONE); // Hide tags when suggestions are present
        }

        suggestionsAdapter.notifyDataSetChanged();
    }

    // Adapter for displaying username and location suggestions
    private class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.SuggestionsViewHolder> {
        private List<Suggestion> suggestions;

        public SuggestionsAdapter(List<Suggestion> suggestions) {
            this.suggestions = suggestions;
        }

        @NonNull
        @Override
        public SuggestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
            return new SuggestionsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SuggestionsViewHolder holder, int position) {
            Suggestion suggestion = suggestions.get(position);
            holder.suggestionTextView.setText(suggestion.getText());

            holder.itemView.setOnClickListener(v -> {
                if (suggestion.getType() == SuggestionType.USERNAME) {
                    openUserProfileFragment(suggestion.getText());
                } else if (suggestion.getType() == SuggestionType.LOCATION) {
                    fetchPosts(suggestion.getText());
                }
            });
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }

        public class SuggestionsViewHolder extends RecyclerView.ViewHolder {
            TextView suggestionTextView;

            public SuggestionsViewHolder(View itemView) {
                super(itemView);
                suggestionTextView = itemView.findViewById(R.id.suggestionTextView);
            }
        }
    }

    // Class to store suggestion data
    private class Suggestion {
        private String text;
        private SuggestionType type;

        public Suggestion(String text, SuggestionType type) {
            this.text = text;
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public SuggestionType getType() {
            return type;
        }
    }

    // Enum moved outside the inner class
    enum SuggestionType {
        USERNAME, LOCATION
    }

//    private void openUserProfileFragment(String username) {
//        String userId = getUserIdFromUsername(username);
//        if (userId != null) {
//            UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(userId);
//            ((AppCompatActivity) getContext()).getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.home_content, userProfileFragment)
//                    .addToBackStack(null)
//                    .commit();
//        }
//    }
//
//    private String getUserIdFromUsername(String username) {
//        for (Map.Entry<String, String> entry : userIdToUsernameMap.entrySet()) {
//            if (entry.getValue().equalsIgnoreCase(username)) {
//                return entry.getKey();
//            }
//        }
//        return null;
//    }

    private void openUserProfileFragment(String username) {
        String userId = getUserIdFromUsername(username);
        if (userId != null) {
            UserProfileFragment userProfileFragment = UserProfileFragment.newInstance(userId);
            ((AppCompatActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_content, userProfileFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            Log.e("SearchFragment", "openUserProfileFragment: User ID not found for username: " + username);
            Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getUserIdFromUsername(String username) {
        for (Map.Entry<String, String> entry : userIdToUsernameMap.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(username)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void openDisplayPostFragment(String userId) {
        DisplayPost displayPostFragment = DisplayPost.newInstance(userId);
        ((AppCompatActivity) getContext()).getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_content, displayPostFragment)
                .addToBackStack(null)
                .commit();
    }

    // Display random tags for users and locations
    private void displayRandomTags() {
        tagsLayout.removeAllViews();

        Button allTagButton = new Button(getContext());
        allTagButton.setText("All");
        allTagButton.setOnClickListener(v -> {
            fetchPosts("");
        });
        tagsLayout.addView(allTagButton);

        // Fetch locations and display random tags
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> locations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String location = snapshot.child("location").getValue(String.class);
                    if (location != null && !locations.contains(location)) {
                        locations.add(location);
                    }
                }

                Collections.shuffle(locations);

                int tagCount = Math.min(5, locations.size());
                for (int i = 0; i < tagCount; i++) {
                    String locationTag = locations.get(i);

                    Button tagButton = new Button(getContext());
                    tagButton.setText(locationTag);
                    tagButton.setOnClickListener(v -> {
                        fetchPosts(locationTag);
                    });

                    tagsLayout.addView(tagButton);
                }
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
        private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Milliseconds
        private long lastClickTime = 0;

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

            // Handle double click
            holder.itemView.setOnClickListener(v -> {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    // Double-click detected, open DisplayPost fragment and pass post's userId
                    openDisplayPostFragment(post.getUserId());
                }
                lastClickTime = clickTime;
            });
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
