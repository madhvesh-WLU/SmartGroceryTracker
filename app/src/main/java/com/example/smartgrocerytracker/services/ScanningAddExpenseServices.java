package com.example.smartgrocerytracker.services;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.BudgetModel;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.ModelClass.SharedBudgetViewModel;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanningAddExpenseServices {

    public interface ApiCallback {
        void onSuccess(); // Called on successful API response
        void onError(String errorMessage); // Called on API error
    }

    public static void makeStoreItemsApiCall(Context context, JSONObject requestData, ApiCallback callback) {
        String token = SecurePreferences.getAuthToken(context);
        String storeItemsUrl = Config.ADD_EXPENSES_URL;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                storeItemsUrl,
                requestData,
                response -> {
                    try {
                        // Log response for debugging
                        Log.i("StoreItemsAPI", "Response: " + response.toString());

                        // Extract budget details from the response
                        JSONObject data = response.getJSONObject("data");
                        JSONObject budgetDetails = data.getJSONObject("budget_details");

                        // Update SharedBudgetViewModel
                        SharedBudgetViewModel sharedBudgetViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(SharedBudgetViewModel.class);
                        BudgetModel updatedBudget = new BudgetModel(
                                budgetDetails.getString("budget_id"),
                                data.getString("user_id"), // Assuming user_id is present in data
                                budgetDetails.getDouble("budget_amount"),
                                budgetDetails.getDouble("spent_amount"),
                                budgetDetails.getString("start_date"),
                                budgetDetails.getString("end_date")
                        );
                        sharedBudgetViewModel.setBudgetModel(updatedBudget);

                        // Notify callback of success
                        callback.onSuccess();
                    } catch (JSONException e) {
                        Log.e("StoreItemsAPI", "Error parsing response: " + e.getMessage());
                        callback.onError("Failed to parse server response.");
                    }
                },
                error -> {
                    Log.e("StoreItemsAPI", "Error: " + error.toString());
                    String errorMessage = "Failed to store items. Please try again.";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    callback.onError(errorMessage);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(context).add(request);
    }

}
