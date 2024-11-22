package com.example.smartgrocerytracker.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.fetchBudgetDetails;
import com.example.smartgrocerytracker.services.storeBudgetServices;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BudgetDialog {

    private final Context context;

    public BudgetDialog(Context context) {
        this.context = context;
    }

    public void showBudgetDialog() {
        // Retrieve budget_id from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        String budgetId = sharedPreferences.getString("budget_id", null);

        // Create a BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        if (Objects.equals(budgetId, "null")) {
            // Inflate and display the setup layout
            View setupView = inflater.inflate(R.layout.dialog_budget, null);
            dialog.setContentView(setupView);

            TextInputEditText editTextBudget = setupView.findViewById(R.id.editTextBudget);
            MaterialButton buttonStartDate = setupView.findViewById(R.id.buttonStartDate);
            MaterialButton buttonEndDate = setupView.findViewById(R.id.buttonEndDate);
            MaterialButton buttonSubmit = setupView.findViewById(R.id.buttonSubmit);

            final Date[] startDate = {null};
            final Date[] endDate = {null};
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            // Setup start date picker
            buttonStartDate.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    startDate[0] = calendar.getTime();
                    buttonStartDate.setText(isoFormat.format(startDate[0]));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            });

            // Setup end date picker
            buttonEndDate.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    endDate[0] = calendar.getTime();
                    buttonEndDate.setText(isoFormat.format(endDate[0]));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            });

            // Handle budget submission
            buttonSubmit.setOnClickListener(v -> {
                String budgetInput = editTextBudget.getText().toString();

                if (budgetInput.isEmpty() || startDate[0] == null || endDate[0] == null) {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int budget = Integer.parseInt(budgetInput);
                    String formattedStartDate = isoFormat.format(startDate[0]);
                    String formattedEndDate = isoFormat.format(endDate[0]);

                    RequestQueue queue = Volley.newRequestQueue(context);
                    storeBudgetServices.sendBudgetRequest(context, queue, budget, formattedStartDate, formattedEndDate);

                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Please enter a valid number for the budget", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            if (budgetId != null && !budgetId.equals("null")) {
                // Fetch updated budget details with listener
                RequestQueue queue = Volley.newRequestQueue(context);
                fetchBudgetDetails.getBudgetService(context, queue, budgetId, new fetchBudgetDetails.BudgetDetailsUpdateListener() {
                    @Override
                    public void onBudgetDetailsUpdated(String amount, String startDate, String endDate) {
                        View detailsView = inflater.inflate(R.layout.dialog_budget_details, null);
                        dialog.setContentView(detailsView);

                        TextView budget_amount = detailsView.findViewById(R.id.textViewBudgetAmount);
                        TextView start_date = detailsView.findViewById(R.id.textViewStartDate);
                        TextView end_date = detailsView.findViewById(R.id.textViewEndDate);

                        // Display the updated budget details
                        budget_amount.setText("Budget Amount: " + amount);
                        start_date.setText("Start Date: " + startDate);
                        end_date.setText("End Date: " + endDate);

                        // Handle Edit Budget action
                        MaterialButton buttonEditBudget = detailsView.findViewById(R.id.buttonEditBudget);
                        MaterialButton buttonClose = detailsView.findViewById(R.id.buttonClose);

                        // Handle Edit Budget action
                        buttonEditBudget.setOnClickListener(v -> {
                            dialog.dismiss();
                            showBudgetDialog(); // Re-open with setup layout
                        });

                        // Handle Close action
                        buttonClose.setOnClickListener(v -> dialog.dismiss());
                    }
                });
            }
        }

        dialog.show();
    }
}
