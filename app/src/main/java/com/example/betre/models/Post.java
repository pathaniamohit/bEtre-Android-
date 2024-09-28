package com.example.betre.models;

public class Post {
    private String userName;
    private String userEmail;
    private String userProfileImage;
    private String postImageUrl;
    private String postDescription;
    private String postLocation;
    private int likeCount;
    private int commentCount;

    public Post() {
    }

    public Post(String userName, String userEmail, String userProfileImage, String postImageUrl, String postDescription, String postLocation, int likeCount, int commentCount) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfileImage = userProfileImage;
        this.postImageUrl = postImageUrl;
        this.postDescription = postDescription;
        this.postLocation = postLocation;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostLocation() {
        return postLocation;
    }

    public void setPostLocation(String postLocation) {
        this.postLocation = postLocation;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

}
