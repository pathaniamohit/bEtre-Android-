package com.example.betre;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashSet;
import java.util.Set;

public class ProfileAdminFragment extends Fragment {
    private static final String TAG = "ProfileAdminFragment";

    private ImageView profilePicture;
    private TextView profileName, profileEmail, usersCount, totalPosts, totalLikes, totalReportedUsers, totalComments, totalModerators, suspendedUsers, reportedPosts, reportedComments, reportedProfiles;
    private Button logoutButton;

    private DatabaseReference databaseRef;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_admin, container, false);

        profilePicture = view.findViewById(R.id.profile_picture);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        usersCount = view.findViewById(R.id.users_count);
        totalPosts = view.findViewById(R.id.total_posts);
        totalLikes = view.findViewById(R.id.total_likes);
        totalReportedUsers = view.findViewById(R.id.total_reported_users);
        totalComments = view.findViewById(R.id.total_comments);
        totalModerators = view.findViewById(R.id.total_moderators);
        suspendedUsers = view.findViewById(R.id.suspended_users);
        reportedPosts = view.findViewById(R.id.reported_posts);
        reportedComments = view.findViewById(R.id.reported_comments);
        reportedProfiles = view.findViewById(R.id.reported_profiles);
        logoutButton = view.findViewById(R.id.logout_button);

        auth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            loadUserProfile(user.getUid());
            fetchStatsData();
            fetchAdditionalStatsData();
        }

        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser != null) {
                            setIsOnlineStatus(currentUser.getUid(), false, this::signOutUser);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }

    private void setIsOnlineStatus(String uid, boolean isOnline, Runnable onComplete) {
        databaseRef.child("users").child(uid).child("isOnline").setValue(isOnline)
                .addOnSuccessListener(aVoid -> onComplete.run())
                .addOnFailureListener(e -> {
                    onComplete.run();
                    // Optionally, handle failure to update isOnline status here
                });
    }

    private void signOutUser() {
        auth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void loadUserProfile(String userId) {
        databaseRef.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                    profileName.setText(username != null ? username : "Username");
                    profileEmail.setText(email != null ? email : "Email");

                    if (profileImageUrl != null) {
                        Glide.with(ProfileAdminFragment.this).load(profileImageUrl).into(profilePicture);
                    }
                } else {
                    Log.w(TAG, "User data not found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load user profile", error.toException());
            }
        });
    }

    private void fetchStatsData() {
        databaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int userCount = 0;
                int suspendedCount = 0;

                for (DataSnapshot user : snapshot.getChildren()) {
                    String role = user.child("role").getValue(String.class);

                    // Only count users who are not "admin" or "moderator"
                    if (!"admin".equalsIgnoreCase(role) && !"moderator".equalsIgnoreCase(role)) {
                        userCount++;
                    }

                    // Check if the user is suspended
                    Boolean isSuspended = user.child("suspended").getValue(Boolean.class);
                    if (isSuspended != null && isSuspended) {
                        suspendedCount++;
                    }
                }

                usersCount.setText(String.valueOf(userCount));
                suspendedUsers.setText(String.valueOf(suspendedCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch user count", error.toException());
            }
        });

        databaseRef.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalPosts.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch posts count", error.toException());
            }
        });

        databaseRef.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int likesCount = 0;
                for (DataSnapshot post : snapshot.getChildren()) {
                    likesCount += post.child("users").getChildrenCount();
                }
                totalLikes.setText(String.valueOf(likesCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch likes count", error.toException());
            }
        });
    }

    private void fetchAdditionalStatsData() {
        fetchTotalReportedUsers();
        fetchTotalComments();
        fetchTotalModerators();
        fetchReportedPosts();
        fetchReportedComments();
        fetchReportedProfiles();
    }

    private void fetchTotalReportedUsers() {
        Set<String> uniqueReportedUserIds = new HashSet<>();
        databaseRef.child("reports").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot report : snapshot.getChildren()) {
                    String reportedUserId = report.child("reportedBy").getValue(String.class);
                    if (reportedUserId != null) {
                        uniqueReportedUserIds.add(reportedUserId);
                    }
                }
                totalReportedUsers.setText(String.valueOf(uniqueReportedUserIds.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch reported users count", error.toException());
            }
        });
    }

    private void fetchTotalComments() {
        databaseRef.child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int commentsCount = (int) snapshot.getChildrenCount();
                totalComments.setText(String.valueOf(commentsCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch comments count", error.toException());
            }
        });
    }

    private void fetchTotalModerators() {
        databaseRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int moderatorCount = 0;
                for (DataSnapshot user : snapshot.getChildren()) {
                    String role = user.child("role").getValue(String.class);
                    if ("moderator".equalsIgnoreCase(role)) {
                        moderatorCount++;
                    }
                }
                totalModerators.setText(String.valueOf(moderatorCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch moderators count", error.toException());
            }
        });
    }

    private void fetchReportedPosts() {
        databaseRef.child("reports").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportedPosts.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch reported posts count", error.toException());
            }
        });
    }

    private void fetchReportedComments() {
        databaseRef.child("report_comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportedComments.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch reported comments count", error.toException());
            }
        });
    }

    private void fetchReportedProfiles() {
        databaseRef.child("reported_profiles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportedProfiles.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch reported profiles count", error.toException());
            }
        });
    }
}
