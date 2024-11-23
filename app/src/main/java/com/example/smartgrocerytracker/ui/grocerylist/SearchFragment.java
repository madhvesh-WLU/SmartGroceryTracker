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
//    private void filterResults(String query) {
//        // Implement logic to filter results based on the query
//    }
//
//    private void applyFilters() {
//        // Implement logic to apply additional filters
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null; // Avoid memory leaks
//    }
//}

package com.example.smartgrocerytracker.ui.grocerylist;

import android.os.Bundle;
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
import com.example.smartgrocerytracker.services.fetchGroceryListServices;
import com.example.smartgrocerytracker.ui.expenses.ExpenseViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment{

    private FragmentSearchBinding binding;
    private ExpenseViewAdapter adapter;
    private List<ExpenseModel> expenseList;

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
        adapter = new ExpenseViewAdapter(expenseList, null); // Pass null for click listener if not needed
        binding.budgetIdRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.budgetIdRecyclerView.setAdapter(adapter);

        // Set up SearchView listener
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

        // Set up Checkbox filters (optional)
//        binding.filterCategory1.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());
//        binding.filterCategory2.setOnCheckedChangeListener((buttonView, isChecked) -> applyFilters());

        // Fetch initial data
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchExpenseActiveBudgetServices.fetchExpensesActiveBudget(requireContext(), queue, expenses -> {
            expenseList.clear();
            expenseList.addAll(expenses);
            adapter.notifyDataSetChanged();
        });
    }

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
//@Override
//public void onExpenseClick(ExpenseModel expense) {
//    RequestQueue queue = Volley.newRequestQueue(requireContext());
//    fetchGroceryListServices.fetchGroceryList(requireContext(), queue, expense.getExpenseId(), new fetchGroceryListServices.ExpenseListFetchListener() {
//        @Override
//        public void onExpensesFetched(ExpenseModel expense) {
//            Bundle bundle = new Bundle();
//            bundle.putString("bill_name", expense.getBillName());
//            bundle.putString("date_of_purchase", expense.getDateOfPurchase());
//            bundle.putString("total_price", String.valueOf(expense.getBillAmount()));
//            bundle.putString("total_quantity", String.valueOf(expense.getTotalQuantity()));
//            bundle.putString("expense_id", expense.getExpenseId());
//            bundle.putString("description", expense.getDescription());
//            bundle.putSerializable("grocery_items", (ArrayList<GroceryItemModel>) expense.getGroceryItems());
//
//            NavController navController = NavHostFragment.findNavController(requireParentFragment());
//            navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
//        }
//
////            @Override
////            public void onExpensesListFetched(List<GroceryItemModel> groceryItems) {
////                // Handle the GroceryItemModel list data
////                Log.d("GroceryListener", "Grocery items fetched: " + groceryItems.size());
////            }
//    });
//
//}
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}