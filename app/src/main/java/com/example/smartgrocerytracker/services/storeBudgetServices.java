package com.example.smartgrocerytracker.services;

import android.app.Activity;
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
import com.example.smartgrocerytracker.ui.Login;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class storeBudgetServices {

    private static final String BASE_URL = Config.BUDGET_STORE_URL;
    private static final String TAG = "store";

    public static void sendBudgetRequest(Context context, RequestQueue queue, Integer budgetAmount, String startDate, String endDate) {
        String token = SecurePreferences.getAuthToken(context);
        Log.i("check",startDate);
        Log.i("check", endDate);

        JSONObject postData = new JSONObject();
        try {
            postData.put("budget_amount",budgetAmount);
            postData.put("spent_amount",0);
            postData.put("start_date", startDate);
            postData.put("end_date", endDate);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating request data!", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest fetchUserRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL,postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {

                                fetchUserServices.fetchUserDetails(context,queue);
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();


                            } else {
                                Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, response.getString("message"));
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            Toast.makeText(context, "Failed to parse user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

        queue.add(fetchUserRequest);


    };
}








//                            int statusCode = response.getInt("statusCode");
//                            String message = response.getString("message");
//                            if (response.getBoolean("success") && statusCode == 201) {
//                                JSONObject dataArray = response.isNull("data") ? null : response.getJSONObject("data");
//                                Toast.makeText(context, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(context, Login.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                context.startActivity(intent);
//                                if (context instanceof Activity) {
//                                    ((Activity) context).finish();
//                                }
//                            } else {
//                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                            }
