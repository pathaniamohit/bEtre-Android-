package com.example.betre.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betre.R;
import com.example.betre.models.Post;
import com.example.betre.models.ReportedPost;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Map;

public class ReportedPostAdapter extends RecyclerView.Adapter<ReportedPostAdapter.ReportedPostViewHolder> {

    private List<ReportedPost> reportedPosts;
    private Context context;

    public ReportedPostAdapter(List<ReportedPost> reportedPosts, Context context) {
        this.reportedPosts = reportedPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reported_post, parent, false);
        return new ReportedPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportedPostViewHolder holder, int position) {
        ReportedPost reportedPost = reportedPosts.get(position);
        Post post = reportedPost.getPost();

        if (post != null) {
            holder.postDescription.setText(post.getContent());
            holder.postLocation.setText("Location: " + post.getLocation());
            Glide.with(context)
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.sign1)
                    .into(holder.postImage);

            holder.reportCount.setText("Reports: " + reportedPost.getReports().size());
        }

        holder.viewReportsButton.setOnClickListener(v -> {
            showReportsDialog(reportedPost.getReports());
        });

        holder.deletePostButton.setOnClickListener(v -> {
            deletePost(reportedPost.getPostId(), position);
        });

        holder.dismissReportButton.setOnClickListener(v -> {
            dismissReport(reportedPost.getPostId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return reportedPosts.size();
    }

    public class ReportedPostViewHolder extends RecyclerView.ViewHolder {
        TextView postDescription, postLocation, reportCount;
        ImageView postImage;
        Button viewReportsButton, deletePostButton, dismissReportButton;

        public ReportedPostViewHolder(@NonNull View itemView) {
            super(itemView);
            postDescription = itemView.findViewById(R.id.reported_post_description);
            postLocation = itemView.findViewById(R.id.reported_post_location);
            reportCount = itemView.findViewById(R.id.report_count);
            postImage = itemView.findViewById(R.id.reported_post_image);
            viewReportsButton = itemView.findViewById(R.id.view_reports_button);
            deletePostButton = itemView.findViewById(R.id.delete_post_button);
            dismissReportButton = itemView.findViewById(R.id.dismiss_report_button);
        }
    }

    private void showReportsDialog(Map<String, String> reports) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Reports");

        StringBuilder message = new StringBuilder();
        for (Map.Entry<String, String> entry : reports.entrySet()) {
            message.append("User ID: ").append(entry.getKey())
                    .append("\nReason: ").append(entry.getValue())
                    .append("\n\n");
        }

        builder.setMessage(message.toString());
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    private void deletePost(String postId, int position) {
        // Remove the post from the database
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
        postRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove the reports associated with the post
                DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(postId);
                reportRef.removeValue();

                // Remove the post from the list and notify the adapter
                reportedPosts.remove(position);
                notifyItemRemoved(position);

                Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void dismissReport(String postId, int position) {
        // Remove the reports associated with the post
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(postId);
        reportRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Remove the post from the list and notify the adapter
                reportedPosts.remove(position);
                notifyItemRemoved(position);

                Toast.makeText(context, "Reports dismissed successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to dismiss reports", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

