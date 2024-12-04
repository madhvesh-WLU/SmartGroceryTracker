package com.example.smartgrocerytracker.ui.grocerylist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentYearSearchBinding;
import com.example.smartgrocerytracker.utils.LanguageUtil;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class YearSearchFragment extends Fragment {

    private FragmentYearSearchBinding binding;

    private RecyclerView searchResultsRecyclerView;
    private View addsearch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentYearSearchBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchResultsRecyclerView = binding.searchResultsRecyclerView;
        addsearch = binding.addResults;

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            // Set the title for Store Locator
            toolbar.setTitle("Global Grocery Search");
        }
        // Setup year spinner
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, getYears());
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearSpinner.setAdapter(yearAdapter);

        // Setup month spinner
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, getMonths());
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.monthSpinner.setAdapter(monthAdapter);

        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);

        // Setup Chip click listeners
        setupChipClickListeners();

        // Search button click listener
        binding.applyFilterButton.setOnClickListener(v -> performSearch());


    }

    private void setupChipClickListeners() {
        // Handling chip clicks to toggle the visibility of the relevant UI elements
        binding.chipBillName.setOnClickListener(v -> toggleVisibility("Bill Name"));
        binding.chipGroceryName.setOnClickListener(v -> toggleVisibility("Grocery Name"));
        binding.chipCategory.setOnClickListener(v -> toggleVisibility("Category"));
    }

    private void toggleVisibility(String chipType) {
        // Toggle visibility of input elements based on selected chip
        switch (chipType) {
            case "Bill Name":
                binding.editTextBillOrGrocery.setVisibility(View.VISIBLE); // Show EditText for Bill/Grocery Name
                binding.categorySpinner.setVisibility(View.GONE); // Hide Category Spinner
                break;

            case "Grocery Name":
                binding.editTextBillOrGrocery.setVisibility(View.VISIBLE); // Show EditText for Bill/Grocery Name
                binding.categorySpinner.setVisibility(View.GONE); // Hide Category Spinner
                break;

            case "Category":
                binding.categorySpinner.setVisibility(View.VISIBLE); // Show Category Spinner
                binding.editTextBillOrGrocery.setVisibility(View.GONE); // Hide EditText for Bill/Grocery Name
                break;

            default:
                binding.editTextBillOrGrocery.setVisibility(View.GONE); // Hide EditText
                binding.categorySpinner.setVisibility(View.GONE); // Hide Category Spinner
                break;
        }
    }

    private String[] getYears() {
        // Populate years (e.g., last 10 years)
        return new String[]{"2023", "2022", "2021", "2020", "2019"};
    }

    private String[] getMonths() {
        // Populate months
        return new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    }

    private String[] getCategories() {
        // Populate categories as needed
        return new String[]{"Groceries", "Electronics", "Clothing", "Furniture", "Others"};
    }

    private void performSearch() {
        // Get selected values from spinners and text fields
        String selectedYear = binding.yearSpinner.getSelectedItem().toString();
        String selectedMonth = binding.monthSpinner.getSelectedItem().toString();
        String selectedCategory = binding.categorySpinner.getSelectedItem().toString();
        String billOrGrocery = binding.editTextBillOrGrocery.getText().toString();

        if (selectedYear.isEmpty() && selectedMonth.isEmpty() && selectedCategory.isEmpty() && billOrGrocery.isEmpty()) {
            searchResultsRecyclerView.setVisibility(View.GONE);
            addsearch.setVisibility(View.VISIBLE);
        } else {
            // Perform your filtering logic here and update RecyclerView
            searchResultsRecyclerView.setVisibility(View.VISIBLE);
            addsearch.setVisibility(View.GONE);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
