package com.example.betre.models;

public class Notification {
    private String postId;
    private String type;
    private String username;
    private String content;
    private long timestamp;
    private String userId;
    private String notificationId;
    private String profileImageUrl;

    // Default constructor
    public Notification() {}

    // Constructor for warning notifications
    public Notification(String notificationId, String type, String userId, long timestamp, String username, String content) {
        this.notificationId = notificationId;
        this.type = type;
        this.userId = userId;
        this.timestamp = timestamp;
        this.username = username;
        this.content = content;
    }

    // Constructor for comment notifications
    public Notification(String notificationId, String type, String userId, String postId, long timestamp, String username, String content) {
        this.notificationId = notificationId;
        this.type = type;
        this.userId = userId;
        this.postId = postId;
        this.timestamp = timestamp;
        this.username = username;
        this.content = content;
    }

    // Overloaded constructor for simpler notification creation
    public Notification(String userId, String type, String username, String content, long timestamp, String notificationId) {
        this.userId = userId;
        this.type = type;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.notificationId = notificationId;
    }


    // Constructor
    public Notification(String userId, String postId, String type, String username, String content, long timestamp, String notificationId, String profileImageUrl) {
        this.userId = userId;
        this.postId = postId;
        this.type = type;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.notificationId = notificationId;
        this.profileImageUrl = profileImageUrl;
    }

    // Getter and Setter for notificationId
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    // Getter and Setter for profileImageUrl
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getDescription() {
        switch (type) {
            case "comment":
                return username + " commented: " + (content != null ? content : "");
            case "like":
                return username + " liked your post.";
            case "follow":
                return username + " started following you.";
            case "unfollow":
                return username + " unfollowed you.";
            case "report":
                return "Reported for: " + (content != null ? content : "No reason specified");
            case "warning":
                return "Admin warned you: " + (content != null ? content : "No reason provided");
            default:
                return "Unknown notification type";
        }
    }

    // Getter and Setter for postId
    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    // Getter and Setter for type
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Getter and Setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Getter and Setter for timestamp
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Getter and Setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
