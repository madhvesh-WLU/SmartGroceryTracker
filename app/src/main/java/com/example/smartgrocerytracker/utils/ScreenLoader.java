package com.example.smartgrocerytracker.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.example.smartgrocerytracker.R;

public class ScreenLoader {
    private Dialog dialog;

    public ScreenLoader(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.full_screen_loader);
    }

    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    public void hide() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}