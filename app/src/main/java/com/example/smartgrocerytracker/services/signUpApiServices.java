package com.example.smartgrocerytracker.services;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartgrocerytracker.ui.Login;

import org.json.JSONException;
import org.json.JSONObject;

public class signUpApiServices {

    private static final String BASE_URL = "http://10.0.2.2:8000/users/";

    public static void sendPostRequest(Context context, RequestQueue queue, String email, String password, String username) {
        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);
            postData.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating request data!", Toast.LENGTH_SHORT).show();
            return;
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", response.toString());
                        try {
                            int statusCode = response.getInt("statusCode");
                            String message = response.getString("message");
                            if (response.getBoolean("success") && statusCode == 201) {
                                JSONObject dataArray = response.isNull("data") ? null : response.getJSONObject("data");
                                Toast.makeText(context, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                context.startActivity(intent);
                                if (context instanceof Activity) {
                                    ((Activity) context).finish();
                                }
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error parsing response!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    Log.d("Error", "Resource not found");
                }
                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(jsonRequest);
    }

}
