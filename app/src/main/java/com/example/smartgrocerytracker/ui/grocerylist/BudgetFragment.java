package com.example.smartgrocerytracker.ui.grocerylist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.fetchBudgetDetails;
import com.example.smartgrocerytracker.services.storeBudgetServices;
import com.example.smartgrocerytracker.services.updateBudgetDetails;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private String budgetId;

    private Date startDate = null;
    private Date endDate = null;
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private TextInputEditText editTextBudget;
    private MaterialButton buttonStartDate, buttonEndDate, buttonSubmit;
    private ImageButton buttonCancel;
    private GridLayout calculatorLayout;

    private boolean isEditMode = false;
    private String passedAmount;
    private String passedStartDate;
    private String passedEndDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        budgetId = sharedPreferences.getString("budget_id", null);

        // Check if data is passed to the fragment
        if (getArguments() != null) {
            isEditMode = getArguments().getBoolean("is_edit_mode", false);
            passedAmount = getArguments().getString("amount");
            passedStartDate = getArguments().getString("start_date");
            passedEndDate = getArguments().getString("end_date");
        }

        // Inflate layout
        if (isEditMode || (budgetId == null || budgetId.equals("null"))) {
            return setupBudgetLayout(inflater.inflate(R.layout.fragment_budget, container, false));
        } else {
            showDetailsDialog(budgetId);
            return inflater.inflate(R.layout.fragment_home, container, false); // Keep the current page (e.g., home page)
        }
    }

    public void showDetailsDialog(String budgetId) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View detailsView = inflater.inflate(R.layout.dialog_budget_details, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(detailsView);
        AlertDialog dialog = builder.create();

        TextView budgetAmount = detailsView.findViewById(R.id.textViewBudgetAmount);
        TextView startDateView = detailsView.findViewById(R.id.textViewStartDate);
        TextView endDateView = detailsView.findViewById(R.id.textViewEndDate);
        TextView spentAmount = detailsView.findViewById(R.id.textViewSpentAmount);
        MaterialButton buttonEditBudget = detailsView.findViewById(R.id.buttonEditBudget);
        MaterialButton buttonClose = detailsView.findViewById(R.id.buttonClose);

        // Fetch budget details and populate the view
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchBudgetDetails.getBudgetService(requireContext(), queue, budgetId, new fetchBudgetDetails.BudgetDetailsUpdateListener() {
            @Override
            public void onBudgetDetailsUpdated(String amount, String startDate, String endDate, String spent) {
                budgetAmount.setText("Budget Amount: " + amount);
                spentAmount.setText("Spent Amount: " + spent);
                startDateView.setText("Start Date: " + startDate);
                endDateView.setText("End Date: " + endDate);

                // Handle edit button
                buttonEditBudget.setOnClickListener(v -> {
                    dialog.dismiss();
                    // Reload fragment with edit mode and pre-filled data
                    BudgetFragment budgetFragment = new BudgetFragment();
                    Bundle args = new Bundle();
                    args.putString("amount", amount);
                    args.putString("start_date", startDate);
                    args.putString("end_date", endDate);
                    args.putBoolean("is_edit_mode", true);
                    budgetFragment.setArguments(args);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_grocerylist, budgetFragment)
                            .addToBackStack(null)
                            .commit();

                });
                // Handle close button
                buttonClose.setOnClickListener(v -> dialog.dismiss());
            }
        });

        dialog.show();
    }

    private View setupBudgetLayout(View view) {
        // Initialize views
        editTextBudget = view.findViewById(R.id.editTextAmount);
        buttonStartDate = view.findViewById(R.id.buttonStartDate);
        buttonEndDate = view.findViewById(R.id.buttonEndDate);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonCancel = view.findViewById(R.id.close_button);
        calculatorLayout = view.findViewById(R.id.gridLayout);

        // Pre-fill fields if in edit mode
        if (isEditMode) {
            if (passedAmount != null) editTextBudget.setText(passedAmount);
            if (passedStartDate != null) buttonStartDate.setText(passedStartDate);
            if (passedEndDate != null) buttonEndDate.setText(passedEndDate);

            buttonSubmit.setText("Update");
        }

        // Set up date pickers
        setupDatePickers();

        // Set up calculator
        setupCalculator();

        // Handle submit button click
        buttonSubmit.setOnClickListener(v -> handleSubmit());

        // Handle cancel button click
        buttonCancel.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void setupDatePickers() {
        buttonStartDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (startDate != null) calendar.setTime(startDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                startDate = calendar.getTime();
                buttonStartDate.setText(isoFormat.format(startDate));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        buttonEndDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (endDate != null) calendar.setTime(endDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                endDate = calendar.getTime();
                buttonEndDate.setText(isoFormat.format(endDate));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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

    private void handleSubmit() {
        String budgetInput = editTextBudget.getText().toString().trim();

        if (TextUtils.isEmpty(budgetInput) || startDate == null || endDate == null) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int budget = Integer.parseInt(budgetInput);
            String formattedStartDate = isoFormat.format(startDate);
            String formattedEndDate = isoFormat.format(endDate);

            RequestQueue queue = Volley.newRequestQueue(requireContext());

            if (isEditMode) {
                // Update existing budget
                updateBudgetDetails.putBudgetRequest(requireContext(), queue, budgetId, budget, formattedStartDate, formattedEndDate);
                Toast.makeText(requireContext(), "Budget Updated!", Toast.LENGTH_SHORT).show();
            } else {
                // Store a new budget
                storeBudgetServices.sendBudgetRequest(requireContext(), queue, budget, formattedStartDate, formattedEndDate);
                // Store the new budgetId in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("budget_id", "new-budget-id"); // Replace with actual budget ID returned from the server
                editor.apply();
                Toast.makeText(requireContext(), "Budget Saved!", Toast.LENGTH_SHORT).show();
            }

            // Check if we need to navigate or simply pop the back stack
            if (isEditMode) {
                // For edit mode, pop the fragment off the stack
                requireFragmentManager().beginTransaction().remove(this).commit();
            } else {
                // For adding the budget for the first time, navigate to the HomeFragment
                NavController navController = NavHostFragment.findNavController(BudgetFragment.this);
                navController.navigate(R.id.nav_home);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid budget value", Toast.LENGTH_SHORT).show();
        }
    }


}
