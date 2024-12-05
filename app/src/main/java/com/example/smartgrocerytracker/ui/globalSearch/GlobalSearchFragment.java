// File: GlobalSearchFragment.java
package com.example.smartgrocerytracker.ui.globalSearch;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.smartgrocerytracker.services.deleteExpenseServices;
import com.example.smartgrocerytracker.services.fetchGroceryListServices;
import com.example.smartgrocerytracker.services.deleteGroceryServices;
import com.example.smartgrocerytracker.utils.GlobalSearchUtils;
import com.example.smartgrocerytracker.utils.HideKeyboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GlobalSearchFragment extends Fragment implements
        SearchResultsAdapter.OnItemClickListener,
        SearchResultsAdapter.OnItemLongClickListener { // Implement OnItemLongClickListener

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


        adapter = new SearchResultsAdapter(this, this); // Pass both listeners
        binding.searchResultsRecyclerView.setAdapter(adapter);
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        services = new GlobalSearchServices(requireContext());
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.addResults.setVisibility(View.VISIBLE);

        setupSpinners();

        // Setup chip listeners
        setupChipClickListeners();

        // Apply filter button
        binding.applyFilterButton.setOnClickListener(v -> performSearch());
        binding.clearFilterButton.setOnClickListener(v -> clearFilters());
    }


    private void clearFilters() {
        // Clear selected chips
        binding.categoryChipGroup.clearCheck();

        // Reset text fields
        binding.editTextBillOrGrocery.setText("");

        // Reset spinners
        binding.yearSpinner.setSelection(0);
        binding.monthSpinner.setSelection(0);
        binding.categorySpinner.setSelection(0);

        binding.editTextBillOrGrocery.setVisibility(View.GONE);
        binding.categorySpinner.setVisibility(View.GONE);

        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.addResults.setVisibility(View.VISIBLE);

        Toast.makeText(requireContext(), "Filters cleared.", Toast.LENGTH_SHORT).show();
    }

    private void setupSpinners() {

        List<String> years = new ArrayList<>();
        years.add("Select Year");
        years.addAll(Arrays.asList(GlobalSearchUtils.getYears()));

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearSpinner.setAdapter(yearAdapter);

        List<String> months = new ArrayList<>();
        months.add("Select Month");
        months.addAll(Arrays.asList(GlobalSearchUtils.getMonths()));

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.monthSpinner.setAdapter(monthAdapter);

        List<String> categories = GlobalSearchUtils.getCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupChipClickListeners() {
        binding.chipBillName.setOnClickListener(v -> {
            binding.categoryChipGroup.check(R.id.chip_bill_name);
            binding.editTextBillOrGrocery.setVisibility(View.VISIBLE);
            binding.categorySpinner.setVisibility(View.GONE);
            binding.editTextBillOrGrocery.setText("");
        });

        binding.chipGroceryName.setOnClickListener(v -> {
            binding.categoryChipGroup.check(R.id.chip_grocery_name);
            binding.editTextBillOrGrocery.setVisibility(View.VISIBLE);
            binding.categorySpinner.setVisibility(View.GONE);
            binding.editTextBillOrGrocery.setText("");
        });

        binding.chipCategory.setOnClickListener(v -> {
            binding.categoryChipGroup.check(R.id.chip_category);
            binding.editTextBillOrGrocery.setVisibility(View.GONE);
            binding.categorySpinner.setVisibility(View.VISIBLE);
            binding.editTextBillOrGrocery.setText("");
        });
    }


    private void performSearch() {
        Log.d("GlobalSearch", "Perform search triggered");

        // Reset visibility to default state
        binding.errorResults.setVisibility(View.GONE);
        binding.addResults.setVisibility(View.VISIBLE);
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);

        int checkedChipId = binding.categoryChipGroup.getCheckedChipId();
        if (checkedChipId == -1) {
            Log.e("GlobalSearch", "No chip selected. Please select a filter.");
            Toast.makeText(requireContext(), "Please select a filter.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        HideKeyboard.hideKeyboard(requireContext(), getView());
        binding.addResults.setVisibility(View.GONE);

        String query = binding.editTextBillOrGrocery.getText().toString().trim();
        String selectedYear = binding.yearSpinner.getSelectedItem() != null ? binding.yearSpinner.getSelectedItem().toString() : "";
        String selectedMonthName = binding.monthSpinner.getSelectedItem() != null ? binding.monthSpinner.getSelectedItem().toString() : "";

        selectedYear = selectedYear.equals("Select Year") ? "" : selectedYear;
        String selectedMonth = selectedMonthName.equals("Select Month") ? "" : GlobalSearchUtils.getMonthNumber(selectedMonthName);

        String finalSelectedYear = selectedYear;

        new Handler().postDelayed(() -> {

            if (checkedChipId == R.id.chip_bill_name) {
                if (!query.isEmpty()) {
                    Log.d("GlobalSearch", "Bill Name search triggered with query: " + query);
                    services.fetchBillNameData(query, finalSelectedYear, selectedMonth,
                            this::handleBillNameResponse,
                            this::handleError);
                } else {
                    Toast.makeText(requireContext(), "Please enter a bill name to search.", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.addResults.setVisibility(View.VISIBLE);
                }
            } else if (checkedChipId == R.id.chip_grocery_name) {
                if (!query.isEmpty()) {
                    Log.d("GlobalSearch", "Grocery Name search triggered with query: " + query);
                    services.fetchGroceryNameData(query, finalSelectedYear, selectedMonth,
                            this::handleGroceryNameResponse,
                            this::handleError);
                }
                else {
                        Toast.makeText(requireContext(), "Please enter a Grocery name to search.", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                        binding.addResults.setVisibility(View.VISIBLE);
                    }
                }
            else if (checkedChipId == R.id.chip_category) {
                String selectedCategory = binding.categorySpinner.getSelectedItem() != null
                        ? binding.categorySpinner.getSelectedItem().toString()
                        : "";
                if (!selectedCategory.isEmpty() && !selectedCategory.equals("Select Category")) {
                    try {
                        String encodedCategory = java.net.URLEncoder.encode(selectedCategory, "UTF-8");
                        Log.d("GlobalSearch", "Category search triggered with category: " + encodedCategory);
                        services.fetchCategoryData(encodedCategory, finalSelectedYear, selectedMonth,
                                this::handleCategoryResponse,
                                this::handleError);
                    } catch (Exception e) {
                        Log.e("GlobalSearch", "Error encoding category: " + e.getMessage());
                        Toast.makeText(requireContext(), "Failed to process category. Please try again.", Toast.LENGTH_SHORT).show();
                        binding.progressBar.setVisibility(View.GONE);
                        binding.addResults.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(requireContext(), "Please select a valid category.", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.addResults.setVisibility(View.VISIBLE);
                }
            }
        }, 500);
    }


    private void handleBillNameResponse(JSONObject response) {
        try {
            binding.progressBar.setVisibility(View.GONE); // Hide progress bar
            JSONArray dataArray = response.getJSONArray("data");

            if (dataArray.length() == 0) {
                // No results found
                binding.errorResults.setVisibility(View.VISIBLE);
                binding.searchResultsRecyclerView.setVisibility(View.GONE);
                binding.addResults.setVisibility(View.GONE);
            } else {
                // Results found, show RecyclerView
                binding.errorResults.setVisibility(View.GONE);
                binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
                binding.addResults.setVisibility(View.GONE);

                List<BillInfo> billList = new ArrayList<>();
                for (int i = 0; i < dataArray.length(); i++) {
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
                adapter.setViewType(SearchResultsAdapter.VIEW_TYPE_BILL_NAME);
                adapter.setData(billList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            binding.errorResults.setVisibility(View.VISIBLE);
            binding.searchResultsRecyclerView.setVisibility(View.GONE);
            binding.addResults.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Failed to parse bill data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGroceryNameResponse(JSONObject response) {
        try {
            binding.progressBar.setVisibility(View.GONE);
            JSONArray dataArray = response.getJSONArray("data");

            if (dataArray.length() == 0) {
                binding.errorResults.setVisibility(View.VISIBLE);
                binding.searchResultsRecyclerView.setVisibility(View.GONE);
                binding.addResults.setVisibility(View.GONE);
            } else {
                binding.errorResults.setVisibility(View.GONE);
                binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
                binding.addResults.setVisibility(View.GONE);

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
                adapter.setViewType(SearchResultsAdapter.VIEW_TYPE_GROCERY_NAME);
                adapter.setData(groceryList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            binding.errorResults.setVisibility(View.VISIBLE);
            binding.searchResultsRecyclerView.setVisibility(View.GONE);
            binding.addResults.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Failed to parse grocery data.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCategoryResponse(JSONObject response) {
        try {
            binding.progressBar.setVisibility(View.GONE);

            JSONArray dataArray = response.getJSONArray("data");

            if (dataArray.length() == 0) {
                binding.errorResults.setVisibility(View.VISIBLE);
                binding.searchResultsRecyclerView.setVisibility(View.GONE);
                binding.addResults.setVisibility(View.GONE);
            } else {
                binding.errorResults.setVisibility(View.GONE);
                binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
                binding.addResults.setVisibility(View.GONE);

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
                adapter.setViewType(SearchResultsAdapter.VIEW_TYPE_GROCERY_NAME);
                adapter.setData(groceryList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            binding.errorResults.setVisibility(View.VISIBLE);
            binding.searchResultsRecyclerView.setVisibility(View.GONE);
            binding.addResults.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Failed to parse category data.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Implementing OnItemClickListener
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

                    NavController navController = NavHostFragment.findNavController(thisFragment());
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

                    NavController navController = NavHostFragment.findNavController(thisFragment());
                    navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
                }
            });
        }
    }

    @Override
    public void onItemLongClick(Object item, int position) {
        if (item instanceof GroceryItem) {
            GroceryItem groceryItem = (GroceryItem) item;
            showDeleteConfirmationDialog(groceryItem, position);
        } else if (item instanceof BillInfo) {
            BillInfo billInfo = (BillInfo) item;
            showDeleteConfirmationDialog2(billInfo, position);
            Toast.makeText(requireContext(), "Long-pressed on Bill: " + ((BillInfo) item).getBillName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog2(BillInfo billInfo, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete Grocery Item \"" + billInfo.getBillName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteExpenseItem(billInfo, position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void showDeleteConfirmationDialog(GroceryItem groceryItem, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete Grocery Item \"" + groceryItem.getItemName() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteGroceryItem(groceryItem, position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    private void deleteGroceryItem(GroceryItem groceryItem, int position) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        deleteGroceryServices.deleteGroceryItems(requireContext(), queue, groceryItem.getItemId(), () -> {
            adapter.data.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(requireContext(), "Item deleted successfully.", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteExpenseItem(BillInfo billInfo, int position) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        deleteExpenseServices.deleteExpenseItems(requireActivity(),queue, billInfo.getExpenseId(),() -> {
            adapter.data.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(requireContext(), "Bill deleted successfully.", Toast.LENGTH_SHORT).show();
        });
    }
    private void handleError(VolleyError error) {
        Log.e("GlobalSearch", "Error: " + error.getMessage());
        Toast.makeText(requireActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private Fragment thisFragment() {
        return this;
    }
}