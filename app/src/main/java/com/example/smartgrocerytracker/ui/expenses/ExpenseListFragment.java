package com.example.smartgrocerytracker.ui.expenses;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentExpenseListBinding;
import com.example.smartgrocerytracker.services.addGroceryServices;
import com.example.smartgrocerytracker.services.fetchGroceryListServices;
import com.example.smartgrocerytracker.services.searchGroceryItemsServices;
import com.example.smartgrocerytracker.services.updateExpenseServices;
import com.example.smartgrocerytracker.ui.grocerylist.ItemInputDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseListFragment extends Fragment implements searchGroceryItemsServices.GroceryItemsFetchListener {

    private FragmentExpenseListBinding binding;
    private GroceryItemAdapter groceryItemAdapter;
    private final List<GroceryItemModel> groceryItemsList = new ArrayList<>();
    private final List<GroceryItemModel> addedItemsList = new ArrayList<>();
    private RequestQueue queue;

    private String billName;
    private String dateOfPurchase;
    private String totalQuantity;
    private String totalPrice;
    private String description;
    private String expense_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExpenseListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the RequestQueue
        queue = Volley.newRequestQueue(requireContext());

        retrieveBillInfoFromArguments();
        setupRecyclerViewForGroceryItemView();
        setupButtonListeners();

        // Set up search functionality
        binding.grocerySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterResults(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterResults(newText);
                return true;
            }
        });
    }

    /**
     * Filters the grocery items based on the search query.
     *
     * @param query The search query entered by the user.
     */
    private void filterResults(String query) {
        if (query.isEmpty()) {
            // Reset to original grocery items if the query is empty
            fetchGroceryListServices.fetchGroceryList(requireContext(), queue, expense_id, fetchedExpense -> {
                groceryItemsList.clear();
                groceryItemsList.addAll(fetchedExpense.getGroceryItems());
                groceryItemAdapter.updateData(groceryItemsList, "");
            });
            return;
        }

        // Call the searchGroceryItems API to fetch filtered grocery items
        searchGroceryItemsServices.searchGroceryItems(requireContext(), queue, query, expense_id, this);
    }

    /**
     * Retrieves the expense details passed via arguments.
     */
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
        }
    }

    /**
     * Displays the expense bill information.
     */
    private void displayBillInfo() {
        binding.billNameTextView.setText(billName);
        binding.dateOfPurchaseTextView.setText(dateOfPurchase);
        binding.descriptionTextView.setText(description);
        binding.totalQuantityTextView.setText(totalQuantity);
        binding.totalPriceTextView.setText(totalPrice);
    }

    /**
     * Sets up the RecyclerView with the GroceryItemAdapter.
     */
    private void setupRecyclerViewForGroceryItemView() {
        List<GroceryItemModel> combinedItems = new ArrayList<>(groceryItemsList);
        combinedItems.addAll(addedItemsList);

        groceryItemAdapter = new GroceryItemAdapter(requireContext(), combinedItems);
        binding.expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.expenseRecyclerView.setAdapter(groceryItemAdapter);
    }

    /**
     * Handles click events for adding, submitting, deleting, and editing grocery items.
     */
    private void setupButtonListeners() {
        binding.addItemButton.setOnClickListener(v -> showItemInputDialog());
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

    /**
     * Shows the dialog to input a new grocery item.
     */
    private void showItemInputDialog() {
        ItemInputDialogFragment itemInputDialog = new ItemInputDialogFragment();
        itemInputDialog.setOnItemAddedListener((name, category, price, quantity) -> {
            GroceryItemModel newItem = new GroceryItemModel(null, name, null, quantity, category, price, true);
            addedItemsList.add(newItem);
            List<GroceryItemModel> combinedItems = new ArrayList<>(groceryItemsList);
            combinedItems.addAll(addedItemsList);
            groceryItemAdapter.updateData(combinedItems, binding.grocerySearchView.getQuery().toString());
        });
        itemInputDialog.show(getParentFragmentManager(), "ItemInputDialog");
    }

    /**
     * Submits the added grocery items to the backend.
     */
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

        if (expense_id == null) {
            Toast.makeText(getContext(), "Expense ID is null", Toast.LENGTH_SHORT).show();
            return;
        } else {
            addGroceryServices.postGroceryDetailsByExpenseID(requireContext(), queue, jsonArray, expense_id, new Runnable() {
                @Override
                public void run() {
                    // Navigate back to the previous fragment
                    NavController navController = NavHostFragment.findNavController(ExpenseListFragment.this);
                    navController.popBackStack();
                }
            });
        }
    }

    /**
     * Deletes selected grocery items.
     */
    private void deleteSelectedItem() {
        groceryItemAdapter.deleteSelectedItem(expense_id);
    }

    /**
     * Enables editing of billing information.
     */
    private void editSelectedItem() {
        // Toggle enabling and disabling EditText fields
        binding.billNameTextView.setEnabled(true);
        binding.dateOfPurchaseTextView.setEnabled(true);
        binding.descriptionTextView.setEnabled(true);
        binding.totalQuantityTextView.setEnabled(true);
        binding.totalPriceTextView.setEnabled(true);

        // Show Cancel and Update buttons, hide Edit Information button
        binding.editBillingInfoButton.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.VISIBLE);
        binding.updateButton.setVisibility(View.VISIBLE);
    }

    /**
     * Cancels the editing of billing information.
     */
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

    /**
     * Updates the billing information by sending data to the backend.
     */
    private void updateInformation() throws JSONException {
        // Save the updated information
        billName = binding.billNameTextView.getText().toString();
        dateOfPurchase = binding.dateOfPurchaseTextView.getText().toString();
        description = binding.descriptionTextView.getText().toString();
        totalQuantity = binding.totalQuantityTextView.getText().toString();
        totalPrice = binding.totalPriceTextView.getText().toString();

        float quantity = Float.parseFloat(totalQuantity);
        float billAmount = Float.parseFloat(totalPrice);
        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("bill_name", billName);
        expenseDetails.put("date_of_purchase", dateOfPurchase);
        expenseDetails.put("total_quantity", quantity);
        expenseDetails.put("bill_amount", billAmount);
        expenseDetails.put("description", description);
        expenseDetails.put("budget_id", "4534bb7c-523a-4bac-85a0-5f1dad3e2e8c"); // Example budget_id
        JSONObject jsonObject = new JSONObject(expenseDetails);
        callUpdateApi(jsonObject);

        // Hide Cancel and Update buttons, show Edit Information button
        binding.editBillingInfoButton.setVisibility(View.VISIBLE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.updateButton.setVisibility(View.GONE);

        // Disable fields after update
        binding.billNameTextView.setEnabled(false);
        binding.dateOfPurchaseTextView.setEnabled(false);
        binding.descriptionTextView.setEnabled(false);
        binding.totalPriceTextView.setEnabled(false);
    }

    /**
     * Calls the update API to update billing information.
     *
     * @param updatedBillInfo The JSON object containing updated billing information.
     */
    private void callUpdateApi(JSONObject updatedBillInfo) {
        updateExpenseServices.putExpenseDetails(requireContext(), queue, updatedBillInfo, expense_id);
    }

    /**
     * Callback method when grocery items are fetched from the search API.
     *
     * @param groceryItems The list of fetched grocery items.
     */
    @Override
    public void onGroceryFetched(List<GroceryItemModel> groceryItems) {
        if (groceryItems != null && !groceryItems.isEmpty()) {
            groceryItemsList.clear();
            groceryItemsList.addAll(groceryItems);
            groceryItemAdapter.updateData(groceryItemsList, binding.grocerySearchView.getQuery().toString());
            Toast.makeText(getContext(), "Grocery items retrieved: " + groceryItems.size(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No matching grocery items found.", Toast.LENGTH_SHORT).show();
            groceryItemsList.clear();
            groceryItemAdapter.updateData(groceryItemsList, binding.grocerySearchView.getQuery().toString());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}