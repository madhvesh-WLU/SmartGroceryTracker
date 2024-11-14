package com.example.smartgrocerytracker.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.storeBudgetServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BudgetDialog {

    private final Context context;

    public BudgetDialog(Context context) {
        this.context = context;
    }

    public void showBudgetDialog() {
        // Create a BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_budget, null);
        dialog.setContentView(dialogView);

        TextInputEditText editTextBudget = dialogView.findViewById(R.id.editTextBudget);
        MaterialButton buttonStartDate = dialogView.findViewById(R.id.buttonStartDate);
        MaterialButton buttonEndDate = dialogView.findViewById(R.id.buttonEndDate);
        MaterialButton buttonSubmit = dialogView.findViewById(R.id.buttonSubmit);

        // Use an array to hold Date values so they can be modified within the lambda expressions
        final Date[] startDate = {null};
        final Date[] endDate = {null};

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC-5"));


        // Define the date format for display and for API submission
//        SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());

        // Set up start date picker
        buttonStartDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth, 0, 0, 0); // Set time to 00:00:00
                startDate[0] = calendar.getTime(); // Modify the array element
                buttonStartDate.setText("2024-11-12"); // Display formatted date
                String displayFormat = isoFormat.format(startDate[0]);
                Log.i("asdasd", displayFormat);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Set up end date picker
        buttonEndDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth, 0, 0, 0); // Set time to 00:00:00
                endDate[0] = calendar.getTime(); // Modify the array element
                buttonEndDate.setText("2024-11-12"); // Display formatted date
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Handle budget submission
        buttonSubmit.setOnClickListener(v -> {
            String budgetInput = editTextBudget.getText().toString();

            // Check if budget input is empty
            if (budgetInput.isEmpty() || startDate[0] == null || endDate[0] == null) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Convert budget input to an integer
                int budget = Integer.parseInt(budgetInput);

                // Convert Date to formatted String for API call
                String formattedStartDate = isoFormat.format(startDate[0]);
                String formattedEndDate = isoFormat.format(endDate[0]);
                // API call for storing budget into backend
                RequestQueue queue = Volley.newRequestQueue(context);
                storeBudgetServices.sendBudgetRequest(context, queue, budget, formattedStartDate, formattedEndDate);

                Toast.makeText(context, startDate[0] +  formattedEndDate, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } catch (NumberFormatException e) {
                // Show error message if budget input is not a valid integer
                Toast.makeText(context, "Please enter a valid number for the budget", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.show();
    }
}
