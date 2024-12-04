//package com.example.smartgrocerytracker.ui.grocerylist;
//
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.SearchView;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.smartgrocerytracker.services.fetchExpenseActiveBudgetServices;
//
//import com.android.volley.RequestQueue;
//import com.android.volley.toolbox.Volley;
//import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
//import com.example.smartgrocerytracker.R;
//import com.example.smartgrocerytracker.databinding.FragmentSearchBinding;
//import com.example.smartgrocerytracker.services.fetchExpensesServices;
//import com.example.smartgrocerytracker.ui.expenses.ExpenseViewAdapter;
//
//import java.util.List;
//
//public class SearchFragment extends Fragment{
//
//    private FragmentSearchBinding binding;
//    private ExpenseViewAdapter adapter;
//    private List<ExpenseModel> expenseList;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentSearchBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Set up RecyclerView
//        binding.budgetIdRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        // Use an adapter to display search results
//
//        // Search functionality
//        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                filterResults(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filterResults(newText);
//                return true;
//            }
//        });
//        // Checkbox filter listener (optional)
//        binding.filterCategory1.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
//        binding.filterCategory2.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
//
//
//
//        RequestQueue queue = Volley.newRequestQueue(requireContext());
//        fetchExpenseActiveBudgetServices.fetchExpensesActiveBudget(requireContext(), queue, expenses -> {
//            expenseList.clear();
//            expenseList.addAll(expenses);
//            adapter.notifyDataSetChanged();
//        });
//    }
//

//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null; // Avoid memory leaks
//    }
//}



        // Set up Checkbox filters (optional)
//        binding.filterCategory1.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
//        binding.filterCategory2.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());



//    private void filterResults(String query) {
//        List<ExpenseModel> filteredList = new ArrayList<>();
//        for (ExpenseModel expense : expenseList) {
//            if (expense.getName().toLowerCase().contains(query.toLowerCase())) {
//                filteredList.add(expense);
//            }
//        }
//        adapter.updateData(filteredList);
//    }

//    private void applyFilters() {
//        // Implement logic to apply additional filters
//        // Example: Filter based on checkbox selections
//        List<ExpenseModel> filteredList = new ArrayList<>();
//        for (ExpenseModel expense : expenseList) {
//            if (binding.filterCategory1.isChecked() && expense.getCategory().equalsIgnoreCase("Category1")) {
//                filteredList.add(expense);
//            } else if (binding.filterCategory2.isChecked() && expense.getCategory().equalsIgnoreCase("Category2")) {
//                filteredList.add(expense);
//            }
//        }
//        adapter.updateData(filteredList);
//    }


package com.example.smartgrocerytracker.ui.grocerylist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentSearchBinding;
import com.example.smartgrocerytracker.services.fetchExpenseActiveBudgetServices;
import com.example.smartgrocerytracker.services.searchExpensesServices;
import com.example.smartgrocerytracker.services.fetchGroceryListServices;
import com.example.smartgrocerytracker.ui.expenses.ExpenseViewAdapter;
import com.example.smartgrocerytracker.utils.LanguageUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements ExpenseViewAdapter.OnExpenseClickListener {

    private FragmentSearchBinding binding;
    private ExpenseViewAdapter adapter;
    private List<ExpenseModel> expenseList;
    RequestQueue queue;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the RecyclerView
        expenseList = new ArrayList<>();
        adapter = new ExpenseViewAdapter(expenseList, this,requireContext()); // Pass this as the listener
        binding.budgetIdRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.budgetIdRecyclerView.setAdapter(adapter);


        // Initialize the RequestQueue
        queue = Volley.newRequestQueue(requireContext());

        // Set up search functionality
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        // Fetch initial data (optional, if you want to display something initially)
        fetchExpenseActiveBudgetServices.fetchExpensesActiveBudget(requireContext(), queue, expenses -> {
            expenseList.clear();
            expenseList.addAll(expenses);
            adapter.notifyDataSetChanged();
        });
    }

    private void filterResults(String query) {
        if (query.isEmpty()) {
            // Optionally reset the list if the query is empty
            fetchExpenseActiveBudgetServices.fetchExpensesActiveBudget(requireContext(), queue, expenses -> {
                expenseList.clear();
                expenseList.addAll(expenses);
                adapter.updateData(expenseList, ""); // Clear query for highlighting
            });
            return;
        }

        searchExpensesServices.fetchExpensesActiveBudget(requireContext(), queue, query, filteredExpenses -> {
            expenseList.clear();
            expenseList.addAll(filteredExpenses);
            adapter.updateData(expenseList, query); // Pass query to adapter for highlighting
        });
    }
    private void applyFilters() {

    }
    @Override
    public void onExpenseClick(ExpenseModel expense) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchGroceryListServices.fetchGroceryList(requireContext(), queue, expense.getExpenseId(), new fetchGroceryListServices.ExpenseListFetchListener() {
            @Override
            public void onExpensesFetched(ExpenseModel fetchedExpense) {
                Bundle bundle = new Bundle();
                bundle.putString("bill_name", fetchedExpense.getBillName());
                bundle.putString("date_of_purchase", fetchedExpense.getDateOfPurchase());
                bundle.putString("total_price", String.valueOf(fetchedExpense.getBillAmount()));
                bundle.putString("total_quantity", String.valueOf(fetchedExpense.getTotalQuantity()));
                bundle.putString("expense_id", fetchedExpense.getExpenseId());
                bundle.putString("description", fetchedExpense.getDescription());
                bundle.putSerializable("grocery_items", (ArrayList<GroceryItemModel>) fetchedExpense.getGroceryItems());

                NavController navController = NavHostFragment.findNavController(SearchFragment.this);
                navController.navigate(R.id.action_searchFragment_to_expenseListFragment, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}