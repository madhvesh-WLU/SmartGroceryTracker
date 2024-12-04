package com.example.smartgrocerytracker.ui.expenses;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentExpenseBinding;

import com.example.smartgrocerytracker.services.fetchExpensesServices;
import com.example.smartgrocerytracker.services.fetchGroceryListServices;
import com.example.smartgrocerytracker.ui.grocerylist.FancyGroceryOptionsDialog;
import com.example.smartgrocerytracker.utils.LanguageUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;


public class ExpenseFragment extends Fragment implements ExpenseViewAdapter.OnExpenseClickListener {

    private FragmentExpenseBinding binding;
    private ExpenseViewAdapter adapter;
    private List<ExpenseModel> expenseList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Use the binding to inflate the layout
        binding = FragmentExpenseBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LanguageUtil.setLocale(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            // Set the title for Store Locator
            toolbar.setTitle("Expense List");
        }
        expenseList = new ArrayList<>();
        adapter = new ExpenseViewAdapter(expenseList, this, requireContext());

        // Set up the RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        setupOptionButton();

        // Initialize the RequestQueue and fetch expenses
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchExpensesServices.fetchExpenses(requireContext(), queue, expenses -> {
            expenseList.clear();
            expenseList.addAll(expenses);
            adapter.notifyDataSetChanged();

            // Update the layout visibility based on the fetched data
            updateLayoutBasedOnItems();
        });
    }

    @Override
    public void onExpenseClick(ExpenseModel expense) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchGroceryListServices.fetchGroceryList(requireContext(), queue, expense.getExpenseId(), new fetchGroceryListServices.ExpenseListFetchListener() {
            @Override
            public void onExpensesFetched(ExpenseModel expense) {
                Bundle bundle = new Bundle();
                bundle.putString("bill_name", "Bill");
                bundle.putString("date_of_purchase", expense.getDateOfPurchase());
                bundle.putString("total_price", String.valueOf(expense.getBillAmount()));
                bundle.putString("total_quantity", String.valueOf(expense.getTotalQuantity()));
                bundle.putString("expense_id", expense.getExpenseId());
                bundle.putString("description", expense.getDescription());
                bundle.putSerializable("grocery_items", (ArrayList<GroceryItemModel>) expense.getGroceryItems());

                NavController navController = NavHostFragment.findNavController(requireParentFragment());
                navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
            }
        });
    }

    private void setupOptionButton() {
        binding.addGList.setOnClickListener(v -> {
            FancyGroceryOptionsDialog optionsDialog = new FancyGroceryOptionsDialog();
            optionsDialog.show(getParentFragmentManager(), "FancyGroceryOptionsDialog");
        });
    }

    /**
     * Updates the layout based on the current number of items.
     */
    private void updateLayoutBasedOnItems() {
        if (expenseList.isEmpty()) {
            Log.e("Expense","Hello");
            binding.addBillIconImageView.setVisibility(View.VISIBLE);
            binding.addBillMessageTextView.setVisibility(View.VISIBLE);
        } else {
            binding.addBillIconImageView.setVisibility(View.GONE);
            binding.addBillMessageTextView.setVisibility(View.GONE);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
