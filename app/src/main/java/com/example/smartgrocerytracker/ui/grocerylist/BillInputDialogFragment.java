// BillInputDialogFragment.java
package com.example.smartgrocerytracker.ui.grocerylist;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.example.smartgrocerytracker.utils.BudgetHelper;
import com.example.smartgrocerytracker.utils.CapitalizeLetter;
import com.example.smartgrocerytracker.utils.LanguageUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private String final_expenseId;
    private ImageButton closeButton;
    SharedPreferences sharedPreferences;
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
        setupBillNameAutoCapitalization();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LanguageUtil.setLocale(context);
    }

    private void setupBillNameAutoCapitalization() {
        billNameEditText.addTextChangedListener(new TextWatcher() {
            private String currentText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                // If input has changed, apply capitalization logic
                if (!input.equals(currentText)) {
                    CapitalizeLetter capitalizeLetter = new CapitalizeLetter();
                    currentText= capitalizeLetter.capitalizeFirstLetter(input);

                    // Temporarily remove the TextWatcher to avoid recursion
                    billNameEditText.removeTextChangedListener(this);

                    // Update the EditText content
                    billNameEditText.setText(currentText);

                    // Set the cursor position at the end
                    billNameEditText.setSelection(currentText.length());

                    // Reattach the TextWatcher
                    billNameEditText.addTextChangedListener(this);
                }
            }


        });
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
        // Retrieve start_date and end_date from SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("ActiveBudget", MODE_PRIVATE);
        String startDate = sharedPreferences.getString("start_date", null);
        String endDate = sharedPreferences.getString("end_date", null);

        // Parse the dates from SharedPreferences if available
        Date minDate;
        Date maxDate;

        if (startDate != null) {
            minDate = BudgetHelper.parseDate(startDate);
        } else {
            minDate = null;
        }
        if (endDate != null) {
            maxDate = BudgetHelper.parseDate(endDate);
        } else {
            maxDate = null;
        }

        // Set up the DatePickerDialog
        dateOfPurchaseEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = formatSelectedDate(selectedDay, selectedMonth, selectedYear);
                        dateOfPurchaseEditText.setText(formattedDate);
                    },
                    year, month, day);

            // Apply minDate and maxDate if they are not null
            if (minDate != null) {
                datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
            }
            if (maxDate != null) {
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
            }

            datePickerDialog.show();
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
                        final_expenseId = expenseId;
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
        // Bundle the data with proper labels for display
        Bundle bundle = new Bundle();
        bundle.putString("bill_name", "Bill Name: " + billNameEditText.getText().toString().trim());
        bundle.putString("date_of_purchase", "Date of Purchase: " + dateOfPurchaseEditText.getText().toString().trim());
        bundle.putString("description", "Description: " + descriptionEditText.getText().toString().trim());
        bundle.putString("total_quantity", "Total Quantity: " + storeTotalQuantityEditText.getText().toString().trim());
        bundle.putString("total_price", "Total Price: " + totalPriceEditText.getText().toString().trim());

        // Navigate to the expense list fragment and pass the data
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

