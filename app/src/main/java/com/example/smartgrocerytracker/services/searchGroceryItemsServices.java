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
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searchGroceryItemsServices {
    private static final String TAG = "SearchGroceryItemsServices";

    // Define the listener interface
    public interface GroceryItemsFetchListener {
        void onGroceryFetched(List<GroceryItemModel> groceryItems);
    }

    /**
     * Fetches grocery items based on the search query and expense ID.
     *
     * @param context    The application context.
     * @param queue      The Volley RequestQueue.
     * @param query      The search query string.
     * @param expenseId  The ID of the expense to filter grocery items.
     * @param listener   The listener to handle the fetched data.
     */
    public static void searchGroceryItems(Context context, RequestQueue queue, String query, String expenseId, GroceryItemsFetchListener listener) {
        String token = SecurePreferences.getAuthToken(context);
        String url = Config.SEARCH_GROCERY_ITEM_URL + expenseId + "?query=" + query;
        Log.d(TAG, "Search URL: " + url);

        JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
                                            item.has("user_id") ? item.getString("user_id") : null,
                                            item.getInt("quantity"),
                                            item.getString("category"),
                                            item.getDouble("price"),
                                            item.getBoolean("purchased")
                                    );
                                    groceryItems.add(groceryItem);
                                }

                                listener.onGroceryFetched(groceryItems);
                                Toast.makeText(context, "Expenses retrieved: " + itemsArray.length(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, itemsArray.toString());
                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Response message: " + response.getString("message"));
                                listener.onGroceryFetched(new ArrayList<>()); // Return empty list
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(context, "Failed to parse user data", Toast.LENGTH_SHORT).show();
                            listener.onGroceryFetched(new ArrayList<>()); // Return empty list
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                    Toast.makeText(context, "Unauthorized, please login", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "Volley error: " + error.getMessage());
                Toast.makeText(context, "Failed to retrieve grocery items", Toast.LENGTH_SHORT).show();
                listener.onGroceryFetched(new ArrayList<>()); // Return empty list
            }
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