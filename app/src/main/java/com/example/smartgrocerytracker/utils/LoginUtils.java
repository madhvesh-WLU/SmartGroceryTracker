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
}
