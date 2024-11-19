package com.example.smartgrocerytracker.ModelClass;

public class ExpenseModel {

    private String expenseId;
    private String billName;      // Considered as the bill name
    private double billAmount;    // Total bill amount
    private String dateOfPurchase;
    private String description;
    private String budgetId;
    private String storeId;
    private String userId;
    private String createdAt;

    // New fields for item-specific details
    private String itemName;      // Name of the item
    private String category;      // Category of the item
    private int quantity;         // Quantity of the item
    private double price;         // Price per unit or item

    // Updated constructor with the new fields
    public ExpenseModel(String expenseId, String billName, double billAmount, String dateOfPurchase, String description, String budgetId, String storeId, String userId, String createdAt, String itemName, String category, int quantity, double price) {
        this.expenseId = expenseId;
        this.billName = billName;
        this.billAmount = billAmount;
        this.dateOfPurchase = dateOfPurchase;
        this.description = description;
        this.budgetId = budgetId;
        this.storeId = storeId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and setters for new fields
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Existing getters and setters for other fields remain the same
    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
