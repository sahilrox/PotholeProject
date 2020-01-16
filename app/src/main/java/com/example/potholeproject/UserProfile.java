package com.example.potholeproject;

public class UserProfile {
    public String name, email, mobile, pass;

    public UserProfile() {
    }

    public UserProfile(String name, String email, String mobile, String pass) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.pass = pass;
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
}
