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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<ExpenseModel> expenses;

    public ExpenseAdapter(List<ExpenseModel> expenses) {
        this.expenses = expenses;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseModel expense = expenses.get(position);
        holder.billName.setText(expense.getBillName());
        holder.billAmount.setText("Amount: $" + expense.getBillAmount());
        holder.dateOfPurchase.setText("Date: " + expense.getDateOfPurchase());
        holder.description.setText(expense.getDescription());
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView billName, billAmount, dateOfPurchase, description;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            billName = itemView.findViewById(R.id.billName);
            billAmount = itemView.findViewById(R.id.billAmount);
            dateOfPurchase = itemView.findViewById(R.id.dateOfPurchase);
            description = itemView.findViewById(R.id.description);
        }
    }
}
