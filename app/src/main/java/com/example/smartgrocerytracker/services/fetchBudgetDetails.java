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
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.BudgetModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fetchBudgetDetails {

    // Interface to notify when the data is fetched and updated
    public interface BudgetDetailsUpdateListener {
        void onBudgetDetailsUpdated(BudgetModel budgetModel);
    }
    public static void getBudgetService(Context context, String Budget_id,BudgetDetailsUpdateListener listener) {

        String token = SecurePreferences.getAuthToken(context);
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Config.GET_BUDGET_URL + Budget_id;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parse the response JSON
                            JSONObject jsonResponse = new JSONObject(response);

                            boolean success = jsonResponse.getBoolean("success");
                            String message = jsonResponse.getString("message");

                            if (success) {
                                // Get the budget data
                                JSONObject data = jsonResponse.getJSONObject("data");

                                BudgetModel budgetModel = new BudgetModel(
                                        data.getString("budget_id"),
                                        data.getString("user_id"),
                                        data.getDouble("budget_amount"),
                                        data.getDouble("spent_amount"),
                                        data.getString("start_date"),
                                        data.getString("end_date")
                                );

                                // Store the budget details in SharedPreferences
                                SharedPreferences sharedPreferences = context.getSharedPreferences("ActiveBudget", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("id", budgetModel.getBudgetId());
                                editor.putString("user_id", budgetModel.getUserId());
                                editor.putString("amount", String.valueOf(budgetModel.getBudgetAmount()));
                                editor.putString("spent_amount", String.valueOf(budgetModel.getSpentAmount()));
                                editor.putString("start_date", budgetModel.getStartDate());
                                editor.putString("end_date", budgetModel.getEndDate());
                                editor.apply();

                                // Notify the listener that the data is updated
                                if (listener != null) {
                                    listener.onBudgetDetailsUpdated(budgetModel);
                                }
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
                Log.i("RequestHeaders", "Headers: " + headers);
                return headers;
            }
        };

        queue.add(request);
    }
}