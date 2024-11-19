package com.example.smartgrocerytracker.ui.profile;

public class UserProfile {

    private static UserProfile instance;
    private String username;
    private String email;
    private String userId;
    private String budget_id;

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
    public void setUserData(String username, String email, String userId, String budget_id) {
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.budget_id = budget_id;
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

    public String getBudget_id() {
        return budget_id;
    }


    public void clearData() {
        username = null;
        email = null;
        userId = null;
        budget_id= null;
    }
}
