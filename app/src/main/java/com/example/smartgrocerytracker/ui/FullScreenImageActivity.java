package com.example.smartgrocerytracker.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.ZoomableImageView;

public class FullScreenImageActivity extends AppCompatActivity {
    public static final String IMAGE_URI_KEY = "image_uri_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        ZoomableImageView fullScreenImageView = findViewById(R.id.fullScreenImageView);
        Button closeButton = findViewById(R.id.closeButton);

        String imageUriString = getIntent().getStringExtra(IMAGE_URI_KEY);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);

            // Load the image into ZoomableImageView using Glide
            Glide.with(this)
                    .load(imageUri)
                    .into(fullScreenImageView);
        } else {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }

        closeButton.setOnClickListener(v -> finish());
    }
}