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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentGrocerylistBinding;
import com.example.smartgrocerytracker.services.fetchExpensesServices;
import java.util.ArrayList;
import java.util.List;

public class GroceryListFragment extends Fragment implements fetchExpensesServices.ExpenseFetchListener {

    private FragmentGrocerylistBinding binding;
    private GroceryExpenseAdapter adapter;
    private final List<ExpenseModel> expenseList = new ArrayList<>();

    // Variables to hold bill information
    private String billName;
    private String dateOfPurchase;
    private String totalPrice;
    private String storeName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGrocerylistBinding.inflate(inflater, container, false);

        retrieveBillInfoFromArguments();
        setupUI();
        setupRecyclerView();
        setupOptionButton();

        binding.billInfoRow.setOnClickListener(v -> {

            binding.billInfoRow.setBackgroundColor(getResources().getColor(R.color.inactive_color));
            onBillRowClick();  // Navigate or perform other actions
        });

        return binding.getRoot();
    }

    private void retrieveBillInfoFromArguments() {
        if (getArguments() != null) {
            billName = getArguments().getString("bill_name", "No Name");
            dateOfPurchase = getArguments().getString("date_of_purchase", "No Date");
            totalPrice = getArguments().getString("total_price", "0.0");
            storeName = getArguments().getString("store_name", "Unknown Store");
        }
    }

    private void setupUI() {
        binding.dateOfPurchaseTextView.setText(dateOfPurchase);
        binding.storeNameTextView.setText(storeName);

        if (totalPrice != null && !totalPrice.trim().isEmpty()) {
            try {
                double priceValue = Double.parseDouble(totalPrice.trim());
                binding.totalPriceTextView.setText(String.format("$%.2f", priceValue));
            } catch (NumberFormatException e) {
                binding.totalPriceTextView.setText("");
            }
        } else {
            binding.totalPriceTextView.setText("");
        }
    }

    private void setupRecyclerView() {
        adapter = new GroceryExpenseAdapter(expenseList, this::onExpenseItemClick);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupOptionButton() {
        binding.addGList.setOnClickListener(v -> {
            FancyGroceryOptionsDialog optionsDialog = new FancyGroceryOptionsDialog();
            optionsDialog.show(getParentFragmentManager(), "FancyGroceryOptionsDialog");
        });
    }

    private void onExpenseItemClick(ExpenseModel expense) {
        Toast.makeText(getContext(), "Selected Item: " + expense.getItemName(), Toast.LENGTH_SHORT).show();
    }

    private void onBillRowClick() {
        Bundle args = new Bundle();
        args.putString("bill_name", billName);
        args.putString("date_of_purchase", dateOfPurchase);
        args.putString("total_price", totalPrice);
        args.putString("store_name", storeName);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.nav_expense_list, args);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchExpenses();
    }

    private void fetchExpenses() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchExpensesServices.fetchExpenses(requireContext(), queue, this);
    }

    @Override
    public void onExpensesFetched(List<ExpenseModel> expenses) {
        if (expenses.isEmpty()) {
            Toast.makeText(requireContext(), "No expenses found", Toast.LENGTH_SHORT).show();
        } else {
            expenseList.clear();
            expenseList.addAll(expenses);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
