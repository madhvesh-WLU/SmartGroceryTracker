package com.example.smartgrocerytracker.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.R;

import com.example.smartgrocerytracker.services.loginApiServices;

public class Login extends AppCompatActivity {
    private Button startloginButton;
    EditText emailEditText, passwordEditText;
    SharedPreferences sharedPreferences;
    static final String SharedPrefName = "UserPref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(SharedPrefName, MODE_PRIVATE);

        RequestQueue queue = Volley.newRequestQueue(Login.this);


        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        TextView registerLink = findViewById(R.id.register_link);
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        String storedUsername = sharedPreferences.getString("username", null);

        if (storedUsername != null) {
            emailEditText.setText(storedUsername);
        } else {
            emailEditText.setText("");
        }

        startloginButton = findViewById(R.id.login_button);
        startloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startMainActivity();

                boolean isValid = validateEditText(emailEditText) & validateEditText(passwordEditText);  // Both fields must be valid

                if (isValid) {
                    String username = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    loginApiServices.loginUser(Login.this,username,password,queue,sharedPreferences);
                } else {
                    Toast.makeText(Login.this, "Please enter both username and password.", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
    void startMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }

    private boolean validateEditText(EditText editText) {
        String input = editText.getText().toString().trim();
        if (input.isEmpty()) {
            editText.setError(editText.getHint() + " is required");
//            editText.setBackgroundResource(R.drawable.edittext_error_background);
            return false;
        }
        editText.setError(null);  // Clear any previous error
        editText.setBackgroundResource(android.R.drawable.edit_text);  // Reset to default background
        return true;
    }
}