package com.example.smartgrocerytracker.ui.home;

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


    private String lastBudgetAmount = null;
    private String lastSpentAmount = null;
    private float lastSpent = -1f;
    private float lastRemaining = -1f;


    private Handler handler = new Handler();
    private boolean isHighlighted = false;

    public HomeFragmentAdapter(View rootView) {
        this.context = rootView.getContext();


        welcomeView = rootView.findViewById(R.id.welcomeview);
        valueLeftToSpend = rootView.findViewById(R.id.value_left_to_spend);
        valueTotalBudget = rootView.findViewById(R.id.value_total_budget);
        pieChart = rootView.findViewById(R.id.pie_chart);


        setupPieChart();
        animatePieChartDrop();
        setupPieChartAnimation();
    }


    public void updateWelcomeMessage(String username) {
        String welcomeMessage = context.getString(R.string.welcome_message, username != null ? username : "Guest");
        welcomeView.setText(welcomeMessage);
    }



    public void updateBudgetInfo(String budgetAmount, String spentAmount) {
        if (!budgetAmount.equals(lastBudgetAmount) || !spentAmount.equals(lastSpentAmount)) {
            valueTotalBudget.setText(budgetAmount == null ? "No Active Budget" : budgetAmount);
            valueLeftToSpend.setText(spentAmount == null ? "$0" : spentAmount);


            lastBudgetAmount = budgetAmount;
            lastSpentAmount = spentAmount;
        }
    }


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
            pieChart.getDescription().setEnabled(false);
            pieChart.invalidate();


            lastSpent = spent;
            lastRemaining = remaining;
        }
    }

    private void setupPieChart() {

        List<PieEntry> entries = new ArrayList<>();
        float spentValue = lastSpent >= 0 ? lastSpent : 40f;
        float remainingValue = lastRemaining >= 0 ? lastRemaining : 100f - spentValue;

        entries.add(new PieEntry(spentValue, "Spent"));
        entries.add(new PieEntry(remainingValue, "Remaining"));

        int spentColor;

        if (spentValue >= 50f) {
            spentColor = context.getResources().getColor(R.color.green);
        } else if (spentValue >= 30f) {
            spentColor = context.getResources().getColor(R.color.vibrant_orange);
        } else {
            spentColor = context.getResources().getColor(R.color.red);
        }

        int remainingColor = context.getResources().getColor(R.color.green);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(20f);
        dataSet.setColors(new int[]{spentColor, remainingColor});

        PieData data = new PieData(dataSet);
        data.setValueTextSize(16f);
        pieChart.setData(data);
        pieChart.setDrawHoleEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);

        pieChart.invalidate();
    }

    /**
     * Animates the PieChart dropping from the top with a bounce effect.
     */
    private void animatePieChartDrop() {

        TranslateAnimation animation = new TranslateAnimation(
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, -1000,
                Animation.ABSOLUTE, 0
        );

        animation.setDuration(1500);
        animation.setInterpolator(new BounceInterpolator());
        animation.setFillAfter(true);
        pieChart.startAnimation(animation);
    }

    private void setupPieChartAnimation() {
        final int highlightIndex = 0;


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isHighlighted) {

                    pieChart.highlightValue(null);
                } else {

                    Highlight highlight = new Highlight(highlightIndex, 0, 0);
                    pieChart.highlightValue(highlight);
                }
                isHighlighted = !isHighlighted;


                handler.postDelayed(this, 2000);
            }
        }, 2000);
    }

    public void cleanup() {
        handler.removeCallbacksAndMessages(null);
    }
}