package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.services.loginApiServices;
import com.example.smartgrocerytracker.ui.Login;

public class LoginUtils {


    public static void handleUserLogin(EditText emailEditText, EditText passwordEditText, RequestQueue queue, SharedPreferences sharedPreferences, Context context){

        boolean isValid = validateEditText(emailEditText) & validateEditText(passwordEditText);
        if (isValid) {
            String username = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            loginApiServices.loginUser(context,username,password,queue,sharedPreferences);
        } else {
            Toast.makeText(context, "Enter username and password.", Toast.LENGTH_SHORT).show();
        }

    }


    private static boolean validateEditText(EditText editText) {
        String input = editText.getText().toString().trim();
        if (input.isEmpty()) {
            editText.setError(editText.getHint() + " is required");
        // editText.setBackgroundResource(R.drawable.edittext_error_background);
            return false;
        }
        editText.setError(null);
        editText.setBackgroundResource(android.R.drawable.edit_text);
        return true;
    }
}
