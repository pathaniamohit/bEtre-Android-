package com.example.betre.models;

public class Notification {
    private String postId;
    private String type;
    private String username;
    private String content;
    private long timestamp;
    private String userId;


    public Notification() {
        // Default constructor required for Firebase
    }

    public Notification(String postId, String type, String username, String content, long timestamp) {
        this.type = type;
        this.postId = postId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Notification(String userId, String postId, String type, String username, String content, long timestamp) {
        this.type = type;
        this.userId = userId;
        this.postId = postId;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
