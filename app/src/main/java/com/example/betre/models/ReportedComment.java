//// ReportedComment.java
//package com.example.betre.models;
//
//public class ReportedComment {
//    private String commentId;
//    private String postId;
//    private String userId;
//    private String username;
//    private String content;
//    private long timestamp;
//    private String reportedBy;
//    private long reportTimestamp;
//
//    public ReportedComment() {
//        // Default constructor
//    }
//
//    // Getters and Setters
//
//    // ... existing getters and setters
//
//    public String getCommentId() {
//        return commentId;
//    }
//
//    public void setCommentId(String commentId) {
//        this.commentId = commentId;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }
//
//    public String getPostId() {
//        return postId;
//    }
//
//    public void setPostId(String postId) {
//        this.postId = postId;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public long getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(long timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    public String getReportedBy() {
//        return reportedBy;
//    }
//
//    public void setReportedBy(String reportedBy) {
//        this.reportedBy = reportedBy;
//    }
//
//    public long getReportTimestamp() {
//        return reportTimestamp;
//    }
//
//    public void setReportTimestamp(long reportTimestamp) {
//        this.reportTimestamp = reportTimestamp;
//    }
//}
package com.example.betre.models;

public class ReportedComment {
    private String reportId;
    private String commentId;
    private String postId;
    private String userId;
    private String username;
    private String content;
    private long timestamp;
    private String reportedBy;
    private String reportReason;
    private long reportTimestamp;

    public ReportedComment() {
        // Default constructor required for calls to DataSnapshot.getValue(ReportedComment.class)
    }

    public ReportedComment(String reportId, String commentId, String postId, String userId, String username,
                           String content, long timestamp, String reportedBy, String reportReason, long reportTimestamp) {
        this.reportId = reportId;
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.reportedBy = reportedBy;
        this.reportReason = reportReason;
        this.reportTimestamp = reportTimestamp;
    }

    // Getters and Setters

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }

    public long getReportTimestamp() {
        return reportTimestamp;
    }

    public void setReportTimestamp(long reportTimestamp) {
        this.reportTimestamp = reportTimestamp;
    }
}
