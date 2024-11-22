package com.example.smartgrocerytracker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.example.smartgrocerytracker.ui.Login;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        MotionLayout motionLayout = findViewById(R.id.motionLayout);

        // Start the "pop out" animation
        motionLayout.transitionToState(R.id.middle);

        // After "pop out," trigger the "pop in" animation
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {}

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {}

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                if (currentId == R.id.middle) {
                    // Start "pop in" animation
                    motionLayout.transitionToState(R.id.end);
                } else if (currentId == R.id.end) {
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(SplashScreen.this, Login.class));
                        finish();
                    }, 1000);
                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {}
        });
    }
}
