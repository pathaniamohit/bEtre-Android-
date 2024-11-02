////package com.example.betre.adapters;
////
////import android.content.Context;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.TextView;
////
////import androidx.annotation.NonNull;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.example.betre.R;
////import com.example.betre.models.Comment;
////
////import java.util.List;
////
////public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
////
////    private Context context;
////    private List<Comment> commentList;
////
////    public CommentAdapter(Context context, List<Comment> commentList) {
////        this.context = context;
////        this.commentList = commentList;
////    }
////
////    @NonNull
////    @Override
////    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
////        return new CommentViewHolder(view);
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
////        Comment comment = commentList.get(position);
////        holder.commentText.setText(comment.getContent() + " @" + comment.getUsername());
////    }
////
////    @Override
////    public int getItemCount() {
////        return commentList.size();
////    }
////
////    public static class CommentViewHolder extends RecyclerView.ViewHolder {
////        TextView commentText;
////
////        public CommentViewHolder(@NonNull View itemView) {
////            super(itemView);
////            commentText = itemView.findViewById(R.id.commentText);
////        }
////    }
////}
////
//package com.example.betre.adapters;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.appcompat.app.AlertDialog;
//
//import com.example.betre.R;
//import com.example.betre.models.Comment;
//import com.google.android.material.button.MaterialButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.HashMap;
//import java.util.List;
//
//public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
//
//    private Context context;
//    private List<Comment> commentList;
//    private String postId;  // To identify which post the comments belong to
//
//    public CommentAdapter(Context context, List<Comment> commentList, String postId) {
//        this.context = context;
//        this.commentList = commentList;
//        this.postId = postId;
//    }
//
//    @NonNull
//    @Override
//    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context)
//                .inflate(R.layout.item_comment, parent, false);
//        return new CommentViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
//        Comment comment = commentList.get(position);
//
//        holder.commentContent.setText(comment.getContent());
//        holder.commentUsername.setText(comment.getUsername());
//        holder.commentTimestamp.setText(android.text.format.DateFormat.format("MM/dd/yyyy HH:mm", comment.getTimestamp()));
//
//        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        // Show delete button if the comment belongs to the current user
//        if (comment.getUserId().equals(currentUserId)) {
//            holder.deleteCommentButton.setVisibility(View.VISIBLE);
//            holder.deleteCommentButton.setOnClickListener(v -> {
//                deleteComment(comment.getCommentId());
//            });
//        } else {
//            holder.deleteCommentButton.setVisibility(View.GONE);
//        }
//
//        // Report comment
//        holder.reportCommentButton.setOnClickListener(v -> {
//            showReportDialog(comment);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return commentList.size();
//    }
//
//    private void deleteComment(String commentId) {
//        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("comments")
//                .child(postId)
//                .child(commentId);
//
//        commentRef.removeValue()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(context, "Comment deleted.", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(context, "Failed to delete comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void showReportDialog(Comment comment) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Report Comment");
//
//        final EditText input = new EditText(context);
//        input.setHint("Reason for reporting");
//        builder.setView(input);
//
//        builder.setPositiveButton("Report", (dialog, which) -> {
//            String reason = input.getText().toString().trim();
//            if (!reason.isEmpty()) {
//                reportComment(comment, reason);
//            } else {
//                Toast.makeText(context, "Report reason cannot be empty.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        builder.setNegativeButton("Cancel", null);
//        builder.show();
//    }
//
//    private void reportComment(Comment comment, String reason) {
//        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reportcomment");
//        String reportId = reportRef.push().getKey();
//
//        if (reportId == null) {
//            Toast.makeText(context, "Failed to report comment. Try again.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        HashMap<String, Object> reportData = new HashMap<>();
//        reportData.put("reportId", reportId);
//        reportData.put("commentId", comment.getCommentId());
//        reportData.put("postId", postId);
//        reportData.put("userId", comment.getUserId());
//        reportData.put("username", comment.getUsername());
//        reportData.put("content", comment.getContent());
//        reportData.put("timestamp", comment.getTimestamp());
//        reportData.put("reportedBy", FirebaseAuth.getInstance().getCurrentUser().getUid());
//        reportData.put("reportReason", reason);
//        reportData.put("reportTimestamp", System.currentTimeMillis());
//
//        reportRef.child(reportId).setValue(reportData)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(context, "Comment reported.", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(context, "Failed to report comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    public static class CommentViewHolder extends RecyclerView.ViewHolder {
//        TextView commentContent, commentUsername, commentTimestamp;
//        MaterialButton deleteCommentButton, reportCommentButton;
//
//        public CommentViewHolder(@NonNull View itemView) {
//            super(itemView);
//            commentContent = itemView.findViewById(R.id.comment_content);
//            commentUsername = itemView.findViewById(R.id.comment_username);
//            commentTimestamp = itemView.findViewById(R.id.comment_timestamp);
//            deleteCommentButton = itemView.findViewById(R.id.delete_comment_button);
//            reportCommentButton = itemView.findViewById(R.id.report_comment_button);
//        }
//    }
//}
package com.example.betre.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.example.betre.R;
import com.example.betre.models.Comment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private String postId;  // To identify which post the comments belong to

    public CommentAdapter(Context context, List<Comment> commentList, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.commentContent.setText(comment.getContent());
        holder.commentUsername.setText(comment.getUsername());
        holder.commentTimestamp.setText(android.text.format.DateFormat.format("MM/dd/yyyy HH:mm", comment.getTimestamp()));

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Show delete button if the comment belongs to the current user
        if (comment.getUserId().equals(currentUserId)) {
            holder.deleteCommentButton.setVisibility(View.VISIBLE);
            holder.deleteCommentButton.setOnClickListener(v -> {
                deleteComment(comment.getCommentId());
            });
        } else {
            holder.deleteCommentButton.setVisibility(View.GONE);
        }

        // Report comment
        holder.reportCommentButton.setOnClickListener(v -> {
            showReportDialog(comment);
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private void deleteComment(String commentId) {
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("comments")
                .child(postId)
                .child(commentId);

        commentRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Comment deleted.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showReportDialog(Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Report Comment");

        final EditText input = new EditText(context);
        input.setHint("Reason for reporting");
        builder.setView(input);

        builder.setPositiveButton("Report", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (!reason.isEmpty()) {
                reportComment(comment, reason);
            } else {
                Toast.makeText(context, "Report reason cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void reportComment(Comment comment, String reason) {
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reportcomment");
        String reportId = reportRef.push().getKey();

        if (reportId == null) {
            Toast.makeText(context, "Failed to report comment. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportId", reportId);
        reportData.put("commentId", comment.getCommentId());
        reportData.put("postId", postId);
        reportData.put("userId", comment.getUserId());
        reportData.put("username", comment.getUsername());
        reportData.put("content", comment.getContent());
        reportData.put("timestamp", comment.getTimestamp());
        reportData.put("reportedBy", FirebaseAuth.getInstance().getCurrentUser().getUid());
        reportData.put("reportReason", reason);
        reportData.put("reportTimestamp", System.currentTimeMillis());

        reportRef.child(reportId).setValue(reportData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Comment reported.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to report comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentContent, commentUsername, commentTimestamp;
        MaterialButton deleteCommentButton, reportCommentButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentContent = itemView.findViewById(R.id.comment_content);
            commentUsername = itemView.findViewById(R.id.comment_username);
            commentTimestamp = itemView.findViewById(R.id.comment_timestamp);
            deleteCommentButton = itemView.findViewById(R.id.delete_comment_button);
            reportCommentButton = itemView.findViewById(R.id.report_comment_button);
        }
    }
}
