

package com.example.betre.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // or androidx equivalent
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betre.R;
import com.example.betre.models.ReportedComment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ReportedCommentAdapter extends RecyclerView.Adapter<ReportedCommentAdapter.ReportedCommentViewHolder> {

    private Context context;
    private List<ReportedComment> reportedCommentsList;

    public ReportedCommentAdapter(Context context, List<ReportedComment> reportedCommentsList) {
        this.context = context;
        this.reportedCommentsList = reportedCommentsList;
    }

    @NonNull
    @Override
    public ReportedCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reported_comment, parent, false);
        return new ReportedCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedCommentViewHolder holder, int position) {
        ReportedComment reportedComment = reportedCommentsList.get(position);
        Log.d("ReportedCommentAdapter", "Binding ReportedComment: " + reportedComment.getReportId());

        holder.usernameTextView.setText(reportedComment.getUsername());
        holder.reasonTextView.setText("Reported for: " + reportedComment.getReportReason());
        holder.contentTextView.setText(reportedComment.getContent());
        holder.timestampTextView.setText(android.text.format.DateFormat.format("MM/dd/yyyy HH:mm", reportedComment.getTimestamp()));

        // Handle Delete Button
        holder.deleteButton.setOnClickListener(v -> {
            deleteComment(reportedComment);
        });

        // Handle Remove Report Button
        holder.removeReportButton.setOnClickListener(v -> {
            removeReport(reportedComment);
        });
    }

    @Override
    public int getItemCount() {
        return reportedCommentsList.size();
    }

    public class ReportedCommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, reasonTextView, contentTextView, timestampTextView;
        Button deleteButton, removeReportButton;

        public ReportedCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.reported_comment_username);
            reasonTextView = itemView.findViewById(R.id.reported_comment_reason);
            contentTextView = itemView.findViewById(R.id.reported_comment_content);
            timestampTextView = itemView.findViewById(R.id.reported_comment_timestamp);
            deleteButton = itemView.findViewById(R.id.delete_comment_button);
            removeReportButton = itemView.findViewById(R.id.remove_report_button);
        }
    }

    private void deleteComment(ReportedComment reportedComment) {
        String commentId = reportedComment.getCommentId();
        String postId = reportedComment.getPostId();

        if (commentId == null || postId == null) {
            Toast.makeText(context, "Invalid comment or post ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("comments")
                .child(postId).child(commentId);

        commentRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Remove the report from 'report_comments' node
                    removeReport(reportedComment, false); // Pass false to avoid showing Toast again
                    Toast.makeText(context, "Comment deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("ReportedCommentAdapter", "Failed to delete comment: " + e.getMessage());
                    Toast.makeText(context, "Failed to delete comment.", Toast.LENGTH_SHORT).show();
                });
    }

    private void removeReport(ReportedComment reportedComment) {
        removeReport(reportedComment, true);
    }

    private void removeReport(ReportedComment reportedComment, boolean showToast) {
        String reportId = reportedComment.getReportId();

        if (reportId == null) {
            if (showToast) {
                Toast.makeText(context, "Invalid report ID.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Corrected node name from "reportcomment" to "report_comments"
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("report_comments")
                .child(reportId);

        reportRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    if (showToast) {
                        Toast.makeText(context, "Report removed successfully.", Toast.LENGTH_SHORT).show();
                    }
                    // Optionally, remove the item from the list and notify the adapter
                    int position = reportedCommentsList.indexOf(reportedComment);
                    if (position != -1) {
                        reportedCommentsList.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ReportedCommentAdapter", "Failed to remove report: " + e.getMessage());
                    if (showToast) {
                        Toast.makeText(context, "Failed to remove report.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
<<<<<<< HEAD


}
=======
}
>>>>>>> AdminProfile
