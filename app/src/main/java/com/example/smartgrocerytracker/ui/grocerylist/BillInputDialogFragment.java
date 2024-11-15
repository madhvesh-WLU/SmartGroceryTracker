// BillInputDialogFragment.java
package com.example.smartgrocerytracker.ui.grocerylist;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.smartgrocerytracker.R;

import java.util.Calendar;

public class BillInputDialogFragment extends DialogFragment {

    private EditText billNameEditText;
    private EditText dateOfPurchaseEditText;
    private EditText storeTotalQuantityEditText;
    private EditText totalPriceEditText;
    private EditText storeNameEditText;
    private Button nextButton;

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
        dateOfPurchaseEditText = view.findViewById(R.id.date_of_purchase);
        storeTotalQuantityEditText = view.findViewById(R.id.total_quantity);
        totalPriceEditText = view.findViewById(R.id.total_price);
        storeNameEditText = view.findViewById(R.id.store_name);
        nextButton = view.findViewById(R.id.save_bill_button);
    }

    private void setupDatePicker() {
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
            datePickerDialog.show();
        });
    }

    private String formatSelectedDate(int day, int month, int year) {
        return day + "/" + (month + 1) + "/" + year;
    }

    private void setupNextButton() {
        nextButton.setOnClickListener(v -> {
            if (areInputsValid()) {
                openItemInputDialog();
                navigateToExpenseListFragment();
                dismiss();
            } else {
                Toast.makeText(getContext(), "All fields are required. Please fill them in.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openItemInputDialog() {
        ItemInputDialogFragment itemInputDialog = new ItemInputDialogFragment();

        // Optionally, set a listener to handle item addition
        itemInputDialog.setOnItemAddedListener((name, category, price, quantity) -> {
            // Handle the item added, e.g., add it to a list or database
            // You can handle this action in `BillInputDialogFragment` or pass it further
        });

        // Optionally, pass data as arguments if needed
        Bundle bundle = new Bundle();
        bundle.putString("bill_name", billNameEditText.getText().toString().trim());
        bundle.putString("date_of_purchase", dateOfPurchaseEditText.getText().toString().trim());
        bundle.putString("store_quantity", storeTotalQuantityEditText.getText().toString().trim());
        bundle.putString("total_price", totalPriceEditText.getText().toString().trim());
        bundle.putString("store_name", storeNameEditText.getText().toString().trim());
        itemInputDialog.setArguments(bundle);

        itemInputDialog.show(getParentFragmentManager(), "ItemInputDialog");
    }


    private boolean areInputsValid() {
        return !isEditTextEmpty(billNameEditText) &&
                !isEditTextEmpty(dateOfPurchaseEditText) &&
                !isEditTextEmpty(storeTotalQuantityEditText) &&
                !isEditTextEmpty(totalPriceEditText) &&
                !isEditTextEmpty(storeNameEditText);
    }

    private boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void navigateToExpenseListFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("bill_name", billNameEditText.getText().toString().trim());
        bundle.putString("date_of_purchase", dateOfPurchaseEditText.getText().toString().trim());
        bundle.putString("store_quantity", storeTotalQuantityEditText.getText().toString().trim());
        bundle.putString("total_price", totalPriceEditText.getText().toString().trim());
        bundle.putString("store_name", storeNameEditText.getText().toString().trim());

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.nav_expense_list, bundle);
    }
}
