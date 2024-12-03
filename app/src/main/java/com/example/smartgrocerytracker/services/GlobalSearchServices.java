package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GlobalSearchServices {

    private final RequestQueue requestQueue;
    Context context;

    public GlobalSearchServices(Context context) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.context = context;
    }

    public void fetchBillNameData(String query, String year, String month, Response.Listener<JSONObject> callback, Response.ErrorListener errorCallback) {
        String url = Config.GLOBAL_SEARCH_BILL_NAME + "?query=" + query;

        Log.i("asd", url);

        // Add global filters for year and month if selected
        if (!year.isEmpty()) url += "&year=" + year;
        if (!month.isEmpty()) url += "&month=" + month;

        makeApiCall(url, callback, errorCallback);
    }

    public void fetchGroceryNameData(String query, String year, String month, Response.Listener<JSONObject> callback, Response.ErrorListener errorCallback) {
        String url = Config.GLOBAL_SEARCH_GROCERY_NAME + "?query=" + query;

        // Add global filters for year and month if selected
        if (!year.isEmpty()) url += "&year=" + year;
        if (!month.isEmpty()) url += "&month=" + month;

        makeApiCall(url, callback, errorCallback);
    }

    public void fetchCategoryData(String category, String year, String month, Response.Listener<JSONObject> callback, Response.ErrorListener errorCallback) {
        String url = Config.GLOBAL_SEARCH_CATEGORY + "?category=" + category;

        // Add global filters for year and month if selected
        if (!year.isEmpty()) url += "&year=" + year;
        if (!month.isEmpty()) url += "&month=" + month;

        makeApiCall(url, callback, errorCallback);
    }

    private void makeApiCall(String url, Response.Listener<JSONObject> callback, Response.ErrorListener errorCallback) {
        String token = SecurePreferences.getAuthToken(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // Log the response
                    Log.d("API Response", "URL: " + url + "\nResponse: " + response.toString());

                    // Pass the response to the callback
                    callback.onResponse(response);
                },
                error -> {
                    // Log the error
                    Log.e("API Error", "URL: " + url + "\nError: " + error.toString());

                    // Pass the error to the errorCallback
                    errorCallback.onErrorResponse(error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Log the request being sent
        Log.d("API Request", "URL: " + url);
        requestQueue.add(jsonObjectRequest);
    }
}