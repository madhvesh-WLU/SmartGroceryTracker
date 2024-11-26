package com.example.smartgrocerytracker.ui.expenses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentExpenseListBinding;
import com.example.smartgrocerytracker.services.addGroceryServices;
import com.example.smartgrocerytracker.services.updateExpenseServices;
import com.example.smartgrocerytracker.ui.grocerylist.ItemInputDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseListFragment extends Fragment {

    private FragmentExpenseListBinding binding;
    private GroceryItemAdapter groceryItemAdapter;
    private final List<GroceryItemModel> groceryItemsList = new ArrayList<>();
    private final List<GroceryItemModel> addedItemsList = new ArrayList<>();

    private String billName;
    private String dateOfPurchase;
    private String totalQuantity;
    private String totalPrice;
    private String description;
    private String expense_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExpenseListBinding.inflate(inflater, container, false);
        retrieveBillInfoFromArguments();
        setupRecyclerViewForGroceryItemView();
        setupButtonListeners();
//        showItemInputDialog();
        updateLayoutBasedOnItems();
        return binding.getRoot();
    }

    private void retrieveBillInfoFromArguments() {
        if (getArguments() != null) {
            billName = getArguments().getString("bill_name");
            dateOfPurchase = getArguments().getString("date_of_purchase");
            description = getArguments().getString("description");

            totalQuantity = getArguments().getString("total_quantity");
            totalPrice = getArguments().getString("total_price");
            expense_id = getArguments().getString("expense_id");

            displayBillInfo();


            List<GroceryItemModel> groceryItems = (List<GroceryItemModel>) getArguments().getSerializable("grocery_items");
            if (groceryItems != null) {
                groceryItemsList.addAll(groceryItems);
            }
            updateLayoutBasedOnItems();
        }
    }


    private void displayBillInfo() {
        binding.billNameTextView.setText(billName);
        binding.dateOfPurchaseTextView.setText(dateOfPurchase);
        binding.descriptionTextView.setText(description);
        binding.totalQuantityTextView.setText(totalQuantity);
        binding.totalPriceTextView.setText(totalPrice);
    }

    private void setupRecyclerViewForGroceryItemView() {
        List<GroceryItemModel> combinedItems = new ArrayList<>(groceryItemsList);
        combinedItems.addAll(addedItemsList);

        groceryItemAdapter = new GroceryItemAdapter(requireContext(), combinedItems);
        binding.expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.expenseRecyclerView.setAdapter(groceryItemAdapter);
    }

    private void onExpenseClick(ExpenseModel expense) {
        Toast.makeText(getContext(), "Clicked: " + expense.getBillName(), Toast.LENGTH_SHORT).show();
    }

    private void setupButtonListeners() {
        binding.addItemButton.setOnClickListener(v -> showItemInputDialog());
        binding.addItemButton1.setOnClickListener(v -> showItemInputDialog());
        binding.submitButton.setOnClickListener(v -> onSubmit());
        binding.deleteItemButton.setOnClickListener(v -> deleteSelectedItem());
        binding.editBillingInfoButton.setOnClickListener(v -> editSelectedItem());
        binding.cancelButton.setOnClickListener(v -> cancelEdit());
        binding.updateButton.setOnClickListener(v -> {
            try {
                updateInformation();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void showItemInputDialog() {
        ItemInputDialogFragment itemInputDialog = new ItemInputDialogFragment();
        itemInputDialog.setOnItemAddedListener((name, category, price, quantity) -> {
            GroceryItemModel newItem = new GroceryItemModel(null,name, null, quantity, category, price, true);
            addedItemsList.add(newItem);
            List<GroceryItemModel> combinedItems = new ArrayList<>(groceryItemsList);
            combinedItems.addAll(addedItemsList);
            groceryItemAdapter.updateData(combinedItems);
            updateLayoutBasedOnItems();
        });
        itemInputDialog.show(getParentFragmentManager(), "ItemInputDialog");
    }



    //Submit Grocery list creation/changes both with API
    private void onSubmit() {
        if (addedItemsList.isEmpty()) {
            Toast.makeText(getContext(), "No new items added. Nothing to submit.", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONArray jsonArray = new JSONArray();
        try {
            for (GroceryItemModel item : addedItemsList) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("item_name", item.getItemName());
                itemObject.put("quantity", item.getQuantity());
                itemObject.put("category", item.getCategory());
                itemObject.put("price", item.getPrice());
                itemObject.put("purchased", true);
                jsonArray.put(itemObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        if(expense_id == null){
            Toast.makeText(getContext(), expense_id, Toast.LENGTH_SHORT).show();

        }
        else{
            addGroceryServices.postGroceryDetailsByExpenseID(requireContext(), queue, jsonArray, expense_id, new Runnable() {
                @Override
                public void run() {
                    // Navigate back to the previous fragment
                    NavController navController = NavHostFragment.findNavController(ExpenseListFragment.this);
                    navController.popBackStack();
                }
            });
        }
        updateLayoutBasedOnItems();

    }




    //Nov 22nd Delete Grocery Items based on Selected fields with API
    private void deleteSelectedItem() {
        groceryItemAdapter.deleteSelectedItem(expense_id);
        updateLayoutBasedOnItems();
    }



    //Nov 22nd Modfication functionality for Bill Information with API
    private void editSelectedItem() {
        // Hide TextViews and Show EditTexts with current values
        binding.billNameTextView.setVisibility(View.GONE);
        binding.editBillName.setVisibility(View.VISIBLE);
        binding.editBillName.setText(binding.billNameTextView.getText().toString());

        binding.dateOfPurchaseTextView.setVisibility(View.GONE);
        binding.editDateOfPurchase.setVisibility(View.VISIBLE);
        binding.editDateOfPurchase.setText(binding.dateOfPurchaseTextView.getText().toString());

        binding.descriptionTextView.setVisibility(View.GONE);
        binding.editDescription.setVisibility(View.VISIBLE);
        binding.editDescription.setText(binding.descriptionTextView.getText().toString());

        binding.totalQuantityTextView.setVisibility(View.GONE);
        binding.editTotalQuantity.setVisibility(View.VISIBLE);
        binding.editTotalQuantity.setText(binding.totalQuantityTextView.getText().toString());

        binding.totalPriceTextView.setVisibility(View.GONE);
        binding.editTotalPrice.setVisibility(View.VISIBLE);
        binding.editTotalPrice.setText(binding.totalPriceTextView.getText().toString());

        // Show Cancel and Update buttons, hide Edit Information button
        binding.editBillingInfoButton.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.VISIBLE);
        binding.updateButton.setVisibility(View.VISIBLE);
    }

    private void cancelEdit() {
        // Revert changes and disable fields
        binding.billNameTextView.setText(billName);
        binding.dateOfPurchaseTextView.setText(dateOfPurchase);
        binding.descriptionTextView.setText(description);
        binding.totalQuantityTextView.setText(totalQuantity);
        binding.totalPriceTextView.setText(totalPrice);

        binding.billNameTextView.setEnabled(false);
        binding.totalPriceTextView.setEnabled(false);
        binding.dateOfPurchaseTextView.setEnabled(false);
        binding.descriptionTextView.setEnabled(false);
        binding.totalQuantityTextView.setEnabled(false);
        binding.totalPriceTextView.setEnabled(false);

        // Hide Cancel and Update buttons, show Edit Information button
        binding.editBillingInfoButton.setVisibility(View.VISIBLE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.updateButton.setVisibility(View.GONE);
    }
    private void updateInformation() throws JSONException {
        // Save the updated information
        String updatedBillName = binding.editBillName.getText().toString();
        String updatedDateOfPurchase = binding.editDateOfPurchase.getText().toString();
        String updatedDescription = binding.editDescription.getText().toString();
        String updatedTotalQuantity = binding.editTotalQuantity.getText().toString();
        String updatedTotalPrice = binding.editTotalPrice.getText().toString();

        float quantity = Float.parseFloat(updatedTotalQuantity);
        float billAmount = Float.parseFloat(updatedTotalPrice);

        // Create a map with updated details
        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("bill_name", updatedBillName);
        expenseDetails.put("date_of_purchase", updatedDateOfPurchase);
        expenseDetails.put("total_quantity", quantity);
        expenseDetails.put("bill_amount", billAmount);
        expenseDetails.put("description", updatedDescription);
        expenseDetails.put("budget_id", "4534bb7c-523a-4bac-85a0-5f1dad3e2e8c");
        JSONObject jsonObject = new JSONObject(expenseDetails);

        // Call update API to save information
        callUpdateApi(jsonObject);

        // Update TextViews with new values
        binding.billNameTextView.setText(updatedBillName);
        binding.dateOfPurchaseTextView.setText(updatedDateOfPurchase);
        binding.descriptionTextView.setText(updatedDescription);
        binding.totalQuantityTextView.setText(updatedTotalQuantity);
        binding.totalPriceTextView.setText(updatedTotalPrice);

        // Hide Cancel and Update buttons, show Edit button
        binding.editBillingInfoButton.setVisibility(View.VISIBLE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.updateButton.setVisibility(View.GONE);

        // Hide EditText fields and show TextViews
        binding.editBillName.setVisibility(View.GONE);
        binding.editDateOfPurchase.setVisibility(View.GONE);
        binding.editDescription.setVisibility(View.GONE);
        binding.editTotalQuantity.setVisibility(View.GONE);
        binding.editTotalPrice.setVisibility(View.GONE);

        binding.billNameTextView.setVisibility(View.VISIBLE);
        binding.dateOfPurchaseTextView.setVisibility(View.VISIBLE);
        binding.descriptionTextView.setVisibility(View.VISIBLE);
        binding.totalQuantityTextView.setVisibility(View.VISIBLE);
        binding.totalPriceTextView.setVisibility(View.VISIBLE);
    }

    private void callUpdateApi(JSONObject updatedBillInfo) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        updateExpenseServices.putExpenseDetails(requireContext(), queue, updatedBillInfo, expense_id);
    }

    private void updateLayoutBasedOnItems() {
        if (groceryItemsList.isEmpty() && addedItemsList.isEmpty()) {
            binding.addItemButton1.setVisibility(View.VISIBLE);
            binding.addItemMessageTextView.setVisibility(View.VISIBLE);
            binding.controlButtonLayout.setVisibility(View.GONE);
        } else {
            binding.addItemButton1.setVisibility(View.GONE);
            binding.addItemMessageTextView.setVisibility(View.GONE);
            binding.controlButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}






