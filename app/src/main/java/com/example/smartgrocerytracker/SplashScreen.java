package com.example.smartgrocerytracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.example.smartgrocerytracker.ui.Login;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar progressBar;
    private MotionLayout motionLayout;

    // Total duration for the splash screen (in milliseconds)
    private static final int TOTAL_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        motionLayout = findViewById(R.id.motionLayout);
        progressBar = findViewById(R.id.progressBar);

        // Start the "pop out" animation
        motionLayout.transitionToState(R.id.middle);

        // Start the AsyncTask to update the ProgressBar
        new ProgressBarAsyncTask().execute();

        // Handle MotionLayout transitions
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                // No action needed here
            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                // No action needed here
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                if (currentId == R.id.middle) {
                    // Start "pop in" animation
                    motionLayout.transitionToState(R.id.end);
                }
                // No action needed when reaching the 'end' state
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                // No action needed here
            }
        });
    }

    /**
     * AsyncTask to update the ProgressBar in the background.
     */
    private class ProgressBarAsyncTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Initialize ProgressBar
            progressBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int progress = 0;
            int sleepTime = TOTAL_DURATION / 100; // Duration per progress increment

            while (progress < 100) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress++;
                publishProgress(progress);
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            // Update ProgressBar with the current progress value
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Navigate to Login activity once ProgressBar is complete
            navigateToLogin();
        }
    }

    /**
     * Navigates to the Login activity and finishes the SplashScreen.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(SplashScreen.this, Login.class);
        startActivity(intent);
        finish();
    }
}