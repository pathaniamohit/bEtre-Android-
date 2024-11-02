package com.example.betre.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.betre.R;
import com.example.betre.models.Report;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private final Context context;
    private final List<Report> reportList;

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.reporterInfoTextView.setText(report.getReporterInfo());
        holder.reasonTextView.setText(report.getReason());
        holder.reportedUserIdTextView.setText(report.getReportedUserId());

        // Handle dismiss button click
        holder.dismissButton.setOnClickListener(v -> dismissReport(report, position));
    }

    private void dismissReport(Report report, int position) {
        DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference("reports").child(report.getReportId());
        reportRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    reportList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Report dismissed", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to dismiss report", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reporterInfoTextView, reasonTextView, reportedUserIdTextView;
        Button dismissButton;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reporterInfoTextView = itemView.findViewById(R.id.reporter_info);
            reasonTextView = itemView.findViewById(R.id.report_reason);
            reportedUserIdTextView = itemView.findViewById(R.id.reported_user_id);
            dismissButton = itemView.findViewById(R.id.dismiss_button);
        }
    }
}
