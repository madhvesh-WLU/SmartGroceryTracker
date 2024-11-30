package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.content.Intent;
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
import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class addExpenseServices {
    public interface ExpenseResponseListener {
        void onExpenseAdded(String expenseId); // Called when the request succeeds
        void onError(String errorMessage);    // Called when the request fails
    }
        public static void postExpenseDetails(Context context, RequestQueue queue, JSONObject requestData, ExpenseResponseListener listener) {
            String token = SecurePreferences.getAuthToken(context);
            String url = Config.ADD_EXPENSES_URL;

            Log.i("asd2222",requestData.toString());
            JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getBoolean("success")){
                                    String statusCode = response.getString("statusCode");
                                    String message = response.getString("message");
                                    Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
                                    JSONObject data = response.getJSONObject("data");
                                    String expense_id = data.getString("expense_id");

                                    // Pass the expense_id to the listener
                                    listener.onExpenseAdded(expense_id);

                                }
                                else{
                                    String message = response.getString("message");
                                    listener.onError(message); // Pass error message to the listener
                                    Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                listener.onError("Parsing error: " + e.getMessage());
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = error.getMessage();
                    Toast.makeText(context, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    Log.e("Add expense", "Error: " + errorMessage);
                }
            }) {
                // Optionally, send headers with your request
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + token);
                    Log.i("RequestHeaders", "Headers: " + headers.toString());
                    return headers;
                }
            };

            queue.add(loginRequest);
        }

}

