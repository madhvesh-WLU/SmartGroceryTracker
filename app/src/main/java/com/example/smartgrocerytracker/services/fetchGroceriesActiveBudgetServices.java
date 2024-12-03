package com.example.smartgrocerytracker.services;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
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
public class fetchGroceriesActiveBudgetServices {

    private static final String TAG = "fetchGroceryServices";

    // Callback interface
    public interface GroceryFetchListener {
        void onGroceryFetched(List<GroceryItemModel> groceryItems);
    }

    public static void fetchGroceriesActiveBudget(Context context, RequestQueue queue, String budget_id,GroceryFetchListener listener) {
        String token = SecurePreferences.getAuthToken(context);
        String url = Config.ACTIVE_BUDGET_GROCERY_URL + budget_id;

        JsonObjectRequest fetchUserRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray data = response.getJSONArray("data");
                            List<GroceryItemModel> groceryItems = new ArrayList<>();

                            // Parse the response
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject groceryObject = data.getJSONObject(i);

                                // Extract only the required fields
                                String itemId = groceryObject.getString("item_id");
                                String itemName = groceryObject.getString("item_name");
                                double price = groceryObject.getDouble("price");
                                int quantity = groceryObject.getInt("quantity");
                                String category = groceryObject.optString("category", "Uncategorized");

                                // Create a GroceryItemModel object
                                GroceryItemModel groceryItem = new GroceryItemModel(
                                        itemId, // itemId is not required
                                        itemName,
                                        null, // userId is not required
                                        quantity,
                                        category,
                                        price,
                                        false // Assuming "purchased" is not needed in this context
                                );

                                groceryItems.add(groceryItem);
                            }

                            // Pass the parsed list to the listener
                            listener.onGroceryFetched(groceryItems);
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Response message: " + response.getString("message"));
                            listener.onGroceryFetched(new ArrayList<>());
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(context, "Failed to parse grocery data", Toast.LENGTH_SHORT).show();
                        listener.onGroceryFetched(new ArrayList<>());
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                        Toast.makeText(context, "Unauthorized, please login", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Volley error: " + error.getMessage());
                        Toast.makeText(context, "Failed to retrieve grocery details", Toast.LENGTH_SHORT).show();
                    }
                    listener.onGroceryFetched(new ArrayList<>());
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