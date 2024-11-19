package com.example.smartgrocerytracker.ModelClass;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;

import java.util.List;

public class ExpenseModel {

    private String expenseId;
    private String billName;
    private double billAmount;
    private String dateOfPurchase;
    private String description;
    private String budgetId;
    private String storeId;
    private String userId;
    private String createdAt;

    // New field for grocery items
    private List<GroceryItemModel> groceryItems;

    // Constructor
    public ExpenseModel(String expenseId, String billName, double billAmount, String dateOfPurchase,
                        String description, String budgetId, String storeId, String userId, String createdAt,
                        List<GroceryItemModel> groceryItems) {
        this.expenseId = expenseId;
        this.billName = billName;
        this.billAmount = billAmount;
        this.dateOfPurchase = dateOfPurchase;
        this.description = description;
        this.budgetId = budgetId;
        this.storeId = storeId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.groceryItems = groceryItems;
    }

    // Getters and setters
    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }

    public String getBillName() { return billName; }
    public void setBillName(String billName) { this.billName = billName; }

    public double getBillAmount() { return billAmount; }
    public void setBillAmount(double billAmount) { this.billAmount = billAmount; }

    public String getDateOfPurchase() { return dateOfPurchase; }
    public void setDateOfPurchase(String dateOfPurchase) { this.dateOfPurchase = dateOfPurchase; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBudgetId() { return budgetId; }
    public void setBudgetId(String budgetId) { this.budgetId = budgetId; }

    public String getStoreId() { return storeId; }
    public void setStoreId(String storeId) { this.storeId = storeId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<GroceryItemModel> getGroceryItems() { return groceryItems; }
    public void setGroceryItems(List<GroceryItemModel> groceryItems) { this.groceryItems = groceryItems; }
}
