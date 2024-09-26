package com.example.betre;

public class User {
    public String name;
    public String email;
    public String phone;
    public String gender;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String phone, String gender) { // Updated constructor
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }
}

