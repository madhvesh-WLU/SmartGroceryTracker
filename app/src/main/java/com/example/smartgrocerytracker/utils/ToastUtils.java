package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    private static Toast currentToast;

    public static void showToast(Context context, String message) {
        if (currentToast != null) {
            currentToast.cancel(); // Cancel the previous toast
        }
        currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        currentToast.show();
    }
}