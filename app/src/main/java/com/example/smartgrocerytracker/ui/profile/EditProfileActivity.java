package com.example.smartgrocerytracker.ui.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgrocerytracker.databinding.ActivityEditProfileBinding;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private static final String SharedPrefName = "UserPref";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(SharedPrefName, MODE_PRIVATE);

        // Populate fields with existing data
        String currentUsername = sharedPreferences.getString("username", "");
        String currentEmail = sharedPreferences.getString("email", "");

        binding.editUsername.setText(currentUsername);
        binding.editEmail.setText(currentEmail);

        // Handle Update Button
        binding.buttonUpdate.setOnClickListener(v -> updateProfile());

        // Handle Cancel Button
        binding.buttonCancel.setOnClickListener(v -> finish());
    }

    private void updateProfile() {
        String newUsername = binding.editUsername.getText().toString().trim();
        String newEmail = binding.editEmail.getText().toString().trim();

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            binding.editEmail.setError("Invalid email format");
            return;
        }

        // Save updated data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", newUsername);
        editor.putString("email", newEmail);
        editor.apply();

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

        // Notify ProfileFragment to refresh data
        setResult(RESULT_OK);
        finish();
    }
}