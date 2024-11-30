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
import com.example.smartgrocerytracker.ui.profile.UserProfile;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class loginApiServices {

    public static void loginUser(Context context, String username, String password, RequestQueue queue,SharedPreferences sharedPreferences) {
        String url = Config.LOGIN_URL;
        JSONObject jsonObject = new JSONObject();
        try {
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
                            JSONObject user_response = data.getJSONObject("user_response");
                            String user_id = user_response.getString("user_id");
                            String budget_id = user_response.getString("budget_id");

                            SecurePreferences.saveAuthToken(context, token);

//                            SharedPreferences.Editor editor = sharedPreferences.edit();
////
//                            editor.putString("username", username);
//                            editor.putString("email", emailStr);
//                            editor.putString("user_id", user_id);
//                            editor.putString("budget_id", budget_id);
//                            editor.apply();
                            Log.i("Res:", String.valueOf(data));

//                            String usernamename = user_response.getString("username");
//                            String email = user_response.getString("email");
//                            UserProfile.getInstance().setUserData(usernamename,email,user_id,budget_id);


                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                             Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_NO_ANIMATION |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                Toast.makeText(context,  errorMessage, Toast.LENGTH_SHORT).show();
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
