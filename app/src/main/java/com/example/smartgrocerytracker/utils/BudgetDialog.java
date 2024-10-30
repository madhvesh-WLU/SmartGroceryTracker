package com.example.smartgrocerytracker.utils;


import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.smartgrocerytracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

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

        String[] startDate = {""};
        String[] endDate = {""};

        // Set up start date picker
        buttonStartDate.setOnClickListener(v -> {
            // Use a DatePickerDialog to select the start date
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                startDate[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
                buttonStartDate.setText(startDate[0]);
            }, 2023, 0, 1); // Default date
            datePickerDialog.show();
        });

        // Set up end date picker
        buttonEndDate.setOnClickListener(v -> {
            // Use a DatePickerDialog to select the end date
            DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                endDate[0] = dayOfMonth + "/" + (month + 1) + "/" + year;
                buttonEndDate.setText(endDate[0]);
            }, 2023, 0, 1); // Default date
            datePickerDialog.show();
        });

        // Handle budget submission
        buttonSubmit.setOnClickListener(v -> {
            String budget = editTextBudget.getText().toString();
            if (budget.isEmpty() || startDate[0].isEmpty() || endDate[0].isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Save the budget details or handle them as needed
                Toast.makeText(context, "Budget set: " + budget + " CAD\nFrom: " + startDate[0] + "\nTo: " + endDate[0], Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });

        // Show the dialog
        dialog.show();
    }
}
