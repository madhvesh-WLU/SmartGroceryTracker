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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.example.smartgrocerytracker.services.deleteExpenseServices;
import com.example.smartgrocerytracker.services.deleteGroceryServices;
import com.example.smartgrocerytracker.services.fetchExpenseActiveBudgetServices;
import com.example.smartgrocerytracker.services.fetchGroceriesActiveBudgetServices;
import com.example.smartgrocerytracker.services.searchExpensesServices;
import com.example.smartgrocerytracker.services.fetchGroceryListServices;
import com.example.smartgrocerytracker.services.searchGroceryItemsActiveBudgetServices;
import com.example.smartgrocerytracker.ui.expenses.ExpenseViewAdapter;
import com.example.smartgrocerytracker.ui.expenses.GroceryItemAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//filteration
public class SearchFragment extends Fragment implements GroceryItemAdapter.OnGroceryLongClickListener {

    private FragmentSearchBinding binding;
    private GroceryItemAdapter adapter;
    private List<GroceryItemModel> groceryItemList;
    private RequestQueue queue;
    private String budgetId;
    private String searchQuery = "";
    private String selectedCategory = null;
    private boolean isCategoryFilterApplied = false; // Flag to track if category filter is applied

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groceryItemList = new ArrayList<>();
        adapter = new GroceryItemAdapter(requireContext(), groceryItemList, this);
        binding.budgetIdRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.budgetIdRecyclerView.setAdapter(adapter);
        SearchView searchView = binding.searchView;
        searchView.setIconifiedByDefault(false);

        searchView.setQueryHint("Search Grocery Name");

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        budgetId = sharedPreferences.getString("budget_id", null);

        binding.deleteGList.setOnClickListener(v -> deleteSelectedItems());

        queue = Volley.newRequestQueue(requireContext());

        // Initialize Spinner
        Spinner categorySpinner = binding.categorySpinner;
        categorySpinner.setSelection(0);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View spinnerView, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                if (position == 0) {
                    selectedCategory = null;
                } else {
                    selectedCategory = category;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = null;
            }
        });

        // Set up Apply Filters button
        MaterialButton applyFilterButton = binding.applyFilterButton;
        applyFilterButton.setOnClickListener(v -> {
            isCategoryFilterApplied = true; // Set the flag to true when Apply Filters is clicked
            applyFilters();
        });

        // Set up Clear Filters button
        MaterialButton clearFilterButton = binding.clearFilterButton;
        clearFilterButton.setOnClickListener(v -> {
            isCategoryFilterApplied = false; // Reset the flag
            selectedCategory = null;
            binding.categorySpinner.setSelection(0);
            binding.searchView.setQuery("", false);
            binding.searchView.clearFocus();
            fetchGroceriesActiveBudget();
        });

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                filterResults(); // Call filterResults when query is submitted
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                filterResults(); // Call filterResults as the user types
                return true;
            }
        });

        fetchGroceriesActiveBudget();
    }

    private void fetchGroceriesActiveBudget() {
        if (Objects.equals(budgetId, "null")) {
            Toast.makeText(requireContext(), "No Active Budget Found", Toast.LENGTH_SHORT).show();
            Log.e("fetchGroceries", "No Active Budget Found");
            return;
        }
        fetchGroceriesActiveBudgetServices.fetchGroceriesActiveBudget(requireContext(), queue, budgetId, groceryItems -> {
            groceryItemList.clear();
            groceryItemList.addAll(groceryItems);
            adapter.updateData(groceryItemList, "");
        });
    }

    private void applyFilters() {
        filterResults();
    }

    private void filterResults() {
        String query = searchQuery;
        String category = isCategoryFilterApplied ? selectedCategory : null;

        if ((query == null || query.isEmpty()) && (category == null)) {
            fetchGroceriesActiveBudget();
        } else {
            searchGroceryItemsActiveBudgetServices.searchGroceryItemsAll(requireContext(), queue, budgetId, query, category, groceryItems -> {
                groceryItemList.clear();
                groceryItemList.addAll(groceryItems);
                adapter.updateData(groceryItemList, query);
            });
        }
    }

    @Override
    public void onGroceryLongClick() {
        binding.deleteGList.setVisibility(View.VISIBLE);
    }

    private void deleteSelectedItems() {
        List<GroceryItemModel> selectedItems = adapter.getSelectedItems();
        if (selectedItems.isEmpty()) {
            Toast.makeText(requireContext(), "No items selected", Toast.LENGTH_SHORT).show();
            return;
        }

        for (GroceryItemModel item : selectedItems) {
            deleteGroceryItem(item);
        }
        adapter.clearSelection();
        binding.deleteGList.setVisibility(View.GONE);
    }

    private void deleteGroceryItem(GroceryItemModel item) {
        Log.i("itemid",item.getItemId() +"no item id");
        deleteGroceryServices.deleteGroceryItems(requireContext(), queue, item.getItemId(), () -> {
            groceryItemList.remove(item);
            adapter.updateData(new ArrayList<>(groceryItemList), "");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}




//@Override
//    public void onExpenseClick(ExpenseModel expense) {
//        RequestQueue queue = Volley.newRequestQueue(requireContext());
//        fetchGroceryListServices.fetchGroceryList(requireContext(), queue, expense.getExpenseId(), new fetchGroceryListServices.ExpenseListFetchListener() {
//            @Override
//            public void onExpensesFetched(ExpenseModel fetchedExpense) {
//                Bundle bundle = new Bundle();
//                bundle.putString("bill_name", fetchedExpense.getBillName());
//                bundle.putString("date_of_purchase", fetchedExpense.getDateOfPurchase());
//                bundle.putString("total_price", String.valueOf(fetchedExpense.getBillAmount()));
//                bundle.putString("total_quantity", String.valueOf(fetchedExpense.getTotalQuantity()));
//                bundle.putString("budget_id", String.valueOf(fetchedExpense.getBudgetId()));
//                bundle.putString("expense_id", fetchedExpense.getExpenseId());
//                bundle.putString("description", fetchedExpense.getDescription());
//                bundle.putSerializable("grocery_items", (ArrayList<GroceryItemModel>) fetchedExpense.getGroceryItems());
//
//                NavController navController = NavHostFragment.findNavController(SearchFragment.this);
//                navController.navigate(R.id.action_searchFragment_to_expenseListFragment, bundle);
//            }
//        });
//    }