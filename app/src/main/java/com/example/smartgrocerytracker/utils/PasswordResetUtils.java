package com.example.smartgrocerytracker.utils;


import android.content.Context;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class PasswordResetUtils {

    public static void sendPasswordResetRequest(String email, RequestQueue queue, Context context) {
        String url = "localhost:8000/password-reset?email=" + email;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Password reset link sent to your email.", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error sending reset request. Try again.", Toast.LENGTH_LONG).show();
            }
        });

        queue.add(stringRequest);
    }
}
