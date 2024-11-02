//package com.example.betre.models;
//
//import com.google.firebase.database.Exclude;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class User {
//    private String userId;
//    private String username;
//    private String email;
//    private String profileImageUrl;
//    private String phone;
//    private String phoneNumber;
//    private String bio;
//    private String gender;
//    private String role;
//    private Boolean suspended;
//    private Integer warnings;
//    private Boolean banned;
//    private Map<String, Object> additionalProperties = new HashMap<>();
//
//    @Exclude
//    public Map<String, Object> getAdditionalProperties() {
//        return additionalProperties;
//    }
//
//    public User() {}
//
//    public User(String userId, String username, String email, String profileImageUrl) {
//        this.userId = userId;
//        this.username = username;
//        this.email = email;
//        this.profileImageUrl = profileImageUrl;
//    }
//
//    public User(String username, String email, String phone, String gender, String role) {
//        this.username = username;
//        this.email = email;
//        this.phone = phone;
//        this.gender = gender;
//        this.role = role;
//    }
//
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
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
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getProfileImageUrl() {
//        return profileImageUrl;
//    }
//
//    public void setProfileImageUrl(String profileImageUrl) {
//        this.profileImageUrl = profileImageUrl;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//    public String getRole() {
//        return role;
//    }
//
//    public void setRole(String role) {
//        this.role = role;
//    }
//
//    public Boolean getSuspended() {
//        return suspended != null ? suspended : false;
//    }
//
//    public void setSuspended(Boolean suspended) {
//        this.suspended = suspended;
//    }
//
//    public Integer getWarnings() {
//        return warnings;
//    }
//
//    public void setWarnings(Integer warnings) {
//        this.warnings = warnings;
//    }
//
//    public Boolean getBanned() {
//        return banned;
//    }
//
//    public void setBanned(Boolean banned) {
//        this.banned = banned;
//    }
//
////    @Exclude
////    public Map<String, Object> getAdditionalProperties() {
////        return additionalProperties;
////    }
//
//    public void setAdditionalProperty(String key, Object value) {
//        this.additionalProperties.put(key, value);
//    }
//
//    public String getPhoneNumber() {
//        return phoneNumber;
//    }
//
//    public void setPhoneNumber(String phoneNumber) {
//        this.phoneNumber = phoneNumber;
//    }
//
//    public String getBio() {
//        return bio;
//    }
//
//    public void setBio(String bio) {
//        this.bio = bio;
//    }
//
//
//
//}
//
package com.example.betre.models;

import com.google.firebase.database.Exclude;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String userId;
    private String username;
    private String email;
    private String profileImageUrl;
    private String phone;
    private String phoneNumber;
    private String bio;
    private String gender;
    private String role;
    private Boolean suspended;
    private Boolean banned;
    private Map<String, Object> warnings; // Changed from Integer to Map<String, Object>
    private Map<String, Object> additionalProperties = new HashMap<>();

    public User() {}

    public User(String userId, String username, String email, String profileImageUrl) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public User(String username, String email, String phone, String gender, String role) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.role = role;
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    @Exclude
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAdditionalProperty(String key, Object value) {
        this.additionalProperties.put(key, value);
    }

    public String getUsername() {
        return username;
    }

    private String getUsernameFromUserId(String userId) {
        // Implement this method to fetch the username from the userId
        // For simplicity, return a placeholder or fetch from a cached map
        return "Username"; // Replace with actual implementation
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Object> getWarnings() {
        return warnings;
    }

    public void setWarnings(Map<String, Object> warnings) {
        this.warnings = warnings;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public Boolean getSuspended() {
        return suspended != null ? suspended : false;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    public String getRole() {
        return role;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }
}
