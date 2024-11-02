
package com.example.betre;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betre.adapters.ReportedPostAdapter;
import com.example.betre.models.Post;
import com.example.betre.models.ReportedPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
// Import other necessary packages

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class ActivityAdminFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private ReportedPostAdapter adapter;
//    private List<ReportedPost> reportedPostList;
//    private DatabaseReference reportsRef;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState){
//        View view = inflater.inflate(R.layout.fragment_activity_admin, container, false);
//
//        recyclerView = view.findViewById(R.id.reported_posts_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        reportedPostList = new ArrayList<>();
//        adapter = new ReportedPostAdapter(reportedPostList, getContext());
//        recyclerView.setAdapter(adapter);
//
//        reportsRef = FirebaseDatabase.getInstance().getReference("reports");
//
//        fetchReportedPosts();
//
//        return view;
//    }
//
//    private void fetchReportedPosts() {
//        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot reportsSnapshot) {
//                reportedPostList.clear();
//                for (DataSnapshot postSnapshot : reportsSnapshot.getChildren()) {
//                    String postId = postSnapshot.getKey();
//                    Map<String, String> reports = new HashMap<>();
//                    for (DataSnapshot userSnapshot : postSnapshot.getChildren()) {
//                        String userId = userSnapshot.getKey();
//                        Object value = userSnapshot.getValue();
//
//                        String reportReason = "";
//
//                        if (value instanceof String) {
//                            // The report reason is stored as a simple String
//                            reportReason = (String) value;
//                        } else if (value instanceof Map) {
//                            // The report reason is stored as a Map (HashMap)
//                            Map<String, Object> reportData = (Map<String, Object>) value;
//                            reportReason = (String) reportData.get("reason");
//                        } else {
//                            // Handle unexpected data types
//                            Log.e("ActivityAdminFragment", "Unexpected data type for report reason.");
//                        }
//
//                        if (reportReason != null) {
//                            reports.put(userId, reportReason);
//                        }
//                    }
//
//                    // Fetch the post details
//                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
//                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot postSnapshot) {
//                            Post post = postSnapshot.getValue(Post.class);
//                            ReportedPost reportedPost = new ReportedPost();
//                            reportedPost.setPostId(postId);
//                            reportedPost.setPost(post);
//                            reportedPost.setReports(reports);
//
//                            reportedPostList.add(reportedPost);
//                            adapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            Log.e("ActivityAdminFragment", "Error fetching post details: " + databaseError.getMessage());
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("ActivityAdminFragment", "Error fetching reports: " + databaseError.getMessage());
//            }
//        });
//    }
//}
public class ActivityAdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReportedPostAdapter adapter;
    private List<ReportedPost> reportedPostList;
    private List<ReportedPost> filteredPostList; // For search functionality
    private DatabaseReference reportsRef;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_activity_admin, container, false);

        recyclerView = view.findViewById(R.id.reported_posts_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reportedPostList = new ArrayList<>();
        filteredPostList = new ArrayList<>();
        adapter = new ReportedPostAdapter(filteredPostList, getContext());
        recyclerView.setAdapter(adapter);

        searchView = view.findViewById(R.id.admin_search_view);

        reportsRef = FirebaseDatabase.getInstance().getReference("reports");

        fetchReportedPosts();

        setupSearchView();

        return view;
    }

    private void fetchReportedPosts() {
        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot reportsSnapshot) {
                reportedPostList.clear();
                for (DataSnapshot postSnapshot : reportsSnapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    Map<String, String> reports = new HashMap<>();
                    for (DataSnapshot userSnapshot : postSnapshot.getChildren()) {
                        String userId = userSnapshot.getKey();
                        Object value = userSnapshot.getValue();

                        String reportReason = "";

                        if (value instanceof String) {
                            // The report reason is stored as a simple String
                            reportReason = (String) value;
                        } else if (value instanceof Map) {
                            // The report reason is stored as a Map (HashMap)
                            Map<String, Object> reportData = (Map<String, Object>) value;
                            reportReason = (String) reportData.get("reason");
                        } else {
                            // Handle unexpected data types
                            Log.e("ActivityAdminFragment", "Unexpected data type for report reason.");
                        }

                        if (reportReason != null) {
                            reports.put(userId, reportReason);
                        }
                    }

                    // Fetch the post details
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot postSnapshot) {
                            Post post = postSnapshot.getValue(Post.class);
                            ReportedPost reportedPost = new ReportedPost();
                            reportedPost.setPostId(postId);
                            reportedPost.setPost(post);
                            reportedPost.setReports(reports);

                            reportedPostList.add(reportedPost);
                            filteredPostList.add(reportedPost); // Initially, filtered list contains all posts
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e("ActivityAdminFragment", "Error fetching post details: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ActivityAdminFragment", "Error fetching reports: " + databaseError.getMessage());
            }
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the final search
                filterPosts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the user types
                filterPosts(newText);
                return true;
            }
        });
    }

    private void filterPosts(String query) {
        filteredPostList.clear();
        if (query.isEmpty()) {
            filteredPostList.addAll(reportedPostList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ReportedPost reportedPost : reportedPostList) {
                Post post = reportedPost.getPost();
                if (post != null) {
                    boolean matchesContent = post.getContent() != null && post.getContent().toLowerCase().contains(lowerCaseQuery);
                    boolean matchesLocation = post.getLocation() != null && post.getLocation().toLowerCase().contains(lowerCaseQuery);

                    // Fetch the username of the post owner
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(post.getUserId());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String username = dataSnapshot.child("username").getValue(String.class);
                            boolean matchesUsername = username != null && username.toLowerCase().contains(lowerCaseQuery);

                            if (matchesContent || matchesLocation || matchesUsername) {
                                filteredPostList.add(reportedPost);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("ActivityAdminFragment", "Error fetching username: " + databaseError.getMessage());
                        }
                    });
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
