// YearSearchFragment.java
package com.example.smartgrocerytracker.ui.grocerylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentYearSearchBinding;

public class YearSearchFragment extends Fragment {

    private FragmentYearSearchBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentYearSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

       /* // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);

        // Search button click listener
        binding.searchButton.setOnClickListener(v -> performSearch());*/
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
        // Implement search logic based on selected year, month, and category
        String selectedYear = binding.yearSpinner.getSelectedItem().toString();
        String selectedMonth = binding.monthSpinner.getSelectedItem().toString();
       //String selectedCategory = binding.categorySpinner.getSelectedItem().toString();

        // Use these selections to filter your data
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
