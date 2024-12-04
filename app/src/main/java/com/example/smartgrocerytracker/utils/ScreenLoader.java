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
        // Create the overlay view
        overlay = new View(context);
        overlay.setBackgroundColor(0x80000000); // Semi-transparent black background

        // Create and configure the ProgressBar
        progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE); // Initially hidden

        // Create a FrameLayout.LayoutParams to center the progress bar in the overlay
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
            // Add the overlay and progress bar to the root view of the activity/fragment
            rootView.addView(overlay, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT)); // Full screen overlay
            rootView.addView(progressBar);
            progressBar.setVisibility(View.VISIBLE); // Show the loader
        }
    }

    // Hide the loader
    public void hide(ViewGroup rootView) {
        if (rootView != null && progressBar != null) {
            rootView.removeView(overlay); // Remove the overlay
            rootView.removeView(progressBar); // Remove the progress bar
        }
    }
}
