package com.example.betre;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.betre.adapters.ReportAdapter;
import com.example.betre.models.Report;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class InboxModFragment extends Fragment {

    private RecyclerView recyclerViewReports;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;
    private DatabaseReference reportsReference;
    private static final String TAG = "InboxModFragment";

    public InboxModFragment() {
        // Required empty public constructor
    }

    public static InboxModFragment newInstance() {
        return new InboxModFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox_mod, container, false);

        recyclerViewReports = view.findViewById(R.id.recyclerViewReports);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(getContext()));

        reportList = new ArrayList<>();
        reportAdapter = new ReportAdapter(getContext(), reportList);
        recyclerViewReports.setAdapter(reportAdapter);

        // Firebase reference for reports
        reportsReference = FirebaseDatabase.getInstance().getReference("reports");

        loadReportData();

        return view;
    }

    private void loadReportData() {
        reportsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportList.clear();
                for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                    Report report = reportSnapshot.getValue(Report.class);
                    if (report != null) {
                        report.setReportId(reportSnapshot.getKey());

                        // Check if reporterId is not null before fetching reporter info
                        if (report.getReporterId() != null && !report.getReporterId().isEmpty()) {
                            fetchReporterInfo(report);
                        } else {
                            // Add report to the list directly if reporter info is missing
                            reportList.add(report);
                        }
                    }
                }
                reportAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading report data", error.toException());
                Toast.makeText(getContext(), "Failed to load report data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchReporterInfo(Report report) {
        String reporterId = report.getReporterId();

        // Null check for reporterId
        if (reporterId == null || reporterId.isEmpty()) {
            Log.e(TAG, "fetchReporterInfo: Reporter ID is null or empty.");
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(reporterId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String reporterInfo = snapshot.child("username").getValue(String.class);
                if (reporterInfo != null) {
                    report.setReporterInfo(reporterInfo);
                    reportList.add(report);
                    reportAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading reporter info", error.toException());
            }
        });
    }
}
