package com.example.betre.models;

public class Report {
    private String reportId;
    private String reason;
    private String reporterId;
    private String reportedUserId;
    private String reporterInfo; // Username or email of the reporter

    public Report() {}

    public Report(String reason, String reporterId, String reportedUserId) {
        this.reason = reason;
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getReporterId() { return reporterId; }
    public void setReporterId(String reporterId) { this.reporterId = reporterId; }
    public String getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(String reportedUserId) { this.reportedUserId = reportedUserId; }

    public String getReporterInfo() { return reporterInfo; }
    public void setReporterInfo(String reporterInfo) { this.reporterInfo = reporterInfo; }
}
