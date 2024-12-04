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

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.BudgetModel;
import com.example.smartgrocerytracker.ModelClass.SharedBudgetViewModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentHomeBinding;
import com.example.smartgrocerytracker.services.fetchBudgetDetails;
import com.example.smartgrocerytracker.utils.BudgetDialog;
import com.github.mikephil.charting.highlight.Highlight;

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


//
//package com.example.smartgrocerytracker.ui.home;
//
//import static android.content.Context.MODE_PRIVATE;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.Animation;
//import android.view.animation.BounceInterpolator;
//import android.view.animation.TranslateAnimation;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//
//import com.example.smartgrocerytracker.R;
//import com.example.smartgrocerytracker.databinding.FragmentHomeBinding;
//import com.example.smartgrocerytracker.utils.LanguageUtil;
//import com.github.mikephil.charting.charts.PieChart;
//import com.github.mikephil.charting.data.PieData;
//import com.github.mikephil.charting.data.PieDataSet;
//import com.github.mikephil.charting.data.PieEntry;
//import com.github.mikephil.charting.highlight.Highlight;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HomeFragment extends Fragment {
//
//    private FragmentHomeBinding binding;
//    private PieChart pieChart;
//    private Handler handler = new Handler();
//    private boolean isHighlighted = false;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        animatePieChartDrop();
//        // Initialize and set up PieChart
//        pieChart = binding.pieChart;  // Reference PieChart from the binding
//        setupPieChart();
//        setupPieChartAnimation();
//
//        // Set up the user head
//        setUserHead();
//
//        setupClassLabels();
//
//        return binding.getRoot();
//    }
//
//    private void setupClassLabels() {
//        float spentValue = 40f; // Example spent value; you can modify or calculate this dynamically
//
//        int spentColor;
//        if (spentValue >= 50f) {
//            spentColor = getResources().getColor(R.color.green);
//        } else if (spentValue >= 30f) {
//            spentColor = getResources().getColor(R.color.vibrant_orange);
//        } else {
//            spentColor = getResources().getColor(R.color.red);
//        }
//
//        // Set Spent label text and color
//        binding.spentLabel.setText("Spent");
//        binding.spentLabel.setTextColor(spentColor);
//
//        // Set Remaining label text and color
//        binding.remainingLabel.setText("Remaining");
//        binding.remainingLabel.setTextColor(getResources().getColor(R.color.green)); // Always green for remaining
//    }
//
//
//    private void animatePieChartDrop() {
//        // Create the translate animation for the PieChart to simulate dropping from the top
//        TranslateAnimation animation = new TranslateAnimation(
//                Animation.ABSOLUTE, 0,
//                Animation.ABSOLUTE, 0,
//                Animation.ABSOLUTE, -1000, // Start from above its final position
//                Animation.ABSOLUTE, 0 // End at its original position
//        );
//
//        animation.setDuration(1500); // Duration for the drop animation (1.5 seconds)
//        animation.setInterpolator(new BounceInterpolator()); // Add bounce effect when reaching the final position
//        animation.setFillAfter(true); // Ensure the PieChart stays in the final position
//        binding.pieChart.startAnimation(animation);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        NavController navController = Navigation.findNavController(view);
//
//        // Grocery List Button
//        binding.grocerylistMainButton.setOnClickListener(v ->
//                navController.navigate(R.id.nav_expense_fragment));
//
//        binding.monthlyMainButton.setOnClickListener(v ->
//                navController.navigate(R.id.nav_search_fragment));
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//        handler.removeCallbacksAndMessages(null);  // Remove callbacks when the fragment is destroyed
//    }
//
//    private void setUserHead() {
//        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", MODE_PRIVATE);
//        String username = sharedPreferences.getString("username", "Guest");
//        if (binding.welcomeview != null) {
//            binding.welcomeview.setText("Hello, " + username);
//        } else {
//            Log.e("UserName", "TextView not found.");
//        }
//    }
//
//    private void setupPieChart() {
//        // Create entries for the pie chart (assuming we have Spent and Remaining)
//        List<PieEntry> entries = new ArrayList<>();
//        float spentValue = 40f; // Example spent value; you can modify or calculate this dynamically
//        float remainingValue = 100f - spentValue;
//
//        entries.add(new PieEntry(spentValue, ""));
//        entries.add(new PieEntry(remainingValue, ""));
//
//        // Determine colors based on percentage values
//        int spentColor, remainingColor;
//
//        if (spentValue >= 50f) {
//            spentColor = getResources().getColor(R.color.green);
//        } else if (spentValue >= 30f) {
//            spentColor = getResources().getColor(R.color.vibrant_orange);
//        } else {
//            spentColor = getResources().getColor(R.color.red);
//        }
//
//        remainingColor = getResources().getColor(R.color.green); // Assume "Remaining" is always green for a positive balance
//
//        // Create a PieDataSet and configure it
//        PieDataSet dataSet = new PieDataSet(entries, "");
//        dataSet.setSliceSpace(3f);
//        dataSet.setSelectionShift(20f); // How much the slice moves when highlighted
//        dataSet.setColors(new int[]{spentColor, remainingColor}); // Set colors for slices
//
//        // Create PieData and set it to the pie chart
//        PieData data = new PieData(dataSet);
//        data.setValueTextSize(16f);
//        pieChart.setData(data);
//
//        // Disable the center hole and legend for a cleaner look
//        pieChart.setDrawHoleEnabled(false);
//        pieChart.getLegend().setEnabled(false);
//        pieChart.getDescription().setEnabled(false); // Remove description label
//
//        pieChart.invalidate(); // Refresh the chart
//    }
//    private void setupPieChartAnimation() {
//        final int highlightIndex = 0; // The index of the slice to highlight (20%)
//
//        // Set up a handler to highlight and reset the slice every 2 seconds
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isHighlighted) {
//                    // Reset the chart back to its normal state (remove the highlight)
//                    pieChart.highlightValue(null);
//                } else {
//                    // Highlight the slice (Class B: 20%)
//                    Highlight highlight = new Highlight(highlightIndex, 0, 0);
//                    pieChart.highlightValue(highlight);
//                }
//                isHighlighted = !isHighlighted;
//
//                // Repeat this every 2 seconds
//                handler.postDelayed(this, 2000);
//            }
//        }, 2000); // Start after 2 seconds
//    }
//}