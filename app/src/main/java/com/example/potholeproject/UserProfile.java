package com.example.potholeproject;

public class UserProfile {
    private String name, email, mobile, pass;
    private String userID;

    public UserProfile() {
    }

    public UserProfile(String name, String email, String mobile, String pass, String id) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.pass = pass;
        this.userID = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPass() {
        return pass;
    }

    public String getUserID() {
        return userID;
    }
}
