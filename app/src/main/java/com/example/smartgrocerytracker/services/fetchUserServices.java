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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.BudgetModel;
import com.example.smartgrocerytracker.ModelClass.SharedBudgetViewModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fetchUserServices {
    private static final String TAG = "fetchUserServices";

    public static void fetchUserDetails(Context context, RequestQueue queue, SharedBudgetViewModel sharedBudgetViewModel) {
        String token = SecurePreferences.getAuthToken(context);
        String url = Config.USER_FETCH_URL;

        JsonObjectRequest fetchUserRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            String username = data.optString("username",null);
                            String user_id = data.optString("user_id",null);
                            String email = data.optString("email",null);
                            String budgetId = data.optString("budget_id", null);

                            if (budgetId == null || "null".equals(budgetId)) {
                                SharedPreferences sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username",username);
                                editor.putString("email", email);
                                editor.putString("user_id", user_id);
                                editor.putString("budget_id", null);
                                editor.apply();

                                SharedPreferences sharedPreferences_active_budget = context.getSharedPreferences("ActiveBudget", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = sharedPreferences_active_budget.edit();
                                editor2.putString("budget_id",null);
                                editor2.apply();


                                sharedBudgetViewModel.setBudgetModel(null); // No active budget
                                return;
                            }

                            Log.e(TAG, String.valueOf(response.getJSONObject("data")));

                            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username",username);
                            editor.putString("email", email);
                            editor.putString("user_id", user_id);
                            editor.putString("budget_id", budgetId);
                            editor.apply();

                            SharedPreferences sharedPreferences_active_budget = context.getSharedPreferences("ActiveBudget", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sharedPreferences_active_budget.edit();
                            editor2.putString("budget_id",budgetId);
                            editor2.apply();
                            // Fetch the budget details using the budget ID
                            fetchBudgetDetails.getBudgetService(context, budgetId,budgetModel -> {
                                sharedBudgetViewModel.setBudgetModel(budgetModel); // Update ViewModel with budget
                            });
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, response.getString("message"));
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        Toast.makeText(context, "Failed to parse user data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Volley error: " + error.getMessage());
                    Toast.makeText(context, "Failed to retrieve user details", Toast.LENGTH_SHORT).show();
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