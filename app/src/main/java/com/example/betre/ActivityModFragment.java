package com.example.betre;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.betre.adapters.PostAnalyticsAdapter;
import com.example.betre.models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to display analytics data on posts for the moderator.
 */
public class ActivityModFragment extends Fragment {

    private RecyclerView recyclerViewPostAnalytics;
    private PostAnalyticsAdapter postAnalyticsAdapter;
    private List<Post> postList;

    private DatabaseReference postsReference;
    private TextView totalPostsTextView, totalLikesTextView, totalCommentsTextView;
    private static final String TAG = "ActivityModFragment";

    public ActivityModFragment() {
        // Required empty public constructor
    }

    public static ActivityModFragment newInstance() {
        return new ActivityModFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity_mod, container, false);

        totalPostsTextView = view.findViewById(R.id.total_posts);
        totalLikesTextView = view.findViewById(R.id.total_likes);
        totalCommentsTextView = view.findViewById(R.id.total_comments);

        recyclerViewPostAnalytics = view.findViewById(R.id.recyclerViewPostAnalytics);
        recyclerViewPostAnalytics.setLayoutManager(new LinearLayoutManager(getContext()));

        postList = new ArrayList<>();
        postAnalyticsAdapter = new PostAnalyticsAdapter(getContext(), postList);
        recyclerViewPostAnalytics.setAdapter(postAnalyticsAdapter);

        postsReference = FirebaseDatabase.getInstance().getReference("posts");

        loadAnalyticsData();

        return view;
    }

    private void loadAnalyticsData() {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                int totalPosts = 0;
                int totalLikes = 0;
                int totalComments = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post != null) {
                        post.setPostId(dataSnapshot.getKey());
                        postList.add(post);

                        totalPosts++;
                        totalLikes += post.getCount_like();
                        totalComments += post.getCount_comment();
                    }
                }

                // Update the TextViews with analytics data
                totalPostsTextView.setText(String.valueOf(totalPosts));
                totalLikesTextView.setText(String.valueOf(totalLikes));
                totalCommentsTextView.setText(String.valueOf(totalComments));

                postAnalyticsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading analytics data", error.toException());
                Toast.makeText(getContext(), "Failed to load analytics data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}