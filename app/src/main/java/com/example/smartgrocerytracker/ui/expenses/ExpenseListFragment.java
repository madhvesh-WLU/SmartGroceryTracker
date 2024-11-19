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
import com.example.smartgrocerytracker.ui.grocerylist.ItemInputDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListFragment extends Fragment {

    private FragmentExpenseListBinding binding;
    private GroceryItemAdapter groceryItemAdapter;
    private final List<GroceryItemModel> groceryItemsList = new ArrayList<>();
    private final List<GroceryItemModel> addedItemsList = new ArrayList<>();

    private String billName;
    private String dateOfPurchase;
    private String storeQuantity;
    private String totalPrice;
    private String storeName;
    private String expense_id;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExpenseListBinding.inflate(inflater, container, false);
        retrieveBillInfoFromArguments();
        setupRecyclerViewForGroceryItemView();
        setupButtonListeners();
//        showItemInputDialog();
        return binding.getRoot();
    }

    private void retrieveBillInfoFromArguments() {
        if (getArguments() != null) {
            billName = getArguments().getString("bill_name");
            dateOfPurchase = getArguments().getString("date_of_purchase");
            storeQuantity = getArguments().getString("store_quantity");
            totalPrice = getArguments().getString("total_price");
            storeName = getArguments().getString("store_name");
            expense_id = getArguments().getString("expense_id");

            displayBillInfo();


            List<GroceryItemModel> groceryItems = (List<GroceryItemModel>) getArguments().getSerializable("grocery_items");
            if (groceryItems != null) {
                groceryItemsList.addAll(groceryItems);
            }
        }
    }


    private void displayBillInfo() {
        binding.billNameTextView.setText(billName);
        binding.dateOfPurchaseTextView.setText("Date of Purchase: " + dateOfPurchase);
        binding.storeNameTextView.setText("Store Name: " + storeName);
        binding.totalPriceTextView.setText("Total Price: $" + totalPrice);
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
        binding.submitButton.setOnClickListener(v -> onSubmit());
        binding.deleteItemButton.setOnClickListener(v -> deleteSelectedItem());
    }

    private void showItemInputDialog() {
        ItemInputDialogFragment itemInputDialog = new ItemInputDialogFragment();
        itemInputDialog.setOnItemAddedListener((name, category, price, quantity) -> {
            GroceryItemModel newItem = new GroceryItemModel(name, null, quantity, category, price, true);
            addedItemsList.add(newItem);
            List<GroceryItemModel> combinedItems = new ArrayList<>(groceryItemsList);
            combinedItems.addAll(addedItemsList);
            groceryItemAdapter.updateData(combinedItems);
        });
        itemInputDialog.show(getParentFragmentManager(), "ItemInputDialog");
    }

    private void onSubmit() {
        if (addedItemsList.isEmpty()) {
            Toast.makeText(getContext(), "No new items added. Nothing to submit.", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONArray jsonArray = new JSONArray();
        try {
            for (GroceryItemModel item : addedItemsList) {
                JSONObject itemObject = new JSONObject();
                itemObject.put("item_name", item.getItemName());  // Use item name
                itemObject.put("quantity", item.getQuantity());  // Quantity
                itemObject.put("category", item.getCategory());  // Category
                itemObject.put("price", item.getPrice());  // Price
                itemObject.put("purchased", true);  // Purchased (true or false)
                jsonArray.put(itemObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        addGroceryServices.postGroceryDetailsByExpenseID(requireContext(), queue, jsonArray,expense_id);
        Toast.makeText(getContext(), "Making API call", Toast.LENGTH_SHORT).show();
    }
    private void deleteSelectedItem() {
        groceryItemAdapter.deleteSelectedItem();
        Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}






