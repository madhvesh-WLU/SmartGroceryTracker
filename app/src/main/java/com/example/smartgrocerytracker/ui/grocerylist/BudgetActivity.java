package com.example.smartgrocerytracker.ui.grocerylist;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.SharedBudgetViewModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.storeBudgetServices;
import com.example.smartgrocerytracker.services.updateBudgetDetails;
import com.example.smartgrocerytracker.utils.BudgetHelper;
import com.example.smartgrocerytracker.utils.LanguageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class BudgetActivity extends AppCompatActivity {

    private TextInputEditText editTextBudget;
    private MaterialButton buttonStartDate, buttonEndDate, buttonSubmit;
    private GridLayout calculatorLayout;
    private ImageButton buttonCancel;

    private Date startDate = null;
    private Date endDate = null;

    private boolean isEditMode = false;
    private String budgetId;
    private String spentAmount;
    private SharedBudgetViewModel sharedBudgetViewModel;

    private final DecimalFormat decimalFormat = new DecimalFormat("#.00"); // Ensure up to 2 decimal places

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtil.setLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Retrieve the budget ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        budgetId = sharedPreferences.getString("budget_id", null);

        // Initialize UI elements
        editTextBudget = findViewById(R.id.editTextAmount);
        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonCancel = findViewById(R.id.close_button);
        calculatorLayout = findViewById(R.id.gridLayout);

        // Initialize Shared ViewModel
        sharedBudgetViewModel = new ViewModelProvider(this).get(SharedBudgetViewModel.class);

        // Check if this is an edit operation
        isEditMode = getIntent().getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            // Pre-fill data for editing
            String amount = getIntent().getStringExtra("amount");
            spentAmount = getIntent().getStringExtra("spent_amount");
            String startDateStr = getIntent().getStringExtra("startDate");
            String endDateStr = getIntent().getStringExtra("endDate");

            if (amount != null) editTextBudget.setText(amount);
            if (startDateStr != null) {
                buttonStartDate.setText(BudgetHelper.formatDate(BudgetHelper.parseDate(startDateStr)));
                startDate = BudgetHelper.parseDate(startDateStr);
            }
            if (endDateStr != null) {
                buttonEndDate.setText(BudgetHelper.formatDate(BudgetHelper.parseDate(endDateStr)));
                endDate = BudgetHelper.parseDate(endDateStr);
            }

            buttonSubmit.setText("Update");
        } else {
            // Leave fields empty by default
            buttonSubmit.setText("Submit");
        }

        // Set up date pickers
        setupDatePicker(buttonStartDate, true); // Start Date picker
        setupDatePicker(buttonEndDate, false); // End Date picker

        // Set up calculator
        setupCalculator();

        // Set up submit and cancel buttons
        setupSubmitButton(sharedPreferences);
        setupCancelButton();
    }

    private void setupDatePicker(MaterialButton button, boolean isStartDate) {
        button.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        button.setText(formattedDate);

                        // Update startDate or endDate based on the button clicked
                        if (isStartDate) {
                            startDate = BudgetHelper.parseDate(formattedDate);
                        } else {
                            endDate = BudgetHelper.parseDate(formattedDate);
                        }
                    },
                    year, month, day
            );

            // Set min and max date for DatePickerDialog
            if (startDate != null && !isStartDate) {
                datePickerDialog.getDatePicker().setMinDate(startDate.getTime());
            }
            if (endDate != null && isStartDate) {
                datePickerDialog.getDatePicker().setMaxDate(endDate.getTime());
            }

            datePickerDialog.show();
        });
    }

    private void setupCalculator() {
        int[] buttonIds = {
                R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.buttonDot
        };

        for (int id : buttonIds) {
            calculatorLayout.findViewById(id).setOnClickListener(v -> {
                Button button = (Button) v;
                editTextBudget.append(button.getText());
            });
        }

        calculatorLayout.findViewById(R.id.buttonClear).setOnClickListener(v -> editTextBudget.setText(""));
    }

    private void setupSubmitButton(SharedPreferences sharedPreferences) {
        buttonSubmit.setOnClickListener(v -> {
            String budgetInput = editTextBudget.getText().toString().trim();

            if (TextUtils.isEmpty(budgetInput) || startDate == null || endDate == null) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Parse budget and spent amount as float with up to 2 decimal places
                float budget = parseAndFormat(budgetInput);
                float spent = isEditMode && spentAmount != null ? parseAndFormat(spentAmount) : 0.00f;

                String formattedStartDate = BudgetHelper.formatDate(startDate);
                String formattedEndDate = BudgetHelper.formatDate(endDate);

                RequestQueue queue = Volley.newRequestQueue(this);

                if (isEditMode) {
                    // Update existing budget
                    updateBudgetDetails.putBudgetRequest(this, queue, budgetId, String.valueOf(spent), budget, formattedStartDate, formattedEndDate, updatedBudgetModel -> {
                        // Update Shared ViewModel
                        sharedBudgetViewModel.setBudgetModel(updatedBudgetModel);
                        Toast.makeText(this, "Budget Updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    // Store new budget
                    storeBudgetServices.sendBudgetRequest(this, queue, budget, formattedStartDate, formattedEndDate, newBudgetModel -> {
                        // Save new budget ID to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("budget_id", newBudgetModel.getBudgetId());
                        editor.apply();

                        // Update Shared ViewModel
                        sharedBudgetViewModel.setBudgetModel(newBudgetModel);
                        Toast.makeText(this, "Budget Saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

            } catch (NumberFormatException | ParseException e) {
                Toast.makeText(this, "Invalid budget value. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCancelButton() {
        buttonCancel.setOnClickListener(v -> finish());
    }

    // Helper method to parse and format input as float with up to 2 decimal places
    private float parseAndFormat(String value) throws ParseException {
        Number number = decimalFormat.parse(value); // Parse to ensure valid float
        if (number == null) throw new ParseException("Invalid number format", 0);
        return Float.parseFloat(decimalFormat.format(number.floatValue())); // Ensure 2 decimal places
    }
}