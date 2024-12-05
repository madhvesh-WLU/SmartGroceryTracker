package com.example.smartgrocerytracker.ui;

import static com.example.smartgrocerytracker.utils.LoginUtils.validateForm;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.signUpApiServices;
import com.example.smartgrocerytracker.utils.LanguageUtil;
import com.example.smartgrocerytracker.utils.SnackbarHelper;

public class SignUp extends AppCompatActivity {
    private EditText usernameEditText, emailEditText, passwordEditText, rePasswordEditText;
    private Button signupButton;

    private ProgressBar progressBar;
    private TextView welcomesignup;
    RequestQueue queue;
    private TextView welcomesignup1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressBar= findViewById(R.id.progress_Bar);
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

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(playButtonImage, "rotation", 0f, 360f);
        rotationAnimator.setDuration(1500);
        rotationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // Start the animation
        rotationAnimator.start();

        TextView loginLink = findViewById(R.id.login_link);

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        queue = Volley.newRequestQueue(SignUp.this);

        progressBar.setVisibility(View.GONE);
        signupButton.setEnabled(true);


        signupButton.setOnClickListener(v -> {
            localSignupCall();
        });


    }
    private void localSignupCall(){
        if (validateForm(usernameEditText,emailEditText,passwordEditText,rePasswordEditText)) {
            progressBar.setVisibility(View.VISIBLE);
            signupButton.setEnabled(false);
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            new Handler().postDelayed(() -> {

                signUpApiServices.sendPostRequest(SignUp.this, queue, email, password, username);
                SnackbarHelper.showSnackbar(findViewById(android.R.id.content), "SignUp successful");

                progressBar.setVisibility(View.GONE);
                signupButton.setEnabled(true);
            }, 1200);
        }
    };

}
