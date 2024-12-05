package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.services.loginApiServices;
import com.example.smartgrocerytracker.ui.Login;

public class LoginUtils {



    public static boolean validateEditText(EditText editText) {
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


    // Form Validation
    public static boolean validateForm(EditText usernameEditText, EditText emailEditText, EditText passwordEditText, EditText rePasswordEditText) {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String rePassword = rePasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return false;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid email is required");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return false;
        }
        if (TextUtils.isEmpty(rePassword) || !rePassword.equals(password)) {
            rePasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }
}
