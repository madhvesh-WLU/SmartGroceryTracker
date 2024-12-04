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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.storeBudgetServices;
import com.example.smartgrocerytracker.services.updateBudgetDetails;
import com.example.smartgrocerytracker.utils.BudgetHelper;
import com.example.smartgrocerytracker.utils.LanguageUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BudgetActivity extends AppCompatActivity {

    private TextInputEditText editTextBudget;
    private MaterialButton buttonStartDate, buttonEndDate, buttonSubmit;
    private GridLayout calculatorLayout;
    private ImageButton buttonCancel;

    private Date startDate = null;
    private Date endDate = null;
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private boolean isEditMode = false;
    private String budgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        // Check if this is an edit operation
        isEditMode = getIntent().getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            // Pre-fill data for editing
            String amount = getIntent().getStringExtra("amount");
            String startDate = getIntent().getStringExtra("startDate");
            String endDate = getIntent().getStringExtra("endDate");

            if (amount != null) editTextBudget.setText(amount);
            if (startDate != null) buttonStartDate.setText(startDate);
            if (endDate != null) buttonEndDate.setText(endDate);

            buttonSubmit.setText("Update");
        } else {
            buttonSubmit.setText("Submit");
        }

        // Set up date pickers
        final Date[] startDateHolder = {null};
        final Date[] endDateHolder = {null};
        BudgetHelper.setupDatePicker(this, buttonStartDate, startDateHolder);
        BudgetHelper.setupDatePicker(this, buttonEndDate, endDateHolder);

        // Set up calculator
        setupCalculator();

        // Set up submit and cancel buttons
        setupSubmitButton(startDateHolder, endDateHolder, sharedPreferences);
        setupCancelButton();
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

    private void setupSubmitButton(Date[] startDateHolder, Date[] endDateHolder, SharedPreferences sharedPreferences) {
        buttonSubmit.setOnClickListener(v -> {
            String budgetInput = editTextBudget.getText().toString().trim();

            if (TextUtils.isEmpty(budgetInput) || startDateHolder[0] == null || endDateHolder[0] == null) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int budget = Integer.parseInt(budgetInput);
                String formattedStartDate = BudgetHelper.formatDate(startDateHolder[0]);
                String formattedEndDate = BudgetHelper.formatDate(endDateHolder[0]);

                RequestQueue queue = Volley.newRequestQueue(this);

                if (isEditMode) {
                    // Update existing budget
                    updateBudgetDetails.putBudgetRequest(this, queue, budgetId, budget, formattedStartDate, formattedEndDate);
                    Toast.makeText(this, "Budget Updated!", Toast.LENGTH_SHORT).show();
                } else {
                    // Store new budget
                    storeBudgetServices.sendBudgetRequest(this, queue, budget, formattedStartDate, formattedEndDate);

                    // Save new budget ID to SharedPreferences
                    budgetId = "generated_budget_id"; // Replace with actual ID from backend
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("budget_id", budgetId);
                    editor.apply();

                    Toast.makeText(this, "Budget Saved!", Toast.LENGTH_SHORT).show();
                }

                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid budget value", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCancelButton() {
        buttonCancel.setOnClickListener(v -> finish());
    }
}
