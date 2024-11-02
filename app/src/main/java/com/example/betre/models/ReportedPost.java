package com.example.betre.models;

import java.util.Map;

//public class ReportedPost {
//    private String postId;
//    private Post post;
//    private Map<String, String> reports; // Map of userId to reportReason
//
//    public String getPostId() {
//        return postId;
//    }
//
//    public void setPostId(String postId) {
//        this.postId = postId;
//    }
//
//    public Post getPost() {
//        return post;
//    }
//
//    public void setPost(Post post) {
//        this.post = post;
//    }
//
//    public Map<String, String> getReports() {
//        return reports;
//    }
//
//    public void setReports(Map<String, String> reports) {
//        this.reports = reports;
//    }
//// Constructors, getters, and setters
//}
//
public class ReportedPost {
    private String postId;
    private Post post;
    private Map<String, String> reports; // Map of userId to reportReason

    // Constructors
    public ReportedPost() {
    }

    public ReportedPost(String postId, Post post, Map<String, String> reports) {
        this.postId = postId;
        this.post = post;
        this.reports = reports;
    }

    // Getters and Setters
    public String getPostId() {
        return postId;
    }

    public Post getPost() {
        return post;
    }

    public Map<String, String> getReports() {
        return reports;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void setReports(Map<String, String> reports) {
        this.reports = reports;
    }
}
