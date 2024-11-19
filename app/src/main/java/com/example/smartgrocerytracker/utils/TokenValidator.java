package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.ui.Login;

public class TokenValidator {

    public static boolean handleAuthentication(Context context) {
        String authToken = SecurePreferences.getAuthToken(context);

        if (isTokenValid(authToken, context)) {
            navigateToMainActivity(context);
            return true;
        } else {
            Log.i("TokenValidator", "Redirecting to login.");
//            Toast.makeText(context, "Login Again!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private static boolean isTokenValid(String authToken, Context context) {
        if (authToken != null && !authToken.isEmpty()) {
            return TokenValidator.validateToken(authToken, context);
        }
        return false;
    }

    private static void navigateToMainActivity(Context context) {
        Intent homeIntent = new Intent(context, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(homeIntent);
    }

    public static void redirectToLogin(Context context) {
        Intent loginIntent = new Intent(context, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(loginIntent);
    }

    public static boolean validateToken(String token, Context context) {
        try {
            JWT jwt = new JWT(token);
            if (jwt.isExpired(0)) {
                return false;
            }
            return true;

        } catch (Exception e) {
            Log.e("TokenValidator", "Invalid token: " + e.getMessage());
            redirectToLogin(context);
            return false;
        }
    }



}
