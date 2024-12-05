package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.example.smartgrocerytracker.R;

public class SnackbarHelper {

    public static void showSnackbar(@NonNull View view, @NonNull String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        // Customize the Snackbar appearance
        customizeSnackbar(snackbar, view.getContext());

        snackbar.show();
    }


    public static void showSnackbarWithAction(@NonNull View view, @NonNull String message,
                                              @NonNull String actionText, @NonNull View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(actionText, actionListener);

        // Customize the Snackbar appearance
        customizeSnackbar(snackbar, view.getContext());

        snackbar.show();
    }

    private static void customizeSnackbar(Snackbar snackbar, Context context) {

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundResource(R.drawable.snackbar_background);  // Customize your background drawable as needed

        // Customize the text color of the Snackbar message
        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(android.R.color.white));

    }
}

