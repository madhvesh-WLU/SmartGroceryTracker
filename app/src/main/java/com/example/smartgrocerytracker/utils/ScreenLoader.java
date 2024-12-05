package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class ScreenLoader {

    private ProgressBar progressBar;
    private View overlay;

    public ScreenLoader(Context context) {

        overlay = new View(context);
        overlay.setBackgroundColor(0x80000000);

        // Create and configure the ProgressBar
        progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);


        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        progressBar.setLayoutParams(layoutParams);
    }

    // Show the loader
    public void show(ViewGroup rootView) {
        if (rootView != null && progressBar != null) {

            rootView.addView(overlay, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            rootView.addView(progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    // Hide the loader
    public void hide(ViewGroup rootView) {
        if (rootView != null && progressBar != null) {
            rootView.removeView(overlay);
            rootView.removeView(progressBar);
        }
    }
}
