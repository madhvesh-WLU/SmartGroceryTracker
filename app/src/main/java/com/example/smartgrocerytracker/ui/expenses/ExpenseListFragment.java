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
import com.example.smartgrocerytracker.services.deleteGroceryServices;
import com.example.smartgrocerytracker.services.fetchGroceryListServices;
import com.example.smartgrocerytracker.services.searchGroceryItemsServices;
import com.example.smartgrocerytracker.services.updateExpenseServices;
import com.example.smartgrocerytracker.ui.grocerylist.ItemInputDialogFragment;
import com.example.smartgrocerytracker.utils.ToastUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseListFragment extends Fragment implements searchGroceryItemsServices.GroceryItemsFetchListener,GroceryItemAdapter.OnGroceryLongClickListener {

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
    private String bill_budget_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExpenseListBinding.inflate(inflater, container, false);
        updateLayoutBasedOnItems();
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
            bill_budget_id = getArguments().getString("budget_id");
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

    /**
     * Displays the expense bill information.
     */
    private void displayBillInfo() {
        binding.editBillName.setText(billName);
        binding.editDateOfPurchase.setText(dateOfPurchase);
        binding.editDescription.setText(description);
        binding.editTotalQuantity.setText(totalQuantity);
        binding.editTotalPrice.setText(totalPrice);
    }

    /**
     * Sets up the RecyclerView with the GroceryItemAdapter.
     */
    private void setupRecyclerViewForGroceryItemView() {
        List<GroceryItemModel> combinedItems = new ArrayList<>(groceryItemsList);
        combinedItems.addAll(addedItemsList);

        groceryItemAdapter = new GroceryItemAdapter(requireContext(), combinedItems,this);
        binding.expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.expenseRecyclerView.setAdapter(groceryItemAdapter);
    }

    /**
     * Handles click events for adding, submitting, deleting, and editing grocery items.
     */
    private void setupButtonListeners() {
        binding.addItemButton.setOnClickListener(v -> showItemInputDialog());
        binding.addItemButton1.setOnClickListener(v -> showItemInputDialog());
        binding.submitButton.setOnClickListener(v -> onSubmit());
        binding.deleteItemButton.setOnClickListener(v -> deleteSelectedItems());
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
//            groceryItemAdapter.updateData(combinedItems);
            updateLayoutBasedOnItems();
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
        updateLayoutBasedOnItems();

    }

    /**
     * Deletes selected grocery items.
     */
    private void deleteSelectedItem() {
//        groceryItemAdapter.deleteSelectedItem(expense_id);
        updateLayoutBasedOnItems();
    }

    /**
     * Enables editing of billing information.
     */
    private void editSelectedItem() {
        binding.editBillName.setEnabled(true);
        binding.editDateOfPurchase.setEnabled(true);
        binding.editDescription.setEnabled(true);
        binding.editTotalQuantity.setEnabled(true);
        binding.editTotalPrice.setEnabled(true);
        binding.editBillingInfoButton.setVisibility(View.GONE);
        binding.cancelButton.setVisibility(View.VISIBLE);
        binding.updateButton.setVisibility(View.VISIBLE);
    }

    private void cancelEdit() {
        binding.editBillName.setEnabled(false);
        binding.editTotalPrice.setEnabled(false);
        binding.editDateOfPurchase.setEnabled(false);
        binding.editDescription.setEnabled(false);
        binding.editTotalQuantity.setEnabled(false);
        binding.editTotalPrice.setEnabled(false);

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
        expenseDetails.put("budget_id", bill_budget_id == null ? "" : bill_budget_id);
        JSONObject jsonObject = new JSONObject(expenseDetails);

        // Call update API to save information
        callUpdateApi(jsonObject);

        // Update TextViews with new values
        binding.editBillName.setText(updatedBillName);
        binding.editDateOfPurchase.setText(updatedDateOfPurchase);
        binding.editDescription.setText(updatedDescription);
        binding.editTotalQuantity.setText(updatedTotalQuantity);
        binding.editTotalPrice.setText(updatedTotalPrice);

        binding.editBillName.setEnabled(false);
        binding.editTotalPrice.setEnabled(false);
        binding.editDateOfPurchase.setEnabled(false);
        binding.editDescription.setEnabled(false);
        binding.editTotalQuantity.setEnabled(false);
        // Hide Cancel and Update buttons, show Edit button
        binding.editBillingInfoButton.setVisibility(View.VISIBLE);
        binding.cancelButton.setVisibility(View.GONE);
        binding.updateButton.setVisibility(View.GONE);
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
        } else {
            groceryItemsList.clear();
            groceryItemAdapter.updateData(groceryItemsList, binding.grocerySearchView.getQuery().toString());
        }
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

    @Override
    public void onGroceryLongClick() {
        binding.deleteItemButton.setVisibility(View.VISIBLE);
    }

    private void deleteSelectedItems() {
        List<GroceryItemModel> selectedItems = groceryItemAdapter.getSelectedItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        for (GroceryItemModel item : selectedItems) {
            deleteGroceryItem(item);
        }
        groceryItemAdapter.clearSelection();
        binding.deleteItemButton.setVisibility(View.GONE);
    }

    private void deleteGroceryItem(GroceryItemModel item) {
        Log.i("DeleteGrocery", "Deleting item: " + item.getItemId());
        deleteGroceryServices.deleteGroceryItems(requireContext(), queue, item.getItemId(), () -> {
            // Remove item from list
            groceryItemsList.remove(item);

            // Update total quantity and price
            int currentTotalQuantity;
            try {
                currentTotalQuantity = (int) Double.parseDouble(totalQuantity); // Safely parse as double, then cast to int
            } catch (NumberFormatException e) {
                Log.e("DeleteGrocery", "Failed to parse totalQuantity: " + totalQuantity, e);
                currentTotalQuantity = 0; // Default to 0 on error
            }

            double currentTotalPrice;
            try {
                currentTotalPrice = Double.parseDouble(totalPrice); // Parse totalPrice as double
            } catch (NumberFormatException e) {
                Log.e("DeleteGrocery", "Failed to parse totalPrice: " + totalPrice, e);
                currentTotalPrice = 0.0; // Default to 0.0 on error
            }

            // Deduct the item's quantity and price
            currentTotalQuantity -= item.getQuantity();
            currentTotalPrice -= (item.getPrice() * item.getQuantity());

            // Ensure no negative values
            currentTotalQuantity = Math.max(currentTotalQuantity, 0);
            currentTotalPrice = Math.max(currentTotalPrice, 0.0);

            // Format total price to 2 decimal places
            totalQuantity = String.valueOf(currentTotalQuantity);
            totalPrice = String.format("%.2f", currentTotalPrice);

            // Update the fields with new values
            groceryItemAdapter.updateData(new ArrayList<>(groceryItemsList), "");
            updateLayoutBasedOnItems();

            // Update displayed bill information
            displayBillInfo();
        });
    }

}






