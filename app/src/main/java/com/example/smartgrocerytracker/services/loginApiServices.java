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
import com.example.smartgrocerytracker.utils.LoginUtils;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class loginApiServices {
    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailure(String errorMessage);
    }
    /**
     * Initiates the user login process by making a POST request to the login API.
     *
     * @param context           The context from which this method is called.
     * @param username          The username or email entered by the user.
     * @param password          The password entered by the user.
     * @param queue             The Volley RequestQueue.
     * @param sharedPreferences The SharedPreferences for storing user data.
     * @param callback          The callback to handle login responses.
     */

    public static void loginUser(Context context, String username, String password, RequestQueue queue, SharedPreferences sharedPreferences, LoginCallback callback) {
        String url = Config.LOGIN_URL;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onLoginFailure("JSON Error: " + e.getMessage());
            return;
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            String token = data.getString("token");
                            JSONObject user_response = data.getJSONObject("user_response");
                            String user_id = user_response.getString("user_id");
                            String budget_id = user_response.getString("budget_id");

                            // Save token securely
                            SecurePreferences.saveAuthToken(context, token);

                            // Save user data in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", username);
                            editor.putString("user_id", user_id);
                            editor.putString("budget_id", budget_id);
                            editor.apply();

                            Log.i("LoginResponse:", String.valueOf(data));

                            // Invoke success callback
                            callback.onLoginSuccess();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onLoginFailure("Parsing Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Login failed. Please try again.";
                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                    errorMessage = "Invalid username or password.";
                } else if (error.getMessage() != null) {
                    errorMessage = error.getMessage();
                }
                Toast.makeText(context,  errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("LoginError", "Error: " + errorMessage);
                callback.onLoginFailure(errorMessage);
            }
        }) {
            // Optionally, send headers with your request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                // If you have an authorization token, include it here
                // headers.put("Authorization", "Bearer " + token);
                Log.i("RequestHeaders", "Headers: " + headers.toString());
                return headers;
            }
        };

        // Add the request to the RequestQueue
        queue.add(loginRequest);
    }

}