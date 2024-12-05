package com.example.smartgrocerytracker.utils;

import java.util.Arrays;
import java.util.List;

public class GlobalSearchUtils {



    public static String[] getYears() {
        return new String[]{"2024", "2023", "2022", "2021", "2020"};
    }

    public static String[] getMonths() {
        return new String[]{"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
    }

    public static String getMonthNumber(String monthName) {
        switch (monthName) {
            case "January": return "01";
            case "February": return "02";
            case "March": return "03";
            case "April": return "04";
            case "May": return "05";
            case "June": return "06";
            case "July": return "07";
            case "August": return "08";
            case "September": return "09";
            case "October": return "10";
            case "November": return "11";
            case "December": return "12";
            default: return ""; // Return an empty string if the month name is not valid
        }
    }

    public static List<String> getCategories() {
        List<String> categories = Arrays.asList(
                "Hummus, Dips, & Salsa",
                "Energy Drinks",
                "Oils & Shortening",
                "Fresh Soups & Salads",
                "Game Time Faves",
                "Sparkling Water",
                "Better for you",
                "Healthy Snacks",
                "Pudding & Gelatin",
                "Pretzels",
                "Granola Bars",
                "Baking Nuts & Seeds",
                "Yeast",
                "Milk",
                "Cheese",
                "Halloween candy",
                "Canned & Powdered Milks",
                "Specialty Cheeses & Meats",
                "Bacon, Hot Dogs, Sausage",
                "Variety Pack Snacks",
                "Drink Mixes",
                "Eggs",
                "Orange Juice & Chilled",
                "Cream & Creamers",
                "Beverage Deals",
                "Hard candy & lollipops",
                "Meat Sticks",
                "Fruit Snacks",
                "Chocolate",
                "Fresh Pizza",
                "Sugars & Sweeteners",
                "New Arrivals",
                "Biscuits, Cookies, Doughs",
                "Top baking brands",
                "Mints",
                "Prepared Meals & Sides",
                "Yogurt",
                "Popcorn",
                "Chips",
                "Rotisserie Chicken",
                "Juices",
                "Deli Meat & Cheese",
                "Gummy & chewy candy",
                "Non-Alcoholic Mixers",
                "Butter & Margarine",
                "Sports Drinks",
                "Multipacks & bags",
                "Crackers",
                "Fresh Pasta",
                "Flours & Meals",
                "Easy to make",
                "Snack Nuts",
                "Snacks & Fresh Sandwiches",
                "Great Value Beverages",
                "Sour Cream & Chilled Dips",
                "Kids' Multi-Packs",
                "Gum",
                "Beef Jerky",
                "Baking Soda & Starch",
                "Tea",
                "Fresh Bakery Breads",
                "Breakfast Meats",
                "Breakfast Breads",
                "Cookies & Brownies",
                "Grilling",
                "Fresh Food",
                "Rolls & Buns",
                "Cakes & Cupcakes",
                "Emergency & Institutional food",
                "Cereal & Granola",
                "Dairy & Eggs",
                "Breakfast Beverages",
                "Coffee Accessories",
                "Tortillas",
                "Pies",
                "Donuts, Muffins & Pastries",
                "Roast Type",
                "Muffins & Pastries",
                "Sweet Treats",
                "Sliced Bread",
                "Pancakes, Waffles & Syrups",
                "Herbs, spices & seasonings",
                "Frozen Meals & Snacks",
                "Fresh Vegetables",
                "Salad Kits & Bowls",
                "Frozen Potatoes",
                "Organic Produce",
                "Fresh Dressings",
                "Condiments",
                "Salsa & Dips",
                "Cooking oils & vinegars",
                "Plant-based Protein & Tofu",
                "Soup",
                "Fresh Fruits",
                "Frozen Meat & Seafood",
                "Frozen Desserts",
                "Canned goods",
                "Frozen Breakfast",
                "Cut Fruits & Vegetables",
                "Pasta & pizza",
                "International foods",
                "Coffee Additives",
                "Fresh Herbs",
                "Frozen Pizza, Pasta, & Breads",
                "Rice, grains & dried beans",
                "Frozen Produce",
                "Coffee By Type",
                "Canned vegetables",
                "Hot Cereals",
                "Packaged meals & side dishes",
                "Fresh Flowers",
                "Wine",
                "Spirits",
                "Beer"
        );

        return categories;
    }
}
