package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.core.content.ContextCompat;

import com.example.smartgrocerytracker.R;

import java.util.Random;

public class AvatarUtils {

    // Method to create an avatar bitmap with initials
    public static Bitmap createAvatar(Context context, String username) {
        // Extract initials from the username
        String initials = getInitials(username);

        // Avatar properties
        int avatarSize = 120; // Size of the avatar in pixels
        int textSize = 40; // Text size for initials

        // Create a bitmap
        Bitmap bitmap = Bitmap.createBitmap(avatarSize, avatarSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Generate a random color for the avatar background
        int backgroundColor = generateRandomColor(context);

        // Draw the circular background
        Paint circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(backgroundColor);
        canvas.drawCircle(avatarSize / 2, avatarSize / 2, avatarSize / 2, circlePaint);

        // Draw the initials
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Calculate the position of the text
        Rect textBounds = new Rect();
        textPaint.getTextBounds(initials, 0, initials.length(), textBounds);
        int x = avatarSize / 2;
        float y = ((float) avatarSize / 2) - ((textPaint.descent() + textPaint.ascent()) / 2);

        // Draw the initials on the canvas
        canvas.drawText(initials, x, y, textPaint);

        return bitmap;
    }

    // Method to get initials from a username
    private static String getInitials(String username) {
        String[] parts = username.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        for (String part : parts) {
            if (part.length() > 0) {
                initials.append(part.charAt(0));
            }
        }

        return initials.toString().toUpperCase();
    }

    // Method to generate a random color for the avatar
    private static int generateRandomColor(Context context) {
        // You can modify this to use a consistent set of colors if preferred
        Random random = new Random();
        int[] colors = {
                ContextCompat.getColor(context, R.color.vibrant_orange),
                ContextCompat.getColor(context, R.color.green),
                ContextCompat.getColor(context, R.color.red),
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.colorPrimary)
        };
        return colors[random.nextInt(colors.length)];
    }
}
