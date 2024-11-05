package com.example.betre;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.betre.adapters.FlaggedContentAdapter;
import com.example.betre.models.Post;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FlaggedModFragment extends Fragment {

    private static final String TAG = "FlaggedModFragment";
    private TextView totalFlaggedPosts;
    private RecyclerView recyclerViewFlaggedPosts;
    private FlaggedContentAdapter adapter;
    private List<Post> flaggedPostsList;
    private FirebaseFirestore firestore;

    public FlaggedModFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flagged_mod, container, false);

        // Initialize UI elements
        totalFlaggedPosts = view.findViewById(R.id.total_flagged_posts);
        recyclerViewFlaggedPosts = view.findViewById(R.id.recyclerViewFlaggedPosts);
        recyclerViewFlaggedPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        flaggedPostsList = new ArrayList<>();
        adapter = new FlaggedContentAdapter(getContext(), flaggedPostsList);
        recyclerViewFlaggedPosts.setAdapter(adapter);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Load flagged content statistics
        loadFlaggedContentStatistics();

        return view;
    }

    private void loadFlaggedContentStatistics() {
        CollectionReference postsRef = firestore.collection("posts");

        // Query to get all flagged posts
        postsRef.whereEqualTo("is_reported", true).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            flaggedPostsList.clear();
                            flaggedPostsList.addAll(querySnapshot.toObjects(Post.class));
                            adapter.notifyDataSetChanged();

                            // Display the total count of flagged posts
                            totalFlaggedPosts.setText("Total Flagged Posts: " + flaggedPostsList.size());
                        } else {
                            totalFlaggedPosts.setText("Total Flagged Posts: 0");
                        }
                    } else {
                        Log.e(TAG, "Error fetching flagged posts: ", task.getException());
                        Toast.makeText(getContext(), "Failed to load flagged posts", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
