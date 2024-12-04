package com.example.smartgrocerytracker.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.utils.LanguageUtil;

public class TextsizeActivity extends AppCompatActivity {

    private RadioGroup textSizeRadioGroup;
    private RadioButton smallTextSize, mediumTextSize, largeTextSize, extraLargeTextSize;

    private ImageButton cancelButton;
    private Button applyButton;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettingsPrefs";
    private static final String TEXT_SIZE_KEY = "TextSize";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LanguageUtil.setLocale(this);
        super.onCreate(savedInstanceState);

        // Initialize SharedPreferences before setting the content view or applying text size
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Apply the saved text size theme
        applyTextSize();

        setContentView(R.layout.activity_textsize);

        // Initialize Views
        textSizeRadioGroup = findViewById(R.id.text_size_radio_group);
        smallTextSize = findViewById(R.id.small_text_size);
        mediumTextSize = findViewById(R.id.medium_text_size);
        largeTextSize = findViewById(R.id.large_text_size);
        extraLargeTextSize = findViewById(R.id.extra_large_text_size);
        applyButton = findViewById(R.id.apply_text_size_button);
        cancelButton = findViewById(R.id.close_button);

        // Set previously saved text size option
        loadPreferences();

        // Set up the Apply button
        applyButton.setOnClickListener(v -> {
            // Save the selected text size preference
            int selectedTextSizeId = textSizeRadioGroup.getCheckedRadioButtonId();
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (selectedTextSizeId == R.id.small_text_size) {
                editor.putString(TEXT_SIZE_KEY, "Small");
            } else if (selectedTextSizeId == R.id.medium_text_size) {
                editor.putString(TEXT_SIZE_KEY, "Medium");
            } else if (selectedTextSizeId == R.id.large_text_size) {
                editor.putString(TEXT_SIZE_KEY, "Large");
            } else if (selectedTextSizeId == R.id.extra_large_text_size) {
                editor.putString(TEXT_SIZE_KEY, "Extra Large");
            }

            editor.apply();



            // Restart activity to apply the new text size
            applyTextSizeGlobally();
        });
        setupCancelButton();
    }
    private void setupCancelButton() {
        cancelButton.setOnClickListener(v -> finish());
    }
    private void loadPreferences() {
        String textSize = sharedPreferences.getString(TEXT_SIZE_KEY, "Medium");

        switch (textSize) {
            case "Small":
                smallTextSize.setChecked(true);
                break;
            case "Medium":
                mediumTextSize.setChecked(true);
                break;
            case "Large":
                largeTextSize.setChecked(true);
                break;
            case "Extra Large":
                extraLargeTextSize.setChecked(true);
                break;
        }
    }

    private void applyTextSize() {
        // This method should set the theme based on the saved preference
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        }

        String textSize = sharedPreferences.getString(TEXT_SIZE_KEY, "Medium");

        switch (textSize) {
            case "Small":
                setTheme(R.style.TextSizeSmall);
                break;
            case "Medium":
                setTheme(R.style.TextSizeMedium);
                break;
            case "Large":
                setTheme(R.style.TextSizeLarge);
                break;
            case "Extra Large":
                setTheme(R.style.TextSizeExtraLarge);
                break;
        }
    }

    private void applyTextSizeGlobally() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}