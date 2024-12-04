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
import com.example.smartgrocerytracker.ModelClass.BudgetModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class updateBudgetDetails {
    private static final String TAG = "updateBudget";

    // Interface for callback
    public interface UpdateBudgetListener {
        void onBudgetUpdated(BudgetModel updatedBudgetModel);
    }

    public static void putBudgetRequest(Context context, RequestQueue queue, String budgetId, Integer budgetAmount, String startDate, String endDate, UpdateBudgetListener listener) {
        final String BASE_URL = Config.UPDATE_BUDGET_URL + budgetId;
        String token = SecurePreferences.getAuthToken(context);

        JSONObject postData = new JSONObject();
        try {
            postData.put("budget_amount", budgetAmount);
            postData.put("spent_amount", 183); // Update this with actual logic if required
            postData.put("start_date", startDate);
            postData.put("end_date", endDate);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating request data!", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest fetchUserRequest = new JsonObjectRequest(Request.Method.PUT, BASE_URL, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                // Parse response into BudgetModel
                                JSONObject data = response.getJSONObject("data");
                                BudgetModel updatedBudgetModel = new BudgetModel(
                                        data.getString("budget_id"),
                                        data.getString("user_id"),
                                        data.getDouble("budget_amount"),
                                        data.getDouble("spent_amount"),
                                        data.getString("start_date"),
                                        data.getString("end_date")
                                );

                                // Notify listener of the updated budget
                                if (listener != null) {
                                    listener.onBudgetUpdated(updatedBudgetModel);
                                }

                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, response.getString("message"));
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(context, "Failed to parse response", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error: " + error.getMessage());
                Toast.makeText(context, "Failed to update budget", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(fetchUserRequest);
    }
}