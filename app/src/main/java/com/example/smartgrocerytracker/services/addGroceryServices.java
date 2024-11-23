package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class addGroceryServices {
    private static final String TAG = "AddGroceryItemsServices";

    public static void postGroceryDetailsByExpenseID(Context context, RequestQueue queue, JSONArray requestData, String expense_id,final Runnable onSuccess) {
        String token = SecurePreferences.getAuthToken(context);
        String url = Config.ADD_GROCERY_ITEM_URL + expense_id;
        JSONObject payload = new JSONObject();
        try {
            payload.put("items", requestData);
            Log.i(TAG,payload.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Error creating payload: " + e.getMessage());
            return;
        }
    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i(TAG, response.toString());
                    try {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        if (onSuccess != null) {
                            onSuccess.run();  // Call the onSuccess method (navigate back)
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = (error.getMessage() != null) ? error.getMessage() : "Unknown error";
                    Toast.makeText(context, "Request failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + errorMessage);
                }
            }) {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Authorization", "Bearer " + token);
            Log.i(TAG, "Request Headers: " + headers.toString());
            return headers;
        }
    };
        queue.add(request);
}
}
