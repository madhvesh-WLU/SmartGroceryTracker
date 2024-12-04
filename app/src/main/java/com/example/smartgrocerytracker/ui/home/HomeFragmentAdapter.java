package com.example.smartgrocerytracker.ui.home;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static androidx.core.content.res.TypedArrayUtils.getString;

import static java.security.AccessController.getContext;

import android.content.Context;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.highlight.Highlight;
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

    // Animation fields
    private Handler handler = new Handler();
    private boolean isHighlighted = false;

    public HomeFragmentAdapter(View rootView) {
        this.context = rootView.getContext();

        // Initialize views
        welcomeView = rootView.findViewById(R.id.welcomeview);
        valueLeftToSpend = rootView.findViewById(R.id.value_left_to_spend);
        valueTotalBudget = rootView.findViewById(R.id.value_total_budget);
        pieChart = rootView.findViewById(R.id.pie_chart);

        // Setup and animate the PieChart initially
        setupPieChart();
        animatePieChartDrop();
        setupPieChartAnimation();
    }

    /**
     * Updates the welcome message with the given username.
     *
     * @param username The name of the user.
     */
    public void updateWelcomeMessage(String username) {
        // Get the message string with placeholder replacement
        String welcomeMessage = context.getString(R.string.welcome_message, username != null ? username : "Guest");
        welcomeView.setText(welcomeMessage);
    }


    /**
     * Updates the budget information displayed in the TextViews.
     *
     * @param budgetAmount The total budget amount.
     * @param spentAmount  The amount spent.
     */
    public void updateBudgetInfo(String budgetAmount, String spentAmount) {
        if (!budgetAmount.equals(lastBudgetAmount) || !spentAmount.equals(lastSpentAmount)) {
            valueTotalBudget.setText(budgetAmount == null ? "No Active Budget" : budgetAmount);
            valueLeftToSpend.setText(spentAmount == null ? "$0" : spentAmount);

            // Update state
            lastBudgetAmount = budgetAmount;
            lastSpentAmount = spentAmount;
        }
    }

    /**
     * Updates the PieChart with the latest spent and remaining amounts.
     *
     * @param spent     The amount spent.
     * @param remaining The amount remaining.
     */
    public void updatePieChart(float spent, float remaining) {
        if (spent != lastSpent || remaining != lastRemaining) {
            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(spent,context.getString(R.string.spent)));
            entries.add(new PieEntry(remaining,context.getString(R.string.remaining)));

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
            pieChart.getDescription().setEnabled(false); // Remove description label
            pieChart.invalidate(); // Refresh the chart

            // Update state
            lastSpent = spent;
            lastRemaining = remaining;
        }
    }

    /**
     * Sets up the initial configuration for the PieChart.
     */
    private void setupPieChart() {
        // Create entries for the pie chart (assuming we have Spent and Remaining)
        List<PieEntry> entries = new ArrayList<>();
        float spentValue = lastSpent >= 0 ? lastSpent : 40f; // Example spent value or use lastSpent
        float remainingValue = lastRemaining >= 0 ? lastRemaining : 100f - spentValue;

        entries.add(new PieEntry(spentValue, "Spent"));
        entries.add(new PieEntry(remainingValue, "Remaining"));

        // Determine colors based on percentage values
        int spentColor;

        if (spentValue >= 50f) {
            spentColor = context.getResources().getColor(R.color.green);
        } else if (spentValue >= 30f) {
            spentColor = context.getResources().getColor(R.color.vibrant_orange);
        } else {
            spentColor = context.getResources().getColor(R.color.red);
        }

        int remainingColor = context.getResources().getColor(R.color.green); // Always green for remaining

        // Create a PieDataSet and configure it
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(20f); // How much the slice moves when highlighted
        dataSet.setColors(new int[]{spentColor, remainingColor}); // Set colors for slices

        // Create PieData and set it to the pie chart
        PieData data = new PieData(dataSet);
        data.setValueTextSize(16f);
        pieChart.setData(data);

        // Disable the center hole and legend for a cleaner look
        pieChart.setDrawHoleEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false); // Remove description label

        pieChart.invalidate(); // Refresh the chart
    }

    /**
     * Animates the PieChart dropping from the top with a bounce effect.
     */
    private void animatePieChartDrop() {
        // Create the translate animation for the PieChart to simulate dropping from the top
        TranslateAnimation animation = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, -1000, // Start from above its final position
                Animation.ABSOLUTE, 0 // End at its original position
        );

        animation.setDuration(1500); // Duration for the drop animation (1.5 seconds)
        animation.setInterpolator(new BounceInterpolator()); // Add bounce effect when reaching the final position
        animation.setFillAfter(true); // Ensure the PieChart stays in the final position
        pieChart.startAnimation(animation);
    }

    /**
     * Sets up periodic animations for the PieChart, such as highlighting slices.
     */
    private void setupPieChartAnimation() {
        final int highlightIndex = 0; // The index of the slice to highlight (e.g., "Spent")

        // Set up a handler to highlight and reset the slice every 2 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isHighlighted) {
                    // Reset the chart back to its normal state (remove the highlight)
                    pieChart.highlightValue(null);
                } else {
                    // Highlight the slice (e.g., "Spent")
                    Highlight highlight = new Highlight(highlightIndex, 0, 0);
                    pieChart.highlightValue(highlight);
                }
                isHighlighted = !isHighlighted;

                // Repeat this every 2 seconds
                handler.postDelayed(this, 2000);
            }
        }, 2000); // Start after 2 seconds
    }

    /**
     * Cleans up any ongoing animations or handlers to prevent memory leaks.
     * Call this method when the adapter is no longer needed, such as in onDestroy().
     */
    public void cleanup() {
        handler.removeCallbacksAndMessages(null);
    }
}