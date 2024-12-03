package com.example.smartgrocerytracker.ui.globalSearch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentGlobalSearchBinding;
import com.example.smartgrocerytracker.ModelClass.BillInfo;
import com.example.smartgrocerytracker.ModelClass.GroceryItem;
import com.example.smartgrocerytracker.services.GlobalSearchServices;
import com.example.smartgrocerytracker.services.fetchGroceryListServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalSearchFragment extends Fragment implements SearchResultsAdapter.OnItemClickListener {

    private FragmentGlobalSearchBinding binding;
    private SearchResultsAdapter adapter;
    private GlobalSearchServices services;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGlobalSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView and adapter
        adapter = new SearchResultsAdapter(this);
        binding.searchResultsRecyclerView.setAdapter(adapter);
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize Services
        services = new GlobalSearchServices(requireContext());

        // Setup spinners for year and month
        setupSpinners();

        // Populate category chips dynamically

        // Setup chip listeners
        setupChipClickListeners();

        // Apply filter button
        binding.applyFilterButton.setOnClickListener(v -> performSearch());
    }

    private void setupSpinners() {
        // Add placeholder to year options
        List<String> years = new ArrayList<>();
        years.add("Select Year"); // Placeholder
        years.addAll(Arrays.asList(getYears())); // Add actual years

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearSpinner.setAdapter(yearAdapter);

        // Add placeholder to month options
        List<String> months = new ArrayList<>();
        months.add("Select Month"); // Placeholder
        months.addAll(Arrays.asList(getMonths())); // Add actual months

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.monthSpinner.setAdapter(monthAdapter);


        // Populate the spinner with categories
        List<String> categories = getCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);
    }


    private List<String> getCategories() {
        // Replace this with actual data fetching logic
        return Arrays.asList("Fruits", "Vegetables", "Electronics", "Clothing");
    }

    private void setupChipClickListeners() {
        binding.chipBillName.setOnClickListener(v -> {
            binding.categoryChipGroup.check(R.id.chip_bill_name); // Explicitly set the chip as selected
            binding.editTextBillOrGrocery.setVisibility(View.VISIBLE);
            binding.categorySpinner.setVisibility(View.GONE);
            binding.editTextBillOrGrocery.setText(""); // Reset the text field
        });

        binding.chipGroceryName.setOnClickListener(v -> {
            binding.categoryChipGroup.check(R.id.chip_grocery_name); // Explicitly set the chip as selected
            binding.editTextBillOrGrocery.setVisibility(View.VISIBLE);
            binding.categorySpinner.setVisibility(View.GONE);
            binding.editTextBillOrGrocery.setText(""); // Reset the text field
        });

        binding.chipCategory.setOnClickListener(v -> {
            binding.categoryChipGroup.check(R.id.chip_category); // Explicitly set the chip as selected
            binding.editTextBillOrGrocery.setVisibility(View.GONE);
            binding.categorySpinner.setVisibility(View.VISIBLE);
            binding.editTextBillOrGrocery.setText("");
        });
    }

    private void performSearch() {
        Log.d("GlobalSearch", "Perform search triggered");

        int checkedChipId = binding.categoryChipGroup.getCheckedChipId();
        if (checkedChipId == -1) {
            Log.e("GlobalSearch", "No chip selected. Please select a filter.");
            return; // Exit early if no chip is selected
        }

        String query = binding.editTextBillOrGrocery.getText().toString().trim();
        String selectedYear = binding.yearSpinner.getSelectedItem() != null ? binding.yearSpinner.getSelectedItem().toString() : "";
        String selectedMonthName = binding.monthSpinner.getSelectedItem() != null ? binding.monthSpinner.getSelectedItem().toString() : "";

        // Skip "Select Year" and "Select Month"
        selectedYear = selectedYear.equals("Select Year") ? "" : selectedYear;
        String selectedMonth = selectedMonthName.equals("Select Month") ? "" : getMonthNumber(selectedMonthName);

        if (checkedChipId == R.id.chip_bill_name) {
            if (!query.isEmpty()) {
                Log.d("GlobalSearch", "Bill Name search triggered with query: " + query);
                services.fetchBillNameData(query, selectedYear, selectedMonth,
                        this::handleBillNameResponse,
                        this::handleError);
            }
        } else if (checkedChipId == R.id.chip_grocery_name) {
            if (!query.isEmpty()) {
                Log.d("GlobalSearch", "Grocery Name search triggered with query: " + query);
                services.fetchGroceryNameData(query, selectedYear, selectedMonth,
                        this::handleGroceryNameResponse,
                        this::handleError);
            }
        } else if (checkedChipId == R.id.chip_category) {
            String selectedCategory = binding.categorySpinner.getSelectedItem() != null ? binding.categorySpinner.getSelectedItem().toString() : "";
            if (!selectedCategory.isEmpty()) {
                Log.d("GlobalSearch", "Category search triggered with category: " + selectedCategory);
                services.fetchCategoryData(selectedCategory, selectedYear, selectedMonth,
                        this::handleCategoryResponse,
                        this::handleError);
            }
        }
    }
    private void handleBillNameResponse(JSONObject response) {
        try {
            JSONArray dataArray = response.getJSONArray("data");

            List<BillInfo> billList = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                Log.i("Big data",dataArray.getJSONObject(i).toString());
                JSONObject jsonObject = dataArray.getJSONObject(i);
                BillInfo billInfo = new BillInfo();
                billInfo.setBillName(jsonObject.getString("bill_name"));
                billInfo.setBillAmount(jsonObject.getDouble("bill_amount"));
                billInfo.setTotalQuantity(jsonObject.getDouble("total_quantity"));
                billInfo.setDateOfPurchase(jsonObject.getString("date_of_purchase"));
                billInfo.setDescription(jsonObject.optString("description"));
                billInfo.setExpenseId(jsonObject.getString("expense_id"));
                billInfo.setBudgetId(jsonObject.optString("budget_id"));
                billInfo.setStoreId(jsonObject.optString("store_id"));
                billInfo.setUserId(jsonObject.getString("user_id"));
                billInfo.setCreatedAt(jsonObject.getString("created_at"));
                billList.add(billInfo);
            }
            adapter.setViewType(0);
            adapter.setData(billList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleGroceryNameResponse(JSONObject response) {
        try {
            JSONArray dataArray = response.getJSONArray("data");
            List<GroceryItem> groceryList = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject jsonObject = dataArray.getJSONObject(i);
                GroceryItem groceryItem = new GroceryItem();
                groceryItem.setItemName(jsonObject.getString("item_name"));
                groceryItem.setQuantity(jsonObject.getInt("quantity"));
                groceryItem.setCategory(jsonObject.getString("category"));
                groceryItem.setPrice(jsonObject.getDouble("price"));
                groceryItem.setPurchased(jsonObject.getBoolean("purchased"));
                groceryItem.setItemId(jsonObject.getString("item_id"));
                groceryItem.setUserId(jsonObject.getString("user_id"));
                groceryItem.setExpenseId(jsonObject.getString("expense_id"));
                groceryItem.setBillName(jsonObject.optString("bill_name"));
                groceryItem.setBillAmount(jsonObject.optDouble("bill_amount", 0.0));
                groceryItem.setTotalQuantity(jsonObject.optDouble("total_quantity", 0.0));
                groceryItem.setDateOfPurchase(jsonObject.optString("date_of_purchase"));
                groceryItem.setDescription(jsonObject.optString("description"));
                groceryItem.setBudgetId(jsonObject.optString("budget_id"));
                groceryItem.setStoreId(jsonObject.optString("store_id"));
                groceryList.add(groceryItem);
            }
            adapter.setViewType(1);
            adapter.setData(groceryList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleCategoryResponse(JSONObject response) {
        handleGroceryNameResponse(response); // Category response structure is the same as GroceryName
    }

    private void handleError(VolleyError error) {
        Log.e("Asd",error.getMessage());
        error.printStackTrace();
    }

    private String[] getYears() {
        return new String[]{"2024","2023", "2022", "2021", "2020", "2019"};
    }

    private String[] getMonths() {
        return new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    }

    private String getMonthNumber(String monthName) {
        switch (monthName) {
            case "January": return "01";
            case "February": return "02";
            case "March": return "03";
            case "April": return "04";
            case "May": return "05";
            case "June": return "06";
            case "July": return "07";
            case "August": return "08";
            case "September": return "09";
            case "October": return "10";
            case "November": return "11";
            case "December": return "12";
            default: return ""; // Return an empty string if the month name is not valid
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(Object item) {
        if (item instanceof BillInfo) {
            BillInfo billInfo = (BillInfo) item;
            RequestQueue queue = Volley.newRequestQueue(requireContext());
            fetchGroceryListServices.fetchGroceryList(requireContext(), queue, billInfo.getExpenseId(), new fetchGroceryListServices.ExpenseListFetchListener() {
                @Override
                public void onExpensesFetched(ExpenseModel expense) {
                    Bundle bundle = new Bundle();
                    bundle.putString("bill_name",  expense.getBillName());
                    bundle.putString("date_of_purchase", expense.getDateOfPurchase());
                    bundle.putString("total_price", String.valueOf(expense.getBillAmount()));
                    bundle.putString("total_quantity", String.valueOf(expense.getTotalQuantity()));
                    bundle.putString("expense_id", expense.getExpenseId());
                    bundle.putString("description", expense.getDescription());
                    bundle.putString("budget_id", expense.getBudgetId());
                    bundle.putSerializable("grocery_items", (ArrayList<GroceryItemModel>) expense.getGroceryItems());

                    NavController navController = NavHostFragment.findNavController(requireParentFragment());
                    navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
                }
            });
        } else if (item instanceof GroceryItem) {
            GroceryItem groceryItem = (GroceryItem) item;
            RequestQueue queue = Volley.newRequestQueue(requireContext());
            fetchGroceryListServices.fetchGroceryList(requireContext(), queue, groceryItem.getExpenseId(), new fetchGroceryListServices.ExpenseListFetchListener() {
                @Override
                public void onExpensesFetched(ExpenseModel expense) {
                    Bundle bundle = new Bundle();
                    bundle.putString("bill_name",  expense.getBillName());
                    bundle.putString("date_of_purchase", expense.getDateOfPurchase());
                    bundle.putString("total_price", String.valueOf(expense.getBillAmount()));
                    bundle.putString("total_quantity", String.valueOf(expense.getTotalQuantity()));
                    bundle.putString("expense_id", expense.getExpenseId());
                    bundle.putString("description", expense.getDescription());
                    bundle.putString("budget_id", expense.getBudgetId());
                    bundle.putSerializable("grocery_items", (ArrayList<GroceryItemModel>) expense.getGroceryItems());

                    NavController navController = NavHostFragment.findNavController(requireParentFragment());
                    navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
                }
            });
        }
    }
}