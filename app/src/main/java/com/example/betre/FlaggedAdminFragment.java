package com.example.betre;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betre.adapters.ReportAdapter;
import com.example.betre.models.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FlaggedAdminFragment extends Fragment {

    private static final String TAG = "FlaggedAdminFragment";

    private RecyclerView reportsRecyclerView;
    private ProgressBar progressBar;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;

    private DatabaseReference reportsRef;
    private FirebaseAuth mAuth;

    public FlaggedAdminFragment() {
        // Required empty public constructor
    }

    public static FlaggedAdminFragment newInstance() {
        return new FlaggedAdminFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flagged_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        reportsRecyclerView = view.findViewById(R.id.reports_recycler_view);
        progressBar = view.findViewById(R.id.admin_progress_bar);

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance();
        reportsRef = FirebaseDatabase.getInstance().getReference("reported_profiles");

        // Set up RecyclerView
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(reportList, getContext(), this);
        reportsRecyclerView.setAdapter(reportAdapter);

        // Check if current user is admin
        if (isCurrentUserAdmin()) {
            fetchReports();
        }
        else{
//            Toast.makeText(getContext(), "Unauthorized access.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Non-admin user attempted to access reports.");
            // Optionally, navigate back or close the fragment
            getParentFragmentManager().popBackStack();
        }
    }

    // Method to check if the current user is an admin
    private boolean isCurrentUserAdmin(){
        String currentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);
        final boolean[] isAdmin = {false};

        userRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if (snapshot.exists()) {
                    String role = snapshot.child("role").getValue(String.class);
                    if ("admin".equals(role)) {
                        isAdmin[0] = true;
                        fetchReports(); // Fetch reports once confirmed as admin
                    }
                    else{
                        isAdmin[0] = false;
                        Toast.makeText(getContext(), "Unauthorized access.", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "isCurrentUserAdmin: User is not an admin.");
                        // Optionally, navigate back or close the fragment
                        getParentFragmentManager().popBackStack();
                    }
                }
                else{
                    Log.e(TAG, "isCurrentUserAdmin: User data not found.");
                    Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                    // Optionally, navigate back or close the fragment
                    getParentFragmentManager().popBackStack();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG, "isCurrentUserAdmin: Database error: " + error.getMessage());
                Toast.makeText(getContext(), "Failed to verify admin status.", Toast.LENGTH_SHORT).show();
                // Optionally, navigate back or close the fragment
                getParentFragmentManager().popBackStack();
            }
        });

        // Since Firebase calls are asynchronous, return false by default
        // The actual check and navigation are handled in the callback
        return isAdmin[0];
    }

    // Fetch all reports from Firebase
    private void fetchReports(){
        progressBar.setVisibility(View.VISIBLE);
        reportsRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                reportList.clear();
                for (DataSnapshot reportSnapshot : dataSnapshot.getChildren()){
                    Report report = reportSnapshot.getValue(Report.class);
                    if (report != null){
                        report.setReportId(reportSnapshot.getKey()); // Set the unique reportId
                        reportList.add(report);
                    }
                }
                reportAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load reports: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to load reports: " + databaseError.getMessage());
            }
        });
    }

    // Method to remove a report
    public void removeReport(String reportId){
        if (reportId == null || reportId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid report ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        reportsRef.child(reportId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Report removed successfully.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Report " + reportId + " removed.");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to remove report.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to remove report " + reportId, e);
                });
    }
}
