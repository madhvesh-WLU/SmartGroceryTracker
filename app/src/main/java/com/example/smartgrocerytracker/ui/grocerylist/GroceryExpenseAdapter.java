package com.example.smartgrocerytracker.ui.grocerylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.R;
import java.util.List;

public class GroceryExpenseAdapter extends RecyclerView.Adapter<GroceryExpenseAdapter.GroceryExpenseViewHolder> {

    private final List<ExpenseModel> expenseList;
    private final OnExpenseClickListener expenseClickListener;
    private int selectedPosition = -1;  // Track selected item

    // Interface for handling item clicks
    public interface OnExpenseClickListener {
        void onExpenseClick(ExpenseModel expense);
    }

    // Constructor to initialize the adapter with expenses and click listener
    public GroceryExpenseAdapter(List<ExpenseModel> expenseList, OnExpenseClickListener listener) {
        this.expenseList = expenseList;
        this.expenseClickListener = listener;
    }

    @NonNull
    @Override
    public GroceryExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_item_row, parent, false);
        return new GroceryExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryExpenseViewHolder holder, int position) {
        ExpenseModel expense = expenseList.get(position);
        holder.bind(expense, expenseClickListener, position == selectedPosition);

        // Set up click listener for selection
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
            expenseClickListener.onExpenseClick(expense);  // Notify listener of click
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // Method to delete the selected item
    public void deleteSelectedItem() {
        if (selectedPosition != -1 && selectedPosition < expenseList.size()) {
            expenseList.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            selectedPosition = -1;  // Reset selected position after deletion
        }
    }

    static class GroceryExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemNameTextView;
        private final TextView categoryTextView;
        private final TextView priceTextView;
        private final TextView quantityTextView;

        public GroceryExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name);
            categoryTextView = itemView.findViewById(R.id.category);
            quantityTextView = itemView.findViewById(R.id.quantity);
            priceTextView = itemView.findViewById(R.id.price);
        }

        // Binds data to views and highlights if selected
        public void bind(ExpenseModel expense, OnExpenseClickListener listener, boolean isSelected) {
            itemNameTextView.setText(expense.getItemName());
            categoryTextView.setText(expense.getCategory());
            quantityTextView.setText(String.valueOf(expense.getQuantity()));
            priceTextView.setText(String.format("$%.2f", expense.getPrice()));

            // Highlight the selected item
            itemView.setBackgroundColor(isSelected ? itemView.getResources().getColor(R.color.inactive_color) : itemView.getResources().getColor(android.R.color.transparent));
        }
    }
}
