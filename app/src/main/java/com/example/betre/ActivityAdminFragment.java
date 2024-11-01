//package com.example.betre;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ActivityAdminFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class ActivityAdminFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public ActivityAdminFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment ActivityAdminFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static ActivityAdminFragment newInstance(String param1, String param2) {
//        ActivityAdminFragment fragment = new ActivityAdminFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_activity_admin, container, false);
//    }
//}

// File: app/src/main/java/com/example/betre/ActivityAdminFragment.java
package com.example.betre;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.betre.adapters.AdminPostAdapter;
import com.example.betre.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to display all posts for admin management.
 */
public class ActivityAdminFragment extends Fragment {

    private RecyclerView recyclerViewAdminPosts;
    private AdminPostAdapter adminPostAdapter;
    private List<Post> postList;

    private DatabaseReference postsReference;
    private ValueEventListener postsListener;

    public ActivityAdminFragment() {
        // Required empty public constructor
    }

    public static ActivityAdminFragment newInstance() {
        ActivityAdminFragment fragment = new ActivityAdminFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase reference
        postsReference = FirebaseDatabase.getInstance().getReference("posts");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_activity_admin, container, false);

        recyclerViewAdminPosts = view.findViewById(R.id.recyclerViewAdminPosts);
        recyclerViewAdminPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        adminPostAdapter = new AdminPostAdapter(getContext(), postList);
        recyclerViewAdminPosts.setAdapter(adminPostAdapter);

        loadAllPosts();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAllPosts();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachListener();
    }

    /**
     * Fetch all posts from Firebase and populate the RecyclerView.
     */
    private void loadAllPosts(){
        postsListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                postList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post != null){
                        post.setPostId(dataSnapshot.getKey());
                        postList.add(post);
                    }
                }
                adminPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(getContext(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        };

        postsReference.addValueEventListener(postsListener);
    }

    /**
     * Remove the Firebase listener to prevent memory leaks.
     */
    private void detachListener(){
        if(postsListener != null){
            postsReference.removeEventListener(postsListener);
        }
    }
}
