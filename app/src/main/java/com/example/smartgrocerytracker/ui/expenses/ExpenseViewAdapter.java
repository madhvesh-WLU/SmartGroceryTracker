package com.example.smartgrocerytracker.ui.expenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseViewAdapter extends RecyclerView.Adapter<ExpenseViewAdapter.ExpenseViewHolder> {

    private List<ExpenseModel> expenseList;
    private final OnExpenseClickListener clickListener;

    // Interface for handling item clicks
    public interface OnExpenseClickListener {
        void onExpenseClick(ExpenseModel expense);
    }

    public ExpenseViewAdapter(List<ExpenseModel> expenseList, OnExpenseClickListener clickListener) {
        this.expenseList = expenseList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseModel expense = expenseList.get(position);
        holder.storeNameTextView.setText(expense.getBillName() != null ? expense.getBillName() : "N/A");
        String date = formatDate(expense);
        holder.dateOfPurchaseTextView.setText(date);
        holder.totalPriceTextView.setText(String.valueOf("$" + expense.getBillAmount()));

        // Set item click listener
        holder.itemView.setOnClickListener(v -> clickListener.onExpenseClick(expense));
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView storeNameTextView, dateOfPurchaseTextView, totalPriceTextView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            storeNameTextView = itemView.findViewById(R.id.storeNameTextView);
            dateOfPurchaseTextView = itemView.findViewById(R.id.dateOfPurchaseTextView);
            totalPriceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }

    private String formatDate(ExpenseModel expense){
        String formattedDate = "N/A";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = isoFormat.parse(expense.getDateOfPurchase());
            if (date != null) {
                formattedDate = isoFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
