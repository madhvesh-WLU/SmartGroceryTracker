package com.example.smartgrocerytracker.ui.grocerylist;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.utils.LanguageUtil;
import com.example.smartgrocerytracker.utils.SnackbarHelper;

public class ItemInputDialogFragment extends DialogFragment {

    private EditText itemNameEditText;
    private EditText itemCategoryEditText;
    private EditText itemPriceEditText;
    private EditText itemQuantityEditText;
    private Button doneButton;
    private Button cancelButton;
    private OnItemAddedListener listener;

    private ProgressBar progressBar;

    private ImageButton closeButton;

    // Listener interface to handle item addition
    public interface OnItemAddedListener {
        void onItemAdded(String name, String category, double price, int quantity);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Make the dialog width match the parent width
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public ItemInputDialogFragment() {
        // Required empty public constructor
    }

    // Method to set listener from the parent fragment or activity
    public void setOnItemAddedListener(OnItemAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_item, container, false);
        initializeViews(view);
        setupDoneButton();
        setupCancelButton();
        setupCloseButton();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LanguageUtil.setLocale(context);
    }

    // Initializes all view components
    private void initializeViews(View view) {
        progressBar = view.findViewById(R.id.loading_spinner);
        itemNameEditText = view.findViewById(R.id.item_name);
        closeButton=view.findViewById(R.id.close_button);
        itemCategoryEditText = view.findViewById(R.id.item_category);
        itemPriceEditText = view.findViewById(R.id.item_price);
        itemQuantityEditText = view.findViewById(R.id.item_quantity);
        doneButton = view.findViewById(R.id.done_button);
        cancelButton = view.findViewById(R.id.cancel_button);
    }

    private void setupCloseButton()
    {
        closeButton.setOnClickListener(v -> {
            // Clear focus and dismiss the dialog immediately
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        });
    }


    // Configures the Done button to validate input and add the item
    private void setupDoneButton() {
        doneButton.setOnClickListener(v -> {
            // Get text input from EditTexts
            String itemName = itemNameEditText.getText().toString().trim();
            String itemCategory = itemCategoryEditText.getText().toString().trim();
            String itemPriceText = itemPriceEditText.getText().toString().trim();
            String itemQuantityText = itemQuantityEditText.getText().toString().trim();

            // Check if fields are empty
            if (itemName.isEmpty() || itemCategory.isEmpty() || itemPriceText.isEmpty() || itemQuantityText.isEmpty()) {
                SnackbarHelper.showSnackbar(getView(), "Please fill in all fields");
                //Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Show progress bar and disable button
                    progressBar.setVisibility(View.VISIBLE);
                    doneButton.setEnabled(false);

                    // Parse price and quantity
                    double itemPrice = Double.parseDouble(itemPriceText);
                    int itemQuantity = Integer.parseInt(itemQuantityText);

                    // Simulate network operation or task (e.g., adding the item)
                    new Handler().postDelayed(() -> {
                        if (listener != null) {
                            listener.onItemAdded(itemName, itemCategory, itemPrice, itemQuantity);
                        }

                        // Hide progress bar and re-enable the button
                        progressBar.setVisibility(View.GONE);
                        doneButton.setEnabled(true);

                        clearFields();  // Reset fields for another input if needed

                        SnackbarHelper.showSnackbar(getView(), "Item added successfully");
                        //Toast.makeText(getContext(), "Item added successfully", Toast.LENGTH_SHORT).show();
                    }, 2000); // Simulate delay (e.g., network request)

                } catch (NumberFormatException e) {
                    // Hide progress bar and re-enable button on error
                    progressBar.setVisibility(View.GONE);
                    doneButton.setEnabled(true);
                    Toast.makeText(getContext(), "Please enter valid numbers for price and quantity", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    // Configures the Cancel button to dismiss the dialog
    private void setupCancelButton() {
        cancelButton.setOnClickListener(v -> {
            // Clear focus and dismiss the dialog immediately
            if (getDialog() != null) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        // Additional actions or debugging logs if needed
        Toast.makeText(getContext(), "Dialog dismissed", Toast.LENGTH_SHORT).show();
    }


    // Clears input fields after an item is successfully added
    private void clearFields() {
        itemNameEditText.setText("");
        itemCategoryEditText.setText("");
        itemPriceEditText.setText("");
        itemQuantityEditText.setText("");
    }
}
