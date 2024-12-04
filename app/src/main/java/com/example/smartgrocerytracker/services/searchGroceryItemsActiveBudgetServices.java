package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;
import com.example.smartgrocerytracker.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searchGroceryItemsActiveBudgetServices {
    private static final String TAG = "SearchGroceryItemsServices";

    // Define the listener interface
    public interface GroceryItemsFetchListener {
        void onGroceryFetched(List<GroceryItemModel> groceryItems);
    }

    /**
     * Fetches grocery items based on the search query, category, and budget ID.
     *
     * @param context    The application context.
     * @param queue      The Volley RequestQueue.
     * @param budgetId   The active budget ID.
     * @param query      The search query string.
     * @param category   The selected category.
     * @param listener   The listener to handle the fetched data.
     */
    public static void searchGroceryItemsAll(Context context, RequestQueue queue, String budgetId, String query, String category, GroceryItemsFetchListener listener) {
        String token = SecurePreferences.getAuthToken(context);

        // Build the URL with query parameters
        Uri.Builder builder = Uri.parse(Config.SEARCH_GROCERY_ACTIVE_BUDGETL + budgetId).buildUpon();
        if (query != null && !query.isEmpty()) {
            builder.appendQueryParameter("query", query);
        }
        if (category != null && !category.equalsIgnoreCase("All")) {
            builder.appendQueryParameter("category", category);
        }
        String url = builder.build().toString();
        Log.d(TAG, "Search URL: " + url);

        JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            List<GroceryItemModel> groceryItems = new ArrayList<>();

                            JSONArray itemsArray = response.getJSONArray("data");
                            for (int i = 0; i < itemsArray.length(); i++) {
                                Log.i("List", itemsArray.getJSONObject(i).toString());
                                JSONObject item = itemsArray.getJSONObject(i);
                                GroceryItemModel groceryItem = new GroceryItemModel(
                                        item.getString("item_id"),
                                        item.getString("item_name"),
                                        item.optString("user_id", null),
                                        item.getInt("quantity"),
                                        item.optString("category", null),
                                        item.getDouble("price"),
                                        item.getBoolean("purchased")
                                );
                                groceryItems.add(groceryItem);
                            }

                            listener.onGroceryFetched(groceryItems);
                        } else {
                            ToastUtils.showToast(context, "No matching grocery items found.");
                            listener.onGroceryFetched(new ArrayList<>());
                        }
                    } catch (JSONException e) {
                        ToastUtils.showToast(context, "Try Again!");
                        listener.onGroceryFetched(new ArrayList<>()); // Return empty list
                    }
                }, error -> {
            if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                // Handle unauthorized error
            }
            Log.e(TAG, "Volley error: " + error.getMessage());
            ToastUtils.showToast(context, "Failed to retrieve grocery items");
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

        queue.add(searchRequest);
    }
}