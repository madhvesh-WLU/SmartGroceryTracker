package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fetchBudgetDetails {

    // Interface to notify when the data is fetched and updated
    public interface BudgetDetailsUpdateListener {
        void onBudgetDetailsUpdated(String amount, String startDate, String endDate);
    }


    public static void getBudgetService(Context context, RequestQueue queue, String budget_id, BudgetDetailsUpdateListener listener) {
        String token = SecurePreferences.getAuthToken(context);
        String url = Config.GET_BUDGET_URL + budget_id;  // Assuming your URL includes the budget_id

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the response JSON
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");
                            int statusCode = jsonResponse.getInt("statusCode");
                            String message = jsonResponse.getString("message");

                            if (success) {
                                // Get the budget data
                                JSONObject data = jsonResponse.getJSONObject("data");
                                String id = data.getString("id");
                                String userId = data.getString("user_id");
                                String amount = data.getString("amount");
                                String spentAmount = data.getString("spent_amount");
                                String startDate = data.getString("start_date");
                                String endDate = data.getString("end_date");

                                // Store the budget details in SharedPreferences
                                SharedPreferences sharedPreferences = context.getSharedPreferences("ActiveBudget", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("id", id);
                                editor.putString("user_id", userId);
                                editor.putString("amount", amount);
                                editor.putString("spent_amount", spentAmount);
                                editor.putString("start_date", startDate);
                                editor.putString("end_date", endDate);
                                editor.apply();
// Notify the listener that the data is updated
                                if (listener != null) {
                                    listener.onBudgetDetailsUpdated(amount, startDate, endDate);
                                }
                                Toast.makeText(context, "Budget details saved in SharedPreferences", Toast.LENGTH_SHORT).show();


                            } else {
                                // Handle case where success is false
                                Toast.makeText(context, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = error.getMessage();
                        Toast.makeText(context, "Request failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("FetchBudgetDetails", "Error: " + errorMessage);
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
