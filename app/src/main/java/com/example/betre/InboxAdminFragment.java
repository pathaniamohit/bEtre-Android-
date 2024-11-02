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
// */
//public class InboxAdminFragment extends Fragment {
//
//    public InboxAdminFragment() {
//        // Required empty public constructor
//    }
//
//    public static InboxAdminFragment newInstance() {
//        return new InboxAdminFragment();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState){
//        // Inflate an empty or default layout
//        return inflater.inflate(R.layout.fragment_inbox_admin, container, false);
//    }
//}
//

package com.example.betre;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.betre.adapters.ReportedCommentAdapter;
import com.example.betre.models.ReportedComment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment to allow admin to view and manage reported comments.
 */
public class InboxAdminFragment extends Fragment {

    private RecyclerView reportedCommentsRecyclerView;
    private ProgressBar adminProgressBar;
    private ReportedCommentAdapter adapter;
    private List<ReportedComment> reportedCommentsList;

    public InboxAdminFragment() {
        // Required empty public constructor
    }

    public static InboxAdminFragment newInstance() {
        return new InboxAdminFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        reportedCommentsRecyclerView = view.findViewById(R.id.reported_comments_recycler_view);
        adminProgressBar = view.findViewById(R.id.admin_progress_bar);

        reportedCommentsList = new ArrayList<>();
        adapter = new ReportedCommentAdapter(getContext(), reportedCommentsList);
        reportedCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reportedCommentsRecyclerView.setAdapter(adapter);

        // Fetch reported comments from Firebase
        fetchReportedComments();
    }

    private void fetchReportedComments(){
        adminProgressBar.setVisibility(View.VISIBLE);

        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reportcomment");

        // Listen for all reported comments
        reportsRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                reportedCommentsList.clear();

                for (DataSnapshot reportSnapshot : snapshot.getChildren()){
                    ReportedComment reportedComment = reportSnapshot.getValue(ReportedComment.class);
                    if (reportedComment != null){
                        reportedComment.setReportId(reportSnapshot.getKey());
                        reportedCommentsList.add(reportedComment);
                    }
                }

                adapter.notifyDataSetChanged();
                adminProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Toast.makeText(getContext(), "Failed to load reported comments.", Toast.LENGTH_SHORT).show();
                adminProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
