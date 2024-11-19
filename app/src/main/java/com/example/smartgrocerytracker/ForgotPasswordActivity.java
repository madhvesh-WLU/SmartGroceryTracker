package com.example.smartgrocerytracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.ui.Login;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText usernameEditText, newPasswordEditText, confirmPasswordEditText;
    private Button submitButton;
    private TextView confirmationMessage, loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        submitButton = findViewById(R.id.submitButton);
        confirmationMessage = findViewById(R.id.confirmationMessage);
        loginLink = findViewById(R.id.loginLink);

        // Handle the submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    if (isUsernameValid(usernameEditText.getText().toString().trim())) {
                        // Here you would update the password for the username in your database
                        confirmationMessage.setText("Your password has been changed successfully!");
                        confirmationMessage.setVisibility(View.VISIBLE);
                    } else {
                        usernameEditText.setError("Username does not exist");
                    }
                }
            }
        });

        // Handle the login link click
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validateInputs() {
        String username = usernameEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return false;
        }

        if (TextUtils.isEmpty(newPassword) || newPassword.length() < 6 || !isValidPassword(newPassword)) {
            newPasswordEditText.setError("Password must be at least 6 characters and contain at least one special character");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        String specialCharacters = "[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+";
        return password.matches(".*" + specialCharacters + ".*");
    }

    // Placeholder method for checking if the username exists
    private boolean isUsernameValid(String username) {
        // Replace this with actual logic to check the username in your database
        // For demonstration, let's assume all usernames are valid
        // In real scenarios, make a call to your backend or query your local database
        return true; // Change to false if the username does not exist
    }
}
