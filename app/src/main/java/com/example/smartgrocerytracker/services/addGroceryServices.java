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
    public static void postGroceryDetailsByExpenseID(Context context, RequestQueue queue, JSONArray requestData, String expense_id) {
        String token = SecurePreferences.getAuthToken(context);
        String url = Config.ADD_GROCERY_ITEM_URL;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url+expense_id, requestData,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Asd",response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.getMessage();
                Toast.makeText(context, "Request failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("Add expense", "Error: " + errorMessage);
            }
        }) {
            // Send headers with your request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                Log.i("RequestHeaders", "Headers: " + headers.toString());
                return headers;
            }
        };

        queue.add(request);
    }
}

