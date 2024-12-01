package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NearbyStoreService {

    private static final String TAG = "NearbyStoreService";
    private static final String API_KEY = "YOUR_API_KEY_HERE"; // Replace with your API key
    private RequestQueue requestQueue;

    public NearbyStoreService(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public interface StoreFetchCallback {
        void onSuccess(List<String[]> stores);
        void onError(String errorMessage);
    }

    public void fetchNearbyStores(Location location, StoreFetchCallback callback) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + location.getLatitude() + "," + location.getLongitude() +
                "&radius=5000" +
                "&type=grocery_or_supermarket" +
                "&key=" + API_KEY;

        Log.d(TAG, "Requesting nearby stores with URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d(TAG, "API Response: " + response.toString());
                        JSONArray results = response.getJSONArray("results");
                        List<String[]> storeList = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            String name = place.getString("name");
                            String address = place.optString("vicinity", "Unknown Address");
                            boolean openNow = place.optJSONObject("opening_hours") != null &&
                                    place.getJSONObject("opening_hours").optBoolean("open_now", false);
                            String status = openNow ? "Open" : "Closed";

                            double distance = Math.random() * 5; // Mock distance for now
                            String distStr = String.format("%.1f mi", distance);

                            int estTime = (int) (distance * 5); // Mock: 5 min/mile
                            storeList.add(new String[]{name, address, status, distStr, estTime + " min"});
                        }
                        callback.onSuccess(storeList);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing places: " + e.getMessage());
                        callback.onError("Error parsing store data.");
                    }
                },
                error -> {
                    Log.e(TAG, "Request failed: " + error.getMessage());
                    callback.onError("Failed to fetch stores.");
                });
        requestQueue.add(request);
    }
}