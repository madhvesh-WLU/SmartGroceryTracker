package com.example.smartgrocerytracker.services;


import static android.content.Context.MODE_PRIVATE;

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
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searchExpensesServices {
    private static final String TAG = "SearchQueryServices";

    public static void fetchExpensesActiveBudget(Context context, RequestQueue queue, String query,fetchExpenseActiveBudgetServices.ExpenseFetchListener listener) {

        String token = SecurePreferences.getAuthToken(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPref", MODE_PRIVATE);
        String active_budget = sharedPreferences.getString("budget_id", null);
        String url = Config.SEARCH_EXPENSES_URL + active_budget + "?query=" + query;
        JsonObjectRequest searchExpenseUsingActiveBudget = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray data = response.getJSONArray("data");
                                Log.i("List nerw",response.toString());
                                List<ExpenseModel> expenses = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {

                                    JSONObject expenseObject = data.getJSONObject(i);
                                    String expenseId = expenseObject.getString("expense_id");
                                    String billName = expenseObject.optString("bill_name", null);
                                    double billAmount = expenseObject.getDouble("bill_amount");
                                    double totalQuantity = expenseObject.getDouble("total_quantity");
                                    String dateOfPurchase = expenseObject.getString("date_of_purchase");
                                    String description = expenseObject.getString("description");
                                    String budgetId = expenseObject.optString("budget_id", null);
                                    String storeId = expenseObject.optString("store_id", null);
                                    String userId = expenseObject.getString("user_id");
                                    String createdAt = expenseObject.getString("created_at");
                                    Log.i("List nerw",expenseObject.toString());

                                    ExpenseModel expense = new ExpenseModel(expenseId, billName, billAmount, totalQuantity,dateOfPurchase, description, budgetId, storeId, userId, createdAt,null);
                                    expenses.add(expense);
                                }

                                listener.onExpensesFetched(expenses);


                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Response message: " + response.getString("message"));

                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(context, "Failed to parse user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error.networkResponse.statusCode == 401)
                {
                    Toast.makeText(context, "Unauthorized please login", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "Volley error: " + error.getMessage());
                Toast.makeText(context, "Failed to retrieve user details", Toast.LENGTH_SHORT).show();
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

        queue.add(searchExpenseUsingActiveBudget);
    }
}