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
import android.widget.ImageButton;
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
import com.example.smartgrocerytracker.utils.SnackbarHelper;
import com.example.smartgrocerytracker.utils.TokenValidator;

public class Login extends AppCompatActivity {

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressSpinner;
    private static final String SharedPrefName = "UserPref";

    private TextView welcometext;

    private ImageButton loginIcon;

    private TextView welcometext1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        if (!TokenValidator.handleAuthentication(this)) {
            setContentView(R.layout.activity_login);
        }
        setContentView(R.layout.activity_login);

        loginIcon = findViewById(R.id.login_icon);
        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckbox);
        loginButton = findViewById(R.id.loginButton); // Login button
        TextView forgotPasswordText = findViewById(R.id.forgot_password_text);
        TextView registerLink = findViewById(R.id.register_link);
        progressSpinner = findViewById(R.id.buttonProgressBar);
        sharedPreferences = getSharedPreferences(SharedPrefName, MODE_PRIVATE);


        welcometext = findViewById(R.id.welcomlogintext);
        welcometext1 = findViewById(R.id.second_login);


        Animation dropDownAnimation1 = AnimationUtils.loadAnimation(this, R.anim.drop_down);
        welcometext.startAnimation(dropDownAnimation1);
        Animation dropDownAnimation2 = AnimationUtils.loadAnimation(this, R.anim.drop_down);
        welcometext1.startAnimation(dropDownAnimation2);
        Animation dropDownAnimation3 = AnimationUtils.loadAnimation(this, R.anim.drop_down);
        loginIcon.startAnimation(dropDownAnimation3);

        // Request Queue from Volley
        RequestQueue queue = Volley.newRequestQueue(Login.this);

        Intent intent = getIntent();
        String passedUsername = intent.getStringExtra("username");
        if (passedUsername != null && !passedUsername.isEmpty()) {
            emailEditText.setText(passedUsername);
        } else {
            String storedUsername = sharedPreferences.getString("username", null);
            if (storedUsername != null) {
                emailEditText.setText(storedUsername);
            }
        }

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                localLoginCall(username, password);
            }

         public void localLoginCall(String username, String password){
        boolean isValid = LoginUtils.validateEditText(emailEditText) & LoginUtils.validateEditText(passwordEditText);
        if (isValid) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            if (!isConnected) {
                Toast.makeText(Login.this, "No internet connection.", Toast.LENGTH_SHORT).show();
                return;
            }

            progressSpinner.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loginApiServices.loginUser(
                            Login.this,
                            username,
                            password,
                            queue,
                            sharedPreferences,
                            new loginApiServices.LoginCallback() {
                                @Override
                                public void onLoginSuccess() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressSpinner.setVisibility(View.GONE);
                                            loginButton.setEnabled(true);

                                            new Handler().postDelayed(()->{
                                                Intent mainIntent = new Intent(Login.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            },500);
                                            SnackbarHelper.showSnackbar(findViewById(android.R.id.content), "Login successful");
                                        }
                                    });
                                }

                                @Override
                                public void onLoginFailure(String errorMessage) {
                                    // Hide the Progress Spinner on failure too..
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressSpinner.setVisibility(View.GONE);
                                            loginButton.setEnabled(true);
                                            Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                    );
                }
            }, 1200);
        } else {
            SnackbarHelper.showSnackbar(findViewById(android.R.id.content), "Enter username and password");
        }
    }
        });
    }
}
