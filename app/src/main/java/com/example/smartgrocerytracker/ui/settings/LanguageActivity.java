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

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.utils.LanguageUtil;

public class LanguageActivity extends AppCompatActivity {

    private RadioGroup languageRadioGroup;
    private RadioButton englishLanguage, hindiLanguage, spanishLanguage, frenchLanguage;
    private Button applyButton;

    private ImageButton cancelButton;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettingsPrefs";
    private static final String LANGUAGE_KEY = "Language";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LanguageUtil.setLocale(this); // Set the locale before inflating the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        languageRadioGroup = findViewById(R.id.language_radio_group);
        englishLanguage = findViewById(R.id.english_language);
        hindiLanguage = findViewById(R.id.hindi_language);

        applyButton = findViewById(R.id.apply_language_button);
        cancelButton = findViewById(R.id.close_button);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadPreferences();

        applyButton.setOnClickListener(v -> {
            // Save the selected language preference
            int selectedLanguageId = languageRadioGroup.getCheckedRadioButtonId();
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (selectedLanguageId == R.id.english_language) {
                editor.putString(LANGUAGE_KEY, "en");
            } else if (selectedLanguageId == R.id.hindi_language) {
                editor.putString(LANGUAGE_KEY, "hi");
            }

            editor.apply();

            setLocaleAndRestart();
        });
        setupCancelButton();
    }
    private void setupCancelButton() {
        cancelButton.setOnClickListener(v -> finish());
    }
    private void loadPreferences() {
        String language = sharedPreferences.getString(LANGUAGE_KEY, "en");

        switch (language) {
            case "en":
                englishLanguage.setChecked(true);
                break;
            case "hi":
                hindiLanguage.setChecked(true);
                break;
        }
    }

    private void setLocaleAndRestart() {
        LanguageUtil.setLocale(this);

        // Restart the main activity to apply the changes across the app
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity(); // Finish all activities
    }
}
