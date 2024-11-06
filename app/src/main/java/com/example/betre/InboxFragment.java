package com.example.betre;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betre.adapters.NotificationAdapter;
import com.example.betre.models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class InboxFragment extends Fragment {

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference followersRef, likesRef, commentsRef, warningsRef;

    private static final String TAG = "InboxFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        notificationsRecyclerView = view.findViewById(R.id.notifications_recycler_view);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(notificationList, getContext());
        notificationsRecyclerView.setAdapter(notificationAdapter);

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();

        followersRef = FirebaseDatabase.getInstance().getReference("followers").child(currentUserId);
        likesRef = FirebaseDatabase.getInstance().getReference("likes");
        commentsRef = FirebaseDatabase.getInstance().getReference("comments");
        warningsRef = FirebaseDatabase.getInstance().getReference("warnings").child(currentUserId);

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        loadFollowNotifications();
        loadLikeNotifications();
        loadCommentNotifications();
        loadWarningNotifications();
    }

    private void loadFollowNotifications() {
        followersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot followerSnapshot : snapshot.getChildren()) {
                    if (followerSnapshot.getValue(Boolean.class)) {
                        String followerId = followerSnapshot.getKey();
                        fetchUsername(followerId, username -> {
                            Notification notification = new Notification(
                                    UUID.randomUUID().toString(),
                                    "follow",
                                    followerId,
                                    null,
                                    System.currentTimeMillis(),
                                    username,
                                    null
                            );
                            notificationList.add(notification);
                            notificationAdapter.notifyDataSetChanged();
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load follow notifications.", error.toException());
            }
        });
    }

    private void loadLikeNotifications() {
        likesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    String postOwnerId = postSnapshot.child("ownerId").getValue(String.class);

                    if (postOwnerId != null && postOwnerId.equals(mAuth.getCurrentUser().getUid())) {
                        DataSnapshot usersSnapshot = postSnapshot.child("users");
                        for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                            String likerUserId = userSnapshot.getKey();
                            long likedAt = userSnapshot.child("likedAt").getValue(Long.class);

                            fetchUsername(likerUserId, likerUsername -> {
                                Notification notification = new Notification(
                                        UUID.randomUUID().toString(),
                                        "like",
                                        likerUserId,
                                        postId,
                                        likedAt,
                                        likerUsername,
                                        null
                                );
                                notificationList.add(notification);
                                notificationAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load like notifications.", error.toException());
            }
        });
    }

    private void loadCommentNotifications() {
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    String postId = commentSnapshot.child("post_Id").getValue(String.class);
                    String commenterId = commentSnapshot.child("userId").getValue(String.class);
                    String content = commentSnapshot.child("content").getValue(String.class);
                    Long timestamp = commentSnapshot.child("timestamp").getValue(Long.class);

                    // Log and check for null values
                    if (postId == null || commenterId == null || timestamp == null) {
                        Log.e(TAG, "Null value detected in comment data: " +
                                "postId=" + postId + ", commenterId=" + commenterId + ", timestamp=" + timestamp);
                        continue; // Skip this entry if any necessary field is null
                    }

                    DatabaseReference postOwnerRef = FirebaseDatabase.getInstance().getReference("posts").child(postId).child("userId");
                    postOwnerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String postOwnerId = snapshot.getValue(String.class);
                            if (postOwnerId != null && postOwnerId.equals(mAuth.getCurrentUser().getUid())) {
                                fetchUsername(commenterId, commenterUsername -> {
                                    Notification notification = new Notification(
                                            UUID.randomUUID().toString(),
                                            "comment",
                                            commenterId,
                                            postId,
                                            timestamp,
                                            commenterUsername,
                                            content
                                    );
                                    notificationList.add(notification);
                                    notificationAdapter.notifyDataSetChanged();
                                });
                            } else {
                                Log.e(TAG, "Post owner ID is null or does not match current user ID");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "Failed to verify post ownership.", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load comment notifications.", error.toException());
            }
        });
    }

    private void loadWarningNotifications() {
        warningsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot warningSnapshot : snapshot.getChildren()) {
                    String warnedUserId = warningSnapshot.child("userId").getValue(String.class);
                    String reason = warningSnapshot.child("reason").getValue(String.class);
                    long timestamp = warningSnapshot.child("timestamp").getValue(Long.class);

                    Notification notification = new Notification(
                            UUID.randomUUID().toString(),
                            "warning",
                            warnedUserId,
                            null,
                            timestamp,
                            "Admin",
                            reason
                    );
                    notificationList.add(notification);
                    notificationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load warning notifications.", error.toException());
            }
        });
    }

    private void fetchUsername(String userId, Consumer<String> callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.getValue(String.class);
                callback.accept(username != null ? username : "Unknown User");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch username for userId: " + userId, error.toException());
                callback.accept("Unknown User");
            }
        });
    }
}
