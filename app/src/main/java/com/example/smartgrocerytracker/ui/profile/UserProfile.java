package com.example.smartgrocerytracker.ui.profile;

public class UserProfile {

    private static UserProfile instance;
    private String username;
    private String email;
    private String userId;

    // Private constructor to prevent instantiation
    private UserProfile() {}

    // Get the singleton instance
    public static synchronized UserProfile getInstance() {
        if (instance == null) {
            instance = new UserProfile();
        }
        return instance;
    }

    // Setters to update the user data
    public void setUserData(String username, String email, String userId) {
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public void clearData() {
        username = null;
        email = null;
        userId = null;
    }
}
