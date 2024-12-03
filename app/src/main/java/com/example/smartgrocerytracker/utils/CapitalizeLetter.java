package com.example.smartgrocerytracker.utils;

public class CapitalizeLetter {

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Return as-is if input is null or empty
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
