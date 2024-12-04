package com.example.smartgrocerytracker.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ForgotPasswordActivity;
import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.loginApiServices;
import com.example.smartgrocerytracker.utils.LanguageUtil;
import com.example.smartgrocerytracker.utils.LoginUtils;
import com.example.smartgrocerytracker.utils.TokenValidator;

public class Login extends AppCompatActivity {

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressSpinner;
    private static final String SharedPrefName = "UserPref";

    private TextView welcometext;

    private TextView welcometext1;

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
        progressSpinner = findViewById(R.id.buttonProgressBar); // Progress Spinner
        // Shared Preferences for saving username
        sharedPreferences = getSharedPreferences(SharedPrefName, MODE_PRIVATE);


        welcometext = findViewById(R.id.welcomlogintext);
        welcometext1 = findViewById(R.id.second_login);

        Animation dropDownAnimation1 = AnimationUtils.loadAnimation(this, R.anim.drop_down);
        welcometext.startAnimation(dropDownAnimation1);
        Animation dropDownAnimation2 = AnimationUtils.loadAnimation(this, R.anim.drop_down);
        welcometext1.startAnimation(dropDownAnimation2);

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
                String username = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                boolean isValid = LoginUtils.validateEditText(emailEditText) & LoginUtils.validateEditText(passwordEditText);
                if (isValid) {
                    // Optionally, check network connectivity
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                    if (!isConnected) {
                        Toast.makeText(Login.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Show the Progress Spinner
                    progressSpinner.setVisibility(View.VISIBLE);
                    loginButton.setEnabled(false); // Disable the button to prevent multiple clicks

                    // Simulate a 2-second delay using Handler
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loginApiServices.loginUser(
                                    Login.this,
                                    username,
                                    password,
                                    queue,
                                    sharedPreferences,
                                    new loginApiServices.LoginCallback() { // Callback interface
                                        @Override
                                        public void onLoginSuccess() {
                                            // Hide the Progress Spinner and re-enable the button
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressSpinner.setVisibility(View.GONE);
                                                    loginButton.setEnabled(true);
                                                    // Navigate to MainActivity
                                                    Intent mainIntent = new Intent(Login.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    finish(); // Prevent returning to the login screen
                                                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onLoginFailure(String errorMessage) {
                                            // Hide the Progress Spinner and re-enable the button
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    progressSpinner.setVisibility(View.GONE);
                                                    loginButton.setEnabled(true);
                                                    // Show error message to the user
                                                    Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                            );
                        }
                    }, 2000);
                } else {
                    Toast.makeText(Login.this, "Enter username and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
