package com.example.smartgrocerytracker.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.signUpApiServices;

public class SignUp extends AppCompatActivity {
    private EditText usernameEditText, emailEditText, passwordEditText, rePasswordEditText;
    private Button signupButton;

    private TextView welcomesignup;

    private TextView welcomesignup1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize fields
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        rePasswordEditText = findViewById(R.id.confirm_password);
        signupButton = findViewById(R.id.signupButton);
        ImageView playButtonImage = findViewById(R.id.imgplaybtn);
        welcomesignup = findViewById(R.id.welcomeSignUpText);
        welcomesignup1 = findViewById(R.id.second_signup);
        Animation dropDownAnimation = AnimationUtils.loadAnimation(this, R.anim.drop_down);
        welcomesignup.startAnimation(dropDownAnimation);
        Animation dropDownAnimation1 = AnimationUtils.loadAnimation(this, R.anim.drop_down);
        welcomesignup1.startAnimation(dropDownAnimation1);
        // Create the rotation animation
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(playButtonImage, "rotation", 0f, 360f);
        rotationAnimator.setDuration(1500); // 1 second duration
        rotationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // Start the animation
        rotationAnimator.start();

        TextView loginLink = findViewById(R.id.login_link);

        // Set click listener for the login link
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to LoginActivity
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish(); // Optional: Close the current activity
            }
        });

        // Set up RequestQueue
        RequestQueue queue = Volley.newRequestQueue(SignUp.this);

        signupButton.setOnClickListener(v -> {
            if (validateInputs()) {
                String username = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Call sign-up API service
                signUpApiServices.sendPostRequest(SignUp.this, queue, email, password, username);

            }
        });
    }

    private boolean validateInputs() {
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

        if (TextUtils.isEmpty(password) || password.length() < 6 || !isValidPassword(password)) {
            passwordEditText.setError("Password must be at least 6 characters and contain at least one special character");
            return false;
        }

        if (!password.equals(rePassword)) {
            rePasswordEditText.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        String specialCharacters = "[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]+";
        return password.matches(".*" + specialCharacters + ".*");
    }
}
