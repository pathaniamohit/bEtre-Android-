package com.example.betre.models;



public class Report {
    private String postId;
    private String userId;
    private String reason;

    public Report() {
    }

    public Report(String postId, String userId, String reason) {
        this.postId = postId;
        this.userId = userId;
        this.reason = reason;
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
}
