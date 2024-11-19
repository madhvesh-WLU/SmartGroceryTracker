package com.example.smartgrocerytracker.ui.grocerylist;

import android.os.Bundle;
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
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentExpenseListBinding;
import java.util.ArrayList;
import java.util.List;

public class ExpenseListFragment extends Fragment {

    private FragmentExpenseListBinding binding;
    private GroceryExpenseAdapter adapter;
    private final List<ExpenseModel> expenseList = new ArrayList<>();

    private String billName;
    private String dateOfPurchase;
    private String storeQuantity;
    private String totalPrice;
    private String storeName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExpenseListBinding.inflate(inflater, container, false);
        retrieveBillInfoFromArguments();
        displayBillInfo();
        setupRecyclerView();
        setupButtonListeners();
        showItemInputDialog();
        return binding.getRoot();
    }

    private void retrieveBillInfoFromArguments() {
        Bundle args = getArguments();
        if (args != null) {
            billName = args.getString("bill_name");
            dateOfPurchase = args.getString("date_of_purchase");
            storeQuantity = args.getString("store_quantity");
            totalPrice = args.getString("total_price");
            storeName = args.getString("store_name");
        }
    }

    private void displayBillInfo() {
        binding.billNameTextView.setText(billName);
        binding.dateOfPurchaseTextView.setText("Date of Purchase: " + dateOfPurchase);
        binding.storeNameTextView.setText("Store Name: " + storeName);
        binding.totalPriceTextView.setText("Total Price: $" + totalPrice);
    }

    private void setupRecyclerView() {
        adapter = new GroceryExpenseAdapter(expenseList, this::onExpenseClick);
        binding.expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.expenseRecyclerView.setAdapter(adapter);
    }

    private void onExpenseClick(ExpenseModel expense) {
        Toast.makeText(getContext(), "Clicked: " + expense.getItemName(), Toast.LENGTH_SHORT).show();
    }

    private void setupButtonListeners() {
        binding.addItemButton.setOnClickListener(v -> showItemInputDialog());
        binding.submitButton.setOnClickListener(v -> onSubmit());
        binding.deleteItemButton.setOnClickListener(v -> deleteSelectedItem());
    }

    private void showItemInputDialog() {
        ItemInputDialogFragment itemInputDialog = new ItemInputDialogFragment();
        itemInputDialog.setOnItemAddedListener((name, category, price, quantity) -> {
            ExpenseModel item = new ExpenseModel(null, null, price * quantity, null, null, null, null, null, null, name, category, quantity, price);
            expenseList.add(item);
            adapter.notifyItemInserted(expenseList.size() - 1);
        });
        itemInputDialog.show(getParentFragmentManager(), "ItemInputDialog");
    }

    private void onSubmit() {
        if (expenseList.isEmpty()) {
            Toast.makeText(getContext(), "Please add items before submitting.", Toast.LENGTH_SHORT).show();
            return;
        }
        openGroceryListFragment();
    }

    private void deleteSelectedItem() {
        adapter.deleteSelectedItem(); // Remove selected item from adapter
        Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
    }

    private void openGroceryListFragment() {
        GroceryListFragment fragment = new GroceryListFragment();
        Bundle args = new Bundle();
        args.putString("bill_name", billName);
        args.putString("date_of_purchase", dateOfPurchase);
        args.putString("store_quantity", storeQuantity);
        args.putString("total_price", totalPrice);
        args.putString("store_name", storeName);
        fragment.setArguments(args);

        // Get NavController and navigate using the defined action
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.nav_grocerylist, args);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
