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
import com.example.smartgrocerytracker.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class loginApiServices {

    public static void loginUser(Context context, String username, String password, RequestQueue queue,SharedPreferences sharedPreferences) {
        String url = "http://10.0.2.2:8000/login/";

        JSONObject jsonObject = new JSONObject();
        try {
            Log.i("asd",username + password);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest loginRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            String token = data.getString("token");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("auth_token", token);
                            editor.putString("username", username);
                            editor.apply();
                            Log.i("as", String.valueOf(data));
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                            Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.getMessage();
                if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                    errorMessage = "Invalid username or password.";
                }
                Toast.makeText(context, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("LoginError", "Error: " + errorMessage);
            }
        }) {
            // Optionally, send headers with your request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + token);
                Log.i("RequestHeaders", "Headers: " + headers.toString());
                return headers;
            }
        };

        queue.add(loginRequest);
    }

}
