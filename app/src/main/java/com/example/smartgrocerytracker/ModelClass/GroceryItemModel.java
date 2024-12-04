package com.example.smartgrocerytracker.ModelClass;

import java.io.Serializable;

public class GroceryItemModel implements Serializable {

    private String itemId;
    private String itemName;
    private String userId;
    private double quantity;
    private String category;
    private double price;
    private boolean purchased;

    // Constructor
    public GroceryItemModel(String itemId, String itemName, String userId, double quantity, String category, double price, boolean purchased) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.userId = userId;
        this.quantity = quantity;
        this.category = category;
        this.price = price;
        this.purchased = purchased;
    }

    public String getItemId() {
        return itemId;
    }


    // Getters and setters
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }
}
