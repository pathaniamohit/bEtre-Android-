//package com.example.betre;
////
////import android.os.Bundle;
////
////import androidx.fragment.app.Fragment;
////
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////
/////**
//// * A simple {@link Fragment} subclass.
//// * Use the {@link InboxAdminFragment#newInstance} factory method to
//// * create an instance of this fragment.
//// */
////public class InboxAdminFragment extends Fragment {
////
////    // TODO: Rename parameter arguments, choose names that match
////    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
////    private static final String ARG_PARAM1 = "param1";
////    private static final String ARG_PARAM2 = "param2";
////
////    // TODO: Rename and change types of parameters
////    private String mParam1;
////    private String mParam2;
////
////    public InboxAdminFragment() {
////        // Required empty public constructor
////    }
////
////    /**
////     * Use this factory method to create a new instance of
////     * this fragment using the provided parameters.
////     *
////     * @param param1 Parameter 1.
////     * @param param2 Parameter 2.
////     * @return A new instance of fragment InboxAdminFragment.
////     */
////    // TODO: Rename and change types and number of parameters
////    public static InboxAdminFragment newInstance(String param1, String param2) {
////        InboxAdminFragment fragment = new InboxAdminFragment();
////        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
////        args.putString(ARG_PARAM2, param2);
////        fragment.setArguments(args);
////        return fragment;
////    }
////
////    @Override
////    public void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
////        }
////    }
////
////    @Override
////    public View onCreateView(LayoutInflater inflater, ViewGroup container,
////                             Bundle savedInstanceState) {
////        // Inflate the layout for this fragment
////        return inflater.inflate(R.layout.fragment_inbox_admin, container, false);
////    }
//
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.betre.adapters.ReportedPostAdapter;
//import com.example.betre.models.Post;
//import com.example.betre.models.ReportedPost;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class InboxAdminFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private ReportedPostAdapter adapter;
//    private List<ReportedPost> reportedPostList;
//    private DatabaseReference reportsRef;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_inbox_admin, container, false);
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
////    private void fetchReportedPosts() {
////        reportsRef.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot reportsSnapshot) {
////                reportedPostList.clear();
////                for (DataSnapshot postSnapshot : reportsSnapshot.getChildren()) {
////                    String postId = postSnapshot.getKey();
////                    Map<String, String> reports = new HashMap<>();
////                    for (DataSnapshot userSnapshot : postSnapshot.getChildren()) {
////                        String userId = userSnapshot.getKey();
////                        String reportReason = userSnapshot.getValue(String.class);
////                        reports.put(userId, reportReason);
////                    }
////
////                    // Fetch the post details
////                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
////                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot postSnapshot) {
////                            Post post = postSnapshot.getValue(Post.class);
////                            ReportedPost reportedPost = new ReportedPost();
////                            reportedPost.setPostId(postId);
////                            reportedPost.setPost(post);
////                            reportedPost.setReports(reports);
////
////                            reportedPostList.add(reportedPost);
////                            adapter.notifyDataSetChanged();
////                        }
////
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////                            Log.e("InboxAdminFragment", "Error fetching post details: " + databaseError.getMessage());
////                        }
////                    });
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////                Log.e("InboxAdminFragment", "Error fetching reports: " + databaseError.getMessage());
////            }
////        });
////    }
//
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
//                            Log.e("InboxAdminFragment", "Unexpected data type for report reason.");
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
//                            Log.e("InboxAdminFragment", "Error fetching post details: " + databaseError.getMessage());
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.e("InboxAdminFragment", "Error fetching reports: " + databaseError.getMessage());
//            }
//        });
//    }
//}

package com.example.betre;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxAdminFragment extends Fragment {

    public InboxAdminFragment() {
        // Required empty public constructor
    }

    public static InboxAdminFragment newInstance() {
        return new InboxAdminFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate an empty or default layout
        return inflater.inflate(R.layout.fragment_inbox_admin, container, false);
    }
}

