package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fetchGroceryListServices {

    private static final String TAG = "fetchGroceryListServices";

    // In fetchGroceryListServices.java
    public interface ExpenseListFetchListener {
        void onExpensesFetched(ExpenseModel expense);
//        void onExpensesListFetched(List<GroceryItemModel> groceryItems);
    }

    public static void fetchGroceryList(
            Context context,
            RequestQueue queue,
            String expenseId,
            ExpenseListFetchListener listener) {

        String token = SecurePreferences.getAuthToken(context);
        String url = Config.GET_FULL_EXPENSES_URL + expenseId;

        JsonObjectRequest fetchUserRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                // Parse ExpenseModel fields
                String billName = response.optString("bill_name",null);
                double billAmount = response.optDouble("bill_amount");
                double totalQuantity = response.optDouble("total_quantity");
                String dateOfPurchase = response.optString("date_of_purchase",null);
                String description = response.optString("description",null);
                String budgetId = response.optString("budget_id",null);
                String storeId = response.optString("store_id", null);
//                String userId = response.optString("user_id",null);
                String createdAt = response.optString("created_at",null);

                // Parse grocery items
                List<GroceryItemModel> groceryItems = new ArrayList<>();
                JSONArray itemsArray = response.getJSONArray("grocery_items");
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject item = itemsArray.getJSONObject(i);
                    GroceryItemModel groceryItem = new GroceryItemModel(
                            item.getString("item_id"),
                            item.getString("item_name"),
                            null,
                            item.getInt("quantity"),
                            item.getString("category"),
                            item.getDouble("price"),
                            item.getBoolean("purchased")
                    );
                    groceryItems.add(groceryItem);
                }

                // Notify grocery items listener
//                listener.onExpensesListFetched(groceryItems);

                // Create ExpenseModel
                ExpenseModel expense = new ExpenseModel(
                        expenseId,
                        billName,
                        billAmount,
                        totalQuantity,
                        dateOfPurchase,
                        description,
                        budgetId,
                        storeId,
                        null,
                        createdAt,
                        groceryItems
                );

                // Notify expense listener
                listener.onExpensesFetched(expense);

            } catch (JSONException e) {
                Log.e(TAG, "JSON Parsing error: " + e.getMessage());
                Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.e(TAG, "Volley error: " + error.getMessage());
            Toast.makeText(context, "Failed to retrieve expense details", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(fetchUserRequest);
    }

}
