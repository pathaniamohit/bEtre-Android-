package com.example.betre.models;

public class Report {
    private String reportId;

    private String postId;
    private String userId;
    private String reason;
    private String reportedUserId;
    private String reportingUserId;
    private String reportedUsername;
    private String reportingUsername;
    private String reporterId;
    private long timestamp;


    public Report() {}

    public Report(String reason, String reporterId, String reportedUserId) {
        this.reason = reason;
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
    }


    public Report(String reportedUserId, String reportingUserId, String reason, long timestamp) {
        this.reportedUserId = reportedUserId;
        this.reportingUserId = reportingUserId;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public Report(String reportId, String reportedUserId, String reportingUserId, String reason, long timestamp) {
        this.reportId = reportId;
        this.reportedUserId = reportedUserId;
        this.reportingUserId = reportingUserId;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public Report(String reportId, String reportedUserId, String reportingUserId, String reason, long timestamp, String reportedUsername, String reportingUsername) {
        this.reportId = reportId;
        this.reportedUserId = reportedUserId;
        this.reportingUserId = reportingUserId;
        this.reason = reason;
        this.timestamp = timestamp;
        this.reportedUsername = reportedUsername;
        this.reportingUsername = reportingUsername;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(String reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportingUserId() {
        return reportingUserId;
    }

    public void setReportingUserId(String reportingUserId) {
        this.reportingUserId = reportingUserId;
    }



    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Optionally, override toString() for easier logging
//    @Override
//    public String toString() {
//        return "Report{" +
//                "reportedUserId='" + reportedUserId + '\'' +
//                ", reportingUserId='" + reportingUserId + '\'' +
//                ", reason='" + reason + '\'' +
//                ", timestamp=" + timestamp +
//                '}';
//    }

    public String getReportedUsername() {
        return reportedUsername;
    }

    public void setReportedUsername(String reportedUsername) {
        this.reportedUsername = reportedUsername;
    }

    public String getReportingUsername() {
        return reportingUsername;
    }

    public void setReportingUsername(String reportingUsername) {
        this.reportingUsername = reportingUsername;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    // Optional: Override toString() for easier logging
    @Override
    public String toString() {
        return "Report{" +
                "reportId='" + reportId + '\'' +
                ", reportedUserId='" + reportedUserId + '\'' +
                ", reportingUserId='" + reportingUserId + '\'' +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                ", reportedUsername='" + reportedUsername + '\'' +
                ", reportingUsername='" + reportingUsername + '\'' +
                '}';
    }
}
