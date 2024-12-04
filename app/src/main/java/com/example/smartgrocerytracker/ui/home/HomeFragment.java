package com.example.smartgrocerytracker.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.BudgetModel;
import com.example.smartgrocerytracker.ModelClass.SharedBudgetViewModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentHomeBinding;
import com.example.smartgrocerytracker.services.fetchBudgetDetails;
import com.example.smartgrocerytracker.utils.BudgetDialog;

public class HomeFragment extends Fragment{

    private FragmentHomeBinding binding;
    private HomeFragmentAdapter adapter;
    private SharedBudgetViewModel sharedBudgetViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Initialize the adapter
        adapter = new HomeFragmentAdapter(binding.getRoot());

        // Set up the user head
        setUserHead();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        // Grocery List Button
        binding.grocerylistMainButton.setOnClickListener(v ->
                navController.navigate(R.id.nav_expense_fragment));

        binding.monthlyMainButton.setOnClickListener(v ->
                navController.navigate(R.id.nav_search_fragment));

        // Initialize Shared ViewModel
        sharedBudgetViewModel = new ViewModelProvider(requireActivity()).get(SharedBudgetViewModel.class);

        // Observe changes in the budget data
        sharedBudgetViewModel.getBudgetModel().observe(getViewLifecycleOwner(), this::updateBudgetData);

        fetchBudgetDetails.getBudgetService(requireContext(), new fetchBudgetDetails.BudgetDetailsUpdateListener() {
            @Override
            public void onBudgetDetailsUpdated(BudgetModel budgetModel) {
                updateBudgetData(budgetModel);
            }
        });
    }


    private void updateBudgetData(BudgetModel updatedBudgetModel) {
        if (updatedBudgetModel == null) return;

        float spent = (float) updatedBudgetModel.getSpentAmount();
        float budget = (float) updatedBudgetModel.getBudgetAmount();
        String remaining = String.valueOf(budget - spent);

        // Update adapter/UI
        adapter.updateBudgetInfo(String.valueOf(budget), remaining);
        adapter.updatePieChart(spent, budget - spent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setUserHead() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");
        adapter.updateWelcomeMessage(username);
    }
}