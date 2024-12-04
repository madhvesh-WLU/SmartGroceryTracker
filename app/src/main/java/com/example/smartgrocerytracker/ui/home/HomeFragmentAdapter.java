package com.example.smartgrocerytracker.ui.home;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.example.smartgrocerytracker.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragmentAdapter {

    private final Context context;
    private final TextView welcomeView;
    private final TextView valueLeftToSpend;
    private final TextView valueTotalBudget;
    private final PieChart pieChart;

    // State fields
    private String lastBudgetAmount = null;
    private String lastSpentAmount = null;
    private float lastSpent = -1f;
    private float lastRemaining = -1f;

    public HomeFragmentAdapter(View rootView) {
        this.context = rootView.getContext();

        // Initialize views
        welcomeView = rootView.findViewById(R.id.welcomeview);
        valueLeftToSpend = rootView.findViewById(R.id.value_left_to_spend);
        valueTotalBudget = rootView.findViewById(R.id.value_total_budget);
        pieChart = rootView.findViewById(R.id.pie_chart);
    }

    public void updateWelcomeMessage(String username) {
        welcomeView.setText("Hello, " + (username == null ? "Guest" : username));
    }

    public void updateBudgetInfo(String budgetAmount, String spentAmount) {
        if (!budgetAmount.equals(lastBudgetAmount) || !spentAmount.equals(lastSpentAmount)) {
            valueTotalBudget.setText(budgetAmount == null ? "No Active Budget" : budgetAmount);
            valueLeftToSpend.setText(spentAmount == null ? "$0" : spentAmount);

            // Update state
            lastBudgetAmount = budgetAmount;
            lastSpentAmount = spentAmount;
        }
    }

    public void updatePieChart(float spent, float remaining) {
        if (spent != lastSpent || remaining != lastRemaining) {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(spent, "Spent"));
            entries.add(new PieEntry(remaining, "Remaining"));

            PieDataSet dataSet = new PieDataSet(entries, "Budget Breakdown");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(10f);
            dataSet.setColors(
                    context.getResources().getColor(R.color.green),
                    context.getResources().getColor(R.color.red)
            );

            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.setDrawHoleEnabled(false);
            pieChart.getLegend().setEnabled(false);
            pieChart.invalidate(); // Refresh the chart

            // Update state
            lastSpent = spent;
            lastRemaining = remaining;
        }
    }
}