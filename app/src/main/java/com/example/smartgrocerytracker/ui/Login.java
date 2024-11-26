package com.example.smartgrocerytracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ForgotPasswordActivity;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.utils.LoginUtils;
import com.example.smartgrocerytracker.utils.TokenValidator;

public class Login extends AppCompatActivity {

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;

    private static final String SharedPrefName = "UserPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!TokenValidator.handleAuthentication(this)) {
            setContentView(R.layout.activity_login);
        }
        setContentView(R.layout.activity_login);

        // Initialize Views
        emailEditText = findViewById(R.id.username); // Email/Username field
        passwordEditText = findViewById(R.id.password); // Password field
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckbox); // "Remember me" checkbox
        loginButton = findViewById(R.id.loginButton); // Login button
        TextView forgotPasswordText = findViewById(R.id.forgot_password_text); // Forgot Password
        TextView registerLink = findViewById(R.id.register_link); // Register instead link

        // Shared Preferences for saving username
        sharedPreferences = getSharedPreferences(SharedPrefName, MODE_PRIVATE);

        // Request Queue for handling login
        RequestQueue queue = Volley.newRequestQueue(Login.this);

        // Check if username is passed from the SignUp activity
        Intent intent = getIntent();
        String passedUsername = intent.getStringExtra("username");
        if (passedUsername != null && !passedUsername.isEmpty()) {
            emailEditText.setText(passedUsername); // Pre-fill username field
        } else {
            // Pre-fill email field if previously saved
            String storedUsername = sharedPreferences.getString("username", null);
            if (storedUsername != null) {
                emailEditText.setText(storedUsername);
            }
        }

        // Handle Forgot Password click
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        // Handle Register link click
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        // Handle Login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle user login with LoginUtils
                LoginUtils.handleUserLogin(
                        emailEditText,
                        passwordEditText,
                        queue,
                        sharedPreferences,
                        Login.this
                );
            }
        });
    }
}
