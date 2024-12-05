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
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentSearchBinding;
import com.example.smartgrocerytracker.services.deleteGroceryServices;
import com.example.smartgrocerytracker.services.fetchGroceriesActiveBudgetServices;
import com.example.smartgrocerytracker.services.searchGroceryItemsActiveBudgetServices;
import com.example.smartgrocerytracker.ui.expenses.GroceryItemAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment implements GroceryItemAdapter.OnGroceryLongClickListener {

    private FragmentSearchBinding binding;
    private GroceryItemAdapter adapter;
    private List<GroceryItemModel> groceryItemList;
    private RequestQueue queue;
    private String budgetId;
    private String searchQuery = "";
    private String selectedCategory = null;
    private boolean isCategoryFilterApplied = false;

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
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle("Monthly Spend");

            toolbar.setNavigationIcon(R.drawable.back_arrow);
            toolbar.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            toolbar.setNavigationOnClickListener(v -> {
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();
            });
        }

        searchView.setQueryHint("Search Grocery Name");

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        budgetId = sharedPreferences.getString("budget_id", null);

        binding.deleteGList.setOnClickListener(v -> deleteSelectedItems());

        queue = Volley.newRequestQueue(requireContext());

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

        MaterialButton applyFilterButton = binding.applyFilterButton;
        applyFilterButton.setOnClickListener(v -> {
            isCategoryFilterApplied = true;
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
                filterResults();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                filterResults();
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

    private void checkForNoResults() {
        if (adapter.getItemCount() == 0) {
            binding.budgetIdRecyclerView.setVisibility(View.GONE);
            binding.noResultsLayout.setVisibility(View.VISIBLE);
        } else {
            binding.budgetIdRecyclerView.setVisibility(View.VISIBLE);
            binding.noResultsLayout.setVisibility(View.GONE);
        }
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

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setNavigationIcon(null);
            toolbar.setTitle("Smart Grocery Tracker");
        }
    }

}
