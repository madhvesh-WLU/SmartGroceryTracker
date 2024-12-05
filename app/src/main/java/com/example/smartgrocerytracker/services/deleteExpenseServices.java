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
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class deleteExpenseServices {
    private static final String TAG = "DeleteGroceryItemsServices";

    public static void deleteExpenseItems(Context context, RequestQueue queue, String expenseId, Runnable onSuccessCallback) {
        String url = Config.EXPENSES_URL +  expenseId;
        JsonObjectRequest deleteRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                response -> {
                    Log.d("DELETE_EXPENSE", "Response: " + response.toString());
                    if (onSuccessCallback != null) {
                        onSuccessCallback.run();
                    }
                },
                error -> {
                    Toast.makeText(context, "Failed to delete expense!", Toast.LENGTH_SHORT).show();
                    Log.e("DELETE_EXPENSE", "Volley error: " + error.toString());
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorBody = new String(error.networkResponse.data);
                        Log.e("DELETE_EXPENSE", "Error response body: " + errorBody);
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SecurePreferences.getAuthToken(context));
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(deleteRequest);
    }
}
