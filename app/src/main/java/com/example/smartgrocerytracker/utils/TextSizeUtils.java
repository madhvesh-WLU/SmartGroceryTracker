package com.example.smartgrocerytracker.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smartgrocerytracker.R;

public class TextSizeUtils {
    private static SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettingsPrefs";
    private static final String TEXT_SIZE_KEY = "TextSize";

    public static void applyTextSize(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String textSize = sharedPreferences.getString(TEXT_SIZE_KEY, "Medium");

        switch (textSize) {
            case "Small":
                context.setTheme(R.style.TextSizeSmall);
                break;
            case "Medium":
                context.setTheme(R.style.TextSizeMedium);
                break;
            case "Large":
                context.setTheme(R.style.TextSizeLarge);
                break;
            case "Extra Large":
                context.setTheme(R.style.TextSizeExtraLarge);
                break;
        }
    }
}
