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

public class deleteGroceryServices {
    private static final String TAG = "DeleteGroceryItemsServices";

    public static void deleteGroceryItems(Context context, RequestQueue queue, JSONObject requestData, String expense_id) {
        String token = SecurePreferences.getAuthToken(context);
        String url = Config.DELETE_GROCERY_ITEM_URL + expense_id;
        // Create the hardcoded JSON object
        JSONObject newCHECK = new JSONObject();
        try {
            JSONArray groceryIds = new JSONArray();
            groceryIds.put("a02f5886-5490-4bda-8d87-f9e555e0a43d");
            groceryIds.put("aeb34ce0-4468-4961-8608-7c3bf7ebb380");
            newCHECK.put("grocery_ids", groceryIds);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, newCHECK,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        try {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body = new String(error.networkResponse.data);
                        Log.e(TAG, "Error response body: " + body);
                        String errorMessage = (error.getMessage() != null) ? error.getMessage() : "Unknown error";
                        Toast.makeText(context, "Request failed: " + errorMessage, Toast.LENGTH_SHORT).show();
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
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        queue.add(request);
    }

}
