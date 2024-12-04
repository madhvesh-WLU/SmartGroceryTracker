package com.example.smartgrocerytracker.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageUtil {

    private static final String PREFS_NAME = "AppSettingsPrefs";
    private static final String LANGUAGE_KEY = "Language";

    public static void setLocale(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String languageCode = sharedPreferences.getString(LANGUAGE_KEY, "en");
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}

