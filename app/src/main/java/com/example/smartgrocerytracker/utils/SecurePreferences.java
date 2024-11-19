package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class SecurePreferences {

    private static final String PREFS_NAME = "user_token";
    private static final String TOKEN_KEY = "auth_token";
    private static SharedPreferences encryptedPreferences;

    private static SharedPreferences getEncryptedPrefs(Context context) {
        if (encryptedPreferences == null) {
            try {
                MasterKey masterKey = new MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                encryptedPreferences = EncryptedSharedPreferences.create(
                        context,
                        PREFS_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );

            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
        return encryptedPreferences;
    }

    // Save token
    public static void saveAuthToken(Context context, String token) {
        SharedPreferences.Editor editor = getEncryptedPrefs(context).edit();
        editor.putString(TOKEN_KEY, token);
        editor.apply();
    }

    // Retrieve token
    public static String getAuthToken(Context context) {
        return getEncryptedPrefs(context).getString(TOKEN_KEY, null);
    }

    public static boolean removeAuthToken(Context context) {
        SharedPreferences.Editor editor = getEncryptedPrefs(context).edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
        if(getAuthToken(context) == null){
            return true;
        }
        return false;
    }
}
