package com.example.smartgrocerytracker.ModelClass;

public class BudgetModel {
    private String budgetId;
    private String userId;
    private double budgetAmount;
    private double spentAmount;
    private String startDate;
    private String endDate;

    // Constructor
    public BudgetModel(String budgetId, String userId, double budgetAmount, double spentAmount, String startDate, String endDate) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.budgetAmount = budgetAmount;
        this.spentAmount = spentAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(double budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
