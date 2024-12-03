package com.example.smartgrocerytracker.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PieChart pieChart;
    private Handler handler = new Handler();
    private boolean isHighlighted = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Initialize and set up PieChart
        pieChart = binding.pieChart;  // Reference PieChart from the binding
        setupPieChart();
        setupPieChartAnimation();

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        handler.removeCallbacksAndMessages(null);  // Remove callbacks when the fragment is destroyed
    }

    private void setUserHead() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");
        if (binding.welcomeview != null) {
            binding.welcomeview.setText("Hello, " + username);
        } else {
            Log.e("UserName", "TextView not found.");
        }
    }

    private void setupPieChart() {
        // Create entries for the pie chart
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(80f, "Class A"));
        entries.add(new PieEntry(20f, "Class B"));

        // Create a PieDataSet and configure it
        PieDataSet dataSet = new PieDataSet(entries, "Pie Chart Classes");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(20f); // How much the slice moves when highlighted
        dataSet.setColors(new int[]{getResources().getColor(R.color.green), getResources().getColor(R.color.vibrant_orange)}); // Set colors for slices

        // Create PieData and set it to the pie chart
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(false); // Disable the center hole
        pieChart.getLegend().setEnabled(false); // Disable the legend for a cleaner look
        pieChart.invalidate(); // Refresh the chart
    }

    private void setupPieChartAnimation() {
        final int highlightIndex = 1; // The index of the slice to highlight (20%)

        // Set up a handler to highlight and reset the slice every 2 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isHighlighted) {
                    // Reset the chart back to its normal state (remove the highlight)
                    pieChart.highlightValue(null);
                } else {
                    // Highlight the slice (Class B: 20%)
                    Highlight highlight = new Highlight(highlightIndex, 0, 0);
                    pieChart.highlightValue(highlight);
                }
                isHighlighted = !isHighlighted;

                // Repeat this every 2 seconds
                handler.postDelayed(this, 2000);
            }
        }, 2000); // Start after 2 seconds
    }
}
