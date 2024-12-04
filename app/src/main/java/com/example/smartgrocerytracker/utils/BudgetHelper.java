package com.example.smartgrocerytracker.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BudgetHelper {
    private static final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static void setupDatePicker(Context context, Button button, Date[] dateHolder) {
        button.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (dateHolder[0] != null) {
                calendar.setTime(dateHolder[0]);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                dateHolder[0] = calendar.getTime();
                button.setText(isoFormat.format(dateHolder[0]));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });
    }

    public static String formatDate(Date date) {
        return date != null ? isoFormat.format(date) : null;
    }

    public static Date parseDate(String dateString) {
        try {
            return isoFormat.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd.", e);
        }
    }
}
