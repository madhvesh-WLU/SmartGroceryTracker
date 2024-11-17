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
import com.example.smartgrocerytracker.utils.TokenValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fetchUserServices {
    private static final String TAG = "fetchUserServices";
    private static final String USER_PREFS = "UserPref";
    private static final String EMAIL_KEY = "email";
    private static final String USERNAME_KEY = "username";

    public static void fetchUserDetails(Context context, RequestQueue queue) {
        String token = SecurePreferences.getAuthToken(context);
        String url = Config.USER_FETCH_URL;
        JSONObject jsonObject = new JSONObject();
        JsonObjectRequest fetchUserRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject data = response.getJSONObject("data");
                                String username =  data.getString("username");
                                String email = data.getString("email");
                                String user_id = data.getString("user_id");
                                String budget_id = data.optString("budget_id", null);
//
//                              UserProfile.getInstance().setUserData(username,email,user_id);
                                Log.i("Res:", String.valueOf(data));

                                SharedPreferences sharedPreferences = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(USERNAME_KEY,username);
                                editor.putString(EMAIL_KEY, email);
                                editor.putString("user_id", user_id);
                                editor.putString("budget_id", budget_id);
                                editor.apply();

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
    }

}
