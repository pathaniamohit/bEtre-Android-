package com.example.betre.models;

import java.util.Map;

public class Post {

    private String userName;
    private String userEmail;
    private String userProfileImage;
    private String content;
    private int count_comment;
    private int count_like;
    private String imageUrl;
    private boolean is_reported;
    private String location;
    private long timestamp;
    private String userId;
    private String postId;
    private Map<String, Comment> comments;

    public Post() {
    }

    public Post(String userName, String userEmail, String userProfileImage, String content, int count_comment, int count_like, String imageUrl, boolean is_reported, String location, long timestamp, String userId) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfileImage = userProfileImage;
        this.content = content;
        this.count_comment = count_comment;
        this.count_like = count_like;
        this.imageUrl = imageUrl;
        this.is_reported = is_reported;
        this.location = location;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public String getContent() {
        return content;
    }

    public int getCount_comment() {
        return count_comment;
    }

    public int getCount_like() {
        return count_like;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isReported() {
        return is_reported;
    }

    public String getLocation() {
        return location;
    }

    public long getTimestamp() {
        return timestamp;
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

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }
}


