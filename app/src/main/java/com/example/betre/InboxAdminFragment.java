package com.example.betre;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betre.adapters.ReportedCommentAdapter;
import com.example.betre.models.ReportedComment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment for admin to view and manage reported comments.
 */
public class InboxAdminFragment extends Fragment {

    private RecyclerView reportedCommentsRecyclerView;
    private ProgressBar adminProgressBar;
    private ReportedCommentAdapter adapter;
    private List<ReportedComment> reportedCommentsList;
    private DatabaseReference reportsRef;

    public InboxAdminFragment() {
        // Required empty public constructor
    }

    public static InboxAdminFragment newInstance() {
        return new InboxAdminFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox_admin, container, false);

        reportedCommentsRecyclerView = view.findViewById(R.id.reported_comments_recycler_view);
        adminProgressBar = view.findViewById(R.id.admin_progress_bar);

        reportedCommentsList = new ArrayList<>();
        adapter = new ReportedCommentAdapter(reportedCommentsList);
        reportedCommentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        reportedCommentsRecyclerView.setAdapter(adapter);

        reportsRef = FirebaseDatabase.getInstance().getReference("report_comments");
        fetchReportedComments();

        return view;
    }

    private void fetchReportedComments() {
        adminProgressBar.setVisibility(View.VISIBLE);

        reportsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportedCommentsList.clear();

                for (DataSnapshot reportSnapshot : snapshot.getChildren()) {
                    ReportedComment reportedComment = reportSnapshot.getValue(ReportedComment.class);
                    if (reportedComment != null) {
                        reportedComment.setReportId(reportSnapshot.getKey());
                        reportedCommentsList.add(reportedComment);
                    }
                }
                adapter.notifyDataSetChanged();
                adminProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load reported comments.", Toast.LENGTH_SHORT).show();
                adminProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Adapter for reported comments with admin action options.
     */
    private class ReportedCommentAdapter extends RecyclerView.Adapter<ReportedCommentAdapter.ReportedCommentViewHolder> {
        private List<ReportedComment> reportedCommentsList;

        public ReportedCommentAdapter(List<ReportedComment> reportedCommentsList) {
            this.reportedCommentsList = reportedCommentsList;
        }

        @NonNull
        @Override
        public ReportedCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reported_comment, parent, false);
            return new ReportedCommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportedCommentViewHolder holder, int position) {
            ReportedComment report = reportedCommentsList.get(position);

            holder.commentOwnerText.setText("Comment Owner: " + report.getReportedCommentUserId());
            holder.reportedByText.setText("Reported By: " + report.getReportedById());
            holder.contentText.setText("Content: " + report.getContent());
            holder.reasonText.setText("Reason: " + report.getReason());

            holder.itemView.setOnClickListener(v -> showActionsDialog(report, position));
        }

        @Override
        public int getItemCount() {
            return reportedCommentsList.size();
        }

        private void showActionsDialog(ReportedComment report, int position) {
            CharSequence[] options = {"Suspend User", "Give Warning", "Mark as Reviewed", "Delete Comment"};

            new AlertDialog.Builder(getContext())
                    .setTitle("Admin Actions")
                    .setItems(options, (dialog, which) -> {
                        switch (which) {
                            case 0: suspendUser(report.getReportedCommentUserId()); break;
                            case 1: showWarningDialog(report.getReportedCommentUserId()); break;
                            case 2: markCommentAsReviewed(report.getReportId(), position); break;
                            case 3: deleteComment(report.getPostId(), report.getReportId(), position); break;
                        }
                    })
                    .show();
        }

        private void suspendUser(String userId) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("role").setValue("suspended")
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "User suspended.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to suspend user.", Toast.LENGTH_SHORT).show());
        }

        private void showWarningDialog(String commentOwnerId) {
            View warningView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_warning, null);
            final TextView warningMessageInput = warningView.findViewById(R.id.warningMessageInput);

            new AlertDialog.Builder(getContext())
                    .setTitle("Send Warning")
                    .setView(warningView)
                    .setPositiveButton("Send", (dialog, which) -> {
                        String warningMessage = warningMessageInput.getText().toString().trim();
                        giveWarning(commentOwnerId, warningMessage);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void giveWarning(String commentOwnerId, String message) {
            DatabaseReference warningRef = FirebaseDatabase.getInstance().getReference("warnings").child(commentOwnerId).push();
            warningRef.setValue(new HashMap<String, Object>() {{
                        put("userId", commentOwnerId);
                        put("reason", message);
                        put("timestamp", System.currentTimeMillis());
                    }})
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Warning issued.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to issue warning.", Toast.LENGTH_SHORT).show());
        }

        private void markCommentAsReviewed(String reportId, int position) {
            reportsRef.child(reportId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        reportedCommentsList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(getContext(), "Report marked as reviewed.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to mark as reviewed.", Toast.LENGTH_SHORT).show());
        }

        private void deleteComment(String postId, String commentId, int position) {
            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("comments").child(postId).child(commentId);
            commentRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        reportsRef.child(commentId).removeValue();
                        reportedCommentsList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(getContext(), "Comment deleted.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete comment.", Toast.LENGTH_SHORT).show());
        }

        public class ReportedCommentViewHolder extends RecyclerView.ViewHolder {
            TextView commentOwnerText, reportedByText, contentText, reasonText;

            public ReportedCommentViewHolder(@NonNull View itemView) {
                super(itemView);
                commentOwnerText = itemView.findViewById(R.id.comment_owner_text);
                reportedByText = itemView.findViewById(R.id.reported_by_text);
                contentText = itemView.findViewById(R.id.comment_content_text);
                reasonText = itemView.findViewById(R.id.report_reason_text);
            }
        }
    }

    /**
     * Model for each reported comment.
     */
    public static class ReportedComment {
        private String reportId;
        private String postId;
        private String content;
        private String reportedById;
        private String reportedCommentUserId;
        private String reason;

        // Getters and setters for Firebase deserialization
        public String getReportId() { return reportId; }
        public void setReportId(String reportId) { this.reportId = reportId; }
        public String getPostId() { return postId; }
        public void setPostId(String postId) { this.postId = postId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getReportedById() { return reportedById; }
        public void setReportedById(String reportedById) { this.reportedById = reportedById; }
        public String getReportedCommentUserId() { return reportedCommentUserId; }
        public void setReportedCommentUserId(String reportedCommentUserId) { this.reportedCommentUserId = reportedCommentUserId; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
