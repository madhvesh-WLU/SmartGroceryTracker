// BillInputDialogFragment.java
package com.example.smartgrocerytracker.ui.grocerylist;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.addExpenseServices;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class BillInputDialogFragment extends DialogFragment {

    private EditText billNameEditText;
    private EditText dateOfPurchaseEditText;
    private EditText storeTotalQuantityEditText;
    private EditText totalPriceEditText;
    private EditText descriptionEditText;
    private Button nextButton;
    private ProgressBar loadingSpinner;
    private String final_expensId;
    private ImageButton closeButton;

    public BillInputDialogFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_bill, container, false);


        initializeViews(view);
        setupDatePicker();
        setupNextButton();
        setupCloseButton();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make the dialog width match the parent width
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
    private void initializeViews(View view) {
        billNameEditText = view.findViewById(R.id.bill_name);
        closeButton= view.findViewById(R.id.close_button);
        dateOfPurchaseEditText = view.findViewById(R.id.date_of_purchase);
        storeTotalQuantityEditText = view.findViewById(R.id.total_quantity);
        totalPriceEditText = view.findViewById(R.id.total_price);
        descriptionEditText = view.findViewById(R.id.store_name);
        nextButton = view.findViewById(R.id.save_bill_button);
        loadingSpinner = view.findViewById(R.id.loading_spinner);
    }

    private void setupCloseButton() {
        closeButton.setOnClickListener(v -> {
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        });

    }

    private boolean isDatePickerShowing = false;



    private void setupDatePicker() {
        dateOfPurchaseEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Retrieve minDate and maxDate from SharedPreferences
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("ActiveBudget", Context.MODE_PRIVATE);
            String minDateStr = sharedPreferences.getString("start_date", null); // e.g., "2024-01-10"
            String maxDateStr = sharedPreferences.getString("end_date", null);   // e.g., "2024-01-20"

            try {
                // Parse the minDate and maxDate strings into Calendar objects
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();

                if (minDateStr != null) {
                    minDate.setTime(dateFormat.parse(minDateStr));
                }
                if (maxDateStr != null) {
                    maxDate.setTime(dateFormat.parse(maxDateStr));
                }

                // Create the DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String formattedDate = formatSelectedDate(selectedDay, selectedMonth, selectedYear);
                            dateOfPurchaseEditText.setText(formattedDate);
                        },
                        year, month, day);

                // Set the allowed date range
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMinDate(minDate.getTimeInMillis());
                datePicker.setMaxDate(maxDate.getTimeInMillis());

                datePickerDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Invalid date range.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String formatSelectedDate(int day, int month, int year) {
        // Create a Calendar instance and set the date
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month , day);

        // Format the date using SimpleDateFormat
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC-5"));

        // Return the formatted date string
        return isoFormat.format(calendar.getTime());
    }

    // Method to hide the keyboard
    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void setupNextButton() {
        nextButton.setOnClickListener(v -> {
            hideKeyboard(v);
            RequestQueue queue = Volley.newRequestQueue(requireContext());
            if (areInputsValid()) {
                showLoadingSpinner(true);
                float totalQuantity = Float.parseFloat(storeTotalQuantityEditText.getText().toString().trim());
                float billAmount = Float.parseFloat(totalPriceEditText.getText().toString().trim());



                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", MODE_PRIVATE);
                String budget_id = sharedPreferences.getString("budget_id", null);

                // Collect input data in a HashMap
                Map<String, Object> expenseDetails = new HashMap<>();
                expenseDetails.put("bill_name", billNameEditText.getText().toString().trim());
                expenseDetails.put("date_of_purchase", dateOfPurchaseEditText.getText().toString().trim());
                expenseDetails.put("total_quantity", totalQuantity);
                expenseDetails.put("bill_amount", billAmount);
                expenseDetails.put("description", descriptionEditText.getText().toString().trim());
                expenseDetails.put("budget_id", budget_id);
                List<Map<String, Object>> groceryItems = new ArrayList<>();
                expenseDetails.put("grocery_items", groceryItems);

                // Convert HashMap to JSON
                JSONObject jsonObject = new JSONObject(expenseDetails);
                addExpenseServices.postExpenseDetails(requireContext(), queue, jsonObject, new addExpenseServices.ExpenseResponseListener() {
                    @Override
                    public void onExpenseAdded(String expenseId) {
                        final_expensId = expenseId;
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                new Handler().postDelayed(() -> {
                    showLoadingSpinner(false);
                    navigateToExpenseListFragment();
                    dismiss();
                }, 700);
            } else {
                showLoadingSpinner(true);
                new Handler().postDelayed(() -> {
                    showLoadingSpinner(false);
                }, 100);


                Toast.makeText(getContext(), "All fields are required. Please fill them in.", Toast.LENGTH_SHORT).show();
            }
        });
    }





    private boolean areInputsValid() {
        return !isEditTextEmpty(billNameEditText) &&
                !isEditTextEmpty(dateOfPurchaseEditText) &&
                !isEditTextEmpty(storeTotalQuantityEditText) &&
                !isEditTextEmpty(totalPriceEditText) &&
                !isEditTextEmpty(descriptionEditText);
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void navigateToExpenseListFragment() {
        // Ensure expenseId is available
        if (final_expensId == null || final_expensId.isEmpty()) {
            Toast.makeText(requireContext(), "Expense ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("bill_name", billNameEditText.getText().toString().trim());
        bundle.putString("date_of_purchase", dateOfPurchaseEditText.getText().toString().trim());
        bundle.putString("description", descriptionEditText.getText().toString().trim());
        bundle.putString("expense_id", final_expensId);
        bundle.putString("total_quantity", storeTotalQuantityEditText.getText().toString().trim());
        bundle.putString("total_price", totalPriceEditText.getText().toString().trim());

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.nav_expense_list, bundle);
    }

    private void showLoadingSpinner(boolean isLoading) {
        if (loadingSpinner != null) {
            if (isLoading) {
                nextButton.setEnabled(false);
                nextButton.setText("");
                loadingSpinner.setVisibility(View.VISIBLE);
            } else {
                nextButton.setEnabled(true);
                nextButton.setText("Next");
                loadingSpinner.setVisibility(View.GONE);
            }
        }
    }
}

