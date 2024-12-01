package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanningAddExpenseServices {

    public interface ApiCallback {
        void onSuccess(); // Called on successful API response
        void onError(String errorMessage); // Called on API error
    }

    public static void makeStoreItemsApiCall(Context context, JSONObject requestData, ApiCallback callback) {
        String token = SecurePreferences.getAuthToken(context);
        String storeItemsUrl = Config.ADD_EXPENSES_URL;
        // Make the API call
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                storeItemsUrl,
                requestData,
                response -> {
                    callback.onSuccess();
                },
                error -> {
                    Log.e("StoreItemsAPI", "Error: " + error.toString());
                    String errorMessage = "Failed to store items. Please try again.";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    callback.onError(errorMessage);
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); // Add Bearer token
                headers.put("Content-Type", "application/json"); // Set Content-Type
                return headers;
            }
        };

        // Set retry policy
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // Add request to the Volley queue
        Volley.newRequestQueue(context).add(request);

    }
}
