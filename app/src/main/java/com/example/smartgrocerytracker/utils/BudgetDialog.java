package com.example.smartgrocerytracker.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.fetchBudgetDetails;
import com.example.smartgrocerytracker.services.storeBudgetServices;
import com.example.smartgrocerytracker.services.updateBudgetDetails;
import com.example.smartgrocerytracker.ui.grocerylist.BudgetActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.Objects;

public class BudgetDialog {
    private final Context context;

    public BudgetDialog(Context context) {
        this.context = context;
    }

    public void showBudgetDialog(String currentBudget, String currentStartDate, String currentEndDate, boolean isEditMode) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        String budgetId = sharedPreferences.getString("budget_id", null);

        // Check if new budget or edit
        if (isEditMode || Objects.equals(budgetId, "null")) {
            View setupView = inflater.inflate(R.layout.activity_budget, null);
            dialog.setContentView(setupView);

            TextInputEditText editTextBudget = setupView.findViewById(R.id.editTextAmount);
            MaterialButton buttonStartDate = setupView.findViewById(R.id.buttonStartDate);
            MaterialButton buttonEndDate = setupView.findViewById(R.id.buttonEndDate);
            MaterialButton buttonSubmit = setupView.findViewById(R.id.buttonSubmit);
            ImageButton cancelButton = setupView.findViewById(R.id.close_button);

            final Date[] startDate = {null};
            final Date[] endDate = {null};

            // Pre-fill fields if available
            if (currentBudget != null) editTextBudget.setText(currentBudget);
            if (currentStartDate != null) buttonStartDate.setText(currentStartDate);
            if (currentEndDate != null) buttonEndDate.setText(currentEndDate);

            // Setup Date Pickers
            BudgetHelper.setupDatePicker(context, buttonStartDate, startDate);
            BudgetHelper.setupDatePicker(context, buttonEndDate, endDate);

            buttonSubmit.setOnClickListener(v -> {
                String budgetInput = editTextBudget.getText().toString();

                if (budgetInput.isEmpty() || startDate[0] == null || endDate[0] == null) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int budget = Integer.parseInt(budgetInput);
                    String formattedStartDate = BudgetHelper.formatDate(startDate[0]);
                    String formattedEndDate = BudgetHelper.formatDate(endDate[0]);

                    RequestQueue queue = Volley.newRequestQueue(context);
                    if (isEditMode) {
                        updateBudgetDetails.putBudgetRequest(context, queue, budgetId, budget, formattedStartDate, formattedEndDate);
                    } else {
                        storeBudgetServices.sendBudgetRequest(context, queue, budget, formattedStartDate, formattedEndDate);
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("budget_id", budgetId != null ? budgetId : "generated_id"); // Replace with actual ID from backend
                    editor.apply();

                    Toast.makeText(context, isEditMode ? "Budget Updated!" : "Budget Saved!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Invalid budget value", Toast.LENGTH_SHORT).show();
                }
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());
        } else {
            // Show budget details
            fetchBudgetDetails.getBudgetService(context, Volley.newRequestQueue(context), budgetId, new fetchBudgetDetails.BudgetDetailsUpdateListener() {
                @Override
                public void onBudgetDetailsUpdated(String amount, String startDate, String endDate, String spentAmount) {
                    View detailsView = inflater.inflate(R.layout.dialog_budget_details, null);
                    dialog.setContentView(detailsView);

                    TextView budgetAmount = detailsView.findViewById(R.id.textViewBudgetAmount);
                    TextView start_date = detailsView.findViewById(R.id.textViewStartDate);
                    TextView end_date = detailsView.findViewById(R.id.textViewEndDate);
                    TextView spent_amount = detailsView.findViewById(R.id.textViewSpentAmount);

                    budgetAmount.setText("Budget Amount: " + amount);
                    start_date.setText("Start Date: " + startDate);
                    end_date.setText("End Date: " + endDate);
                    spent_amount.setText("Spent Amount: " + spentAmount);

                    MaterialButton buttonEdit = detailsView.findViewById(R.id.buttonEditBudget);
                    buttonEdit.setOnClickListener(v -> {
                        dialog.dismiss();

                        // Create an Intent to open BudgetActivity
                        Intent intent = new Intent(context, BudgetActivity.class);
                        intent.putExtra("isEditMode", true); // Indicate that this is edit mode
                        intent.putExtra("amount", amount); // Pass the previously input budget amount
                        intent.putExtra("startDate", startDate); // Pass the previously input start date
                        intent.putExtra("endDate", endDate); // Pass the previously input end date

                        // Start the activity
                        context.startActivity(intent);
                    });

                    MaterialButton buttonClose = detailsView.findViewById(R.id.buttonClose);
                    buttonClose.setOnClickListener(v -> dialog.dismiss());
                }
            });
        }

        dialog.show();
    }
}
