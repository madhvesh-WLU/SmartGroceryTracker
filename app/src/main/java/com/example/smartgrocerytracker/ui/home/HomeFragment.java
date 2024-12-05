package com.example.smartgrocerytracker.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
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

import com.example.smartgrocerytracker.ModelClass.BudgetModel;
import com.example.smartgrocerytracker.ModelClass.SharedBudgetViewModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentHomeBinding;
import com.example.smartgrocerytracker.services.fetchBudgetDetails;
import com.example.smartgrocerytracker.utils.LanguageUtil;
public class HomeFragment extends Fragment{

    private FragmentHomeBinding binding;
    private HomeFragmentAdapter adapter;
    private SharedBudgetViewModel sharedBudgetViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        adapter = new HomeFragmentAdapter(binding.getRoot());

        setUserHead();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        binding.grocerylistMainButton.setOnClickListener(v ->
                navController.navigate(R.id.nav_expense_fragment));

        binding.monthlyMainButton.setOnClickListener(v ->
                navController.navigate(R.id.nav_search_fragment));

        // Shared ViewModel for live Updayes
        sharedBudgetViewModel = new ViewModelProvider(requireActivity()).get(SharedBudgetViewModel.class);

        sharedBudgetViewModel.getBudgetModel().observe(getViewLifecycleOwner(), this::updateBudgetData);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        String budgetId = sharedPreferences.getString("budget_id",null);
        fetchBudgetDetails.getBudgetService(requireContext(),budgetId, new fetchBudgetDetails.BudgetDetailsUpdateListener() {
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
        float remainingAmount = budget - spent;

        // Format values to two decimal places
        String formattedBudget = String.format("%.2f", budget);
        String formattedRemaining = String.format("%.2f", remainingAmount);

        adapter.updateBudgetInfo(formattedBudget, formattedRemaining);
        adapter.updatePieChart(spent, remainingAmount);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LanguageUtil.setLocale(context);
    }
}