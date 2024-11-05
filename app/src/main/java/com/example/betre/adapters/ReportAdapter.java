////// File: ReportAdapter.java
////package com.example.betre.adapters;
////
////import android.content.Context;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.Button;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import androidx.annotation.NonNull;
////import androidx.recyclerview.widget.RecyclerView;
////
////import com.example.betre.FlaggedAdminFragment;
////import com.example.betre.R;
////import com.example.betre.models.Report;
////
////import java.text.SimpleDateFormat;
////import java.util.Date;
////import java.util.List;
////import java.util.Locale;
////
////public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
////
////    private List<Report> reportList;
////    private Context context;
////    private FlaggedAdminFragment fragment;
////
////    public ReportAdapter(List<Report> reportList, Context context, FlaggedAdminFragment fragment){
////        this.reportList = reportList;
////        this.context = context;
////        this.fragment = fragment;
////    }
////
////    @NonNull
////    @Override
////    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
////        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
////        return new ReportViewHolder(view);
////    }
////
////    @Override
////    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position){
////        Report report = reportList.get(position);
////        holder.reportedUserId.setText("Reported User ID: " + report.getReportedUserId());
////        holder.reportingUserId.setText("Reporting User ID: " + report.getReportingUserId());
////        holder.reason.setText("Reason: " + report.getReason());
////
////        // Format timestamp to readable date
////        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
////                .format(new Date(report.getTimestamp()));
////        holder.timestamp.setText("Reported On: " + formattedDate);
////
////        holder.removeReportButton.setOnClickListener(v -> {
////            // Optional: Confirm before removing
////            Toast.makeText(context, "Removing report...", Toast.LENGTH_SHORT).show();
////            // Implement confirmation dialog if necessary
////            fragment.removeReport(report.getReportedUserId()); // Assuming reportedUserId is unique
////        });
////    }
////
////    @Override
////    public int getItemCount(){
////        return reportList.size();
////    }
////
////    public static class ReportViewHolder extends RecyclerView.ViewHolder{
////        public TextView reportedUserId;
////        public TextView reportingUserId;
////        public TextView reason;
////        public TextView timestamp;
////        public Button removeReportButton;
////
////        public ReportViewHolder(View itemView){
////            super(itemView);
////            reportedUserId = itemView.findViewById(R.id.reported_user_id);
////            reportingUserId = itemView.findViewById(R.id.reporting_user_id);
////            reason = itemView.findViewById(R.id.report_reason);
////            timestamp = itemView.findViewById(R.id.report_timestamp);
////            removeReportButton = itemView.findViewById(R.id.remove_report_button);
////        }
////    }
////}
////
//
//// File: ReportAdapter.java
//package com.example.betre.adapters;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.betre.FlaggedAdminFragment;
//import com.example.betre.R;
//import com.example.betre.models.Report;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
//
//    private List<Report> reportList;
//    private Context context;
//    private FlaggedAdminFragment fragment;
//
//    public ReportAdapter(List<Report> reportList, Context context, FlaggedAdminFragment fragment){
//        this.reportList = reportList;
//        this.context = context;
//        this.fragment = fragment;
//    }
//
//    @NonNull
//    @Override
//    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
//        return new ReportViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position){
//        Report report = reportList.get(position);
//        holder.reportedUserId.setText("Reported User ID: " + report.getReportedUserId());
//        holder.reportingUserId.setText("Reporting User ID: " + report.getReportingUserId());
//        holder.reason.setText("Reason: " + report.getReason());
//
//        // Format timestamp to readable date
//        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
//                .format(new Date(report.getTimestamp()));
//        holder.timestamp.setText("Reported On: " + formattedDate);
//
//        holder.removeReportButton.setOnClickListener(v -> {
//            // Optional: Confirm before removing
//            new AlertDialog.Builder(context)
//                    .setTitle("Remove Report")
//                    .setMessage("Are you sure you want to remove this report?")
//                    .setPositiveButton("Yes", (dialog, which) -> {
//                        fragment.removeReport(report.getReportId());
//                    })
//                    .setNegativeButton("No", null)
//                    .show();
//        });
//    }
//
//    @Override
//    public int getItemCount(){
//        return reportList.size();
//    }
//
//    public static class ReportViewHolder extends RecyclerView.ViewHolder{
//        public TextView reportedUserId;
//        public TextView reportingUserId;
//        public TextView reason;
//        public TextView timestamp;
//        public Button removeReportButton;
//
//        public ReportViewHolder(View itemView){
//            super(itemView);
//            reportedUserId = itemView.findViewById(R.id.reported_user_id);
//            reportingUserId = itemView.findViewById(R.id.reporting_user_id);
//            reason = itemView.findViewById(R.id.report_reason);
//            timestamp = itemView.findViewById(R.id.report_timestamp);
//            removeReportButton = itemView.findViewById(R.id.remove_report_button);
//        }
//    }
//}
// File: ReportAdapter.java
package com.example.betre.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betre.FlaggedAdminFragment;
import com.example.betre.R;
import com.example.betre.models.Report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<Report> reportList;
    private Context context;
    private FlaggedAdminFragment fragment;

    public ReportAdapter(List<Report> reportList, Context context, FlaggedAdminFragment fragment){
        this.reportList = reportList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position){
        Report report = reportList.get(position);
        holder.reportedUsername.setText("Reported User: " + report.getReportedUsername());
        holder.reportingUsername.setText("Reporting User: " + report.getReportingUsername());
        holder.reason.setText("Reason: " + report.getReason());

        // Format timestamp to readable date
        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(report.getTimestamp()));
        holder.timestamp.setText("Reported On: " + formattedDate);

        holder.removeReportButton.setOnClickListener(v -> {
            // Confirm before removing
            new AlertDialog.Builder(context)
                    .setTitle("Remove Report")
                    .setMessage("Are you sure you want to remove this report?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        fragment.removeReport(report.getReportId());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount(){
        return reportList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder{
        public TextView reportedUsername;
        public TextView reportingUsername;
        public TextView reason;
        public TextView timestamp;
        public Button removeReportButton;

        public ReportViewHolder(View itemView){
            super(itemView);
            reportedUsername = itemView.findViewById(R.id.reported_username);
            reportingUsername = itemView.findViewById(R.id.reporting_username);
            reason = itemView.findViewById(R.id.report_reason);
            timestamp = itemView.findViewById(R.id.report_timestamp);
            removeReportButton = itemView.findViewById(R.id.remove_report_button);
        }
    }
}
