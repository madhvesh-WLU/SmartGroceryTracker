package com.example.smartgrocerytracker.ui.expenses;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseViewAdapter extends RecyclerView.Adapter<ExpenseViewAdapter.ExpenseViewHolder> {

    private List<ExpenseModel> expenseList;
    private final OnExpenseClickListener clickListener;
    private final OnExpenseLongClickListener longClickListener;
    private String searchQuery; // To store the current query
    private final Context context; // Store context for accessing resources
    private boolean isMultiSelectionMode = false;
    private final List<ExpenseModel> selectedItems = new ArrayList<>();
    // Interface for handling item clicks
    private long lastPosition = -1;
    public interface OnExpenseClickListener {
        void onExpenseClick(ExpenseModel expense);
    }

    // Interface for handling long-press events
    public interface OnExpenseLongClickListener {
        void onExpenseLongClick();
    }

    public ExpenseViewAdapter(List<ExpenseModel> expenseList, OnExpenseClickListener clickListener, OnExpenseLongClickListener longClickListener, Context context) {
        this.expenseList = expenseList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.context = context;
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

        // Set text for views
        holder.storeNameTextView.setText(expense.getBillName() != null ? expense.getBillName() : "N/A");
        String date = formatDate(expense);
        holder.dateOfPurchaseTextView.setText(date);
        holder.totalPriceTextView.setText(String.valueOf("$" + expense.getBillAmount()));

        // Highlight matching text in bill_name
        if (searchQuery != null && !searchQuery.isEmpty()) {
            holder.storeNameTextView.setText(getHighlightedText(expense.getBillName(), searchQuery));
        } else {
            holder.storeNameTextView.setText(expense.getBillName());
        }

        // Toggle CheckBox visibility and state
        if (isMultiSelectionMode) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(selectedItems.contains(expense));
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (isMultiSelectionMode) {
                toggleSelection(expense);
                holder.checkBox.setChecked(selectedItems.contains(expense));
            } else {
                clickListener.onExpenseClick(expense);
            }
        });

        // Handle long-click to activate multi-selection
        holder.itemView.setOnLongClickListener(v -> {
            if (!isMultiSelectionMode) {
                isMultiSelectionMode = true;
                longClickListener.onExpenseLongClick();
                toggleSelection(expense);
                notifyDataSetChanged();
            }
            return true;
        });

        setAnimation(holder.itemView,position);

//        // Handle CheckBox click
//        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                if (!selectedItems.contains(expense)) {
//                    selectedItems.add(expense);
//                }
//            } else {
//                selectedItems.remove(expense);
//            }
//        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        // Apply the animation only if the view is being bound for the first time
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
            animation.setStartOffset(position * 30); // Delay each item by 100ms * position
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    private void toggleSelection(ExpenseModel expense) {
        if (selectedItems.contains(expense)) {
            selectedItems.remove(expense);
        } else {
            selectedItems.add(expense);
        }
        notifyDataSetChanged();
    }
    public void clearSelection() {
        selectedItems.clear();
        isMultiSelectionMode = false;
        notifyDataSetChanged();
    }

    public void updateData(List<ExpenseModel> newExpenses, String query) {
        this.expenseList = newExpenses;
        this.searchQuery = query; // Update the current query
        notifyDataSetChanged();
    }
    private Spannable getHighlightedText(String fullText, String query) {
        Spannable spannable = new SpannableString(fullText);
        int start = fullText.toLowerCase().indexOf(query.toLowerCase());
        if (start >= 0) {
            int end = start + query.length();
            spannable.setSpan(
                    new ForegroundColorSpan(context.getResources().getColor(R.color.buttonblue)), // Replace with your highlight color
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return spannable;
    }

    public List<ExpenseModel> getSelectedItems() {
        return selectedItems;
    }
    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView storeNameTextView, dateOfPurchaseTextView, totalPriceTextView;
        CheckBox checkBox;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            storeNameTextView = itemView.findViewById(R.id.storeNameTextView);
            dateOfPurchaseTextView = itemView.findViewById(R.id.dateOfPurchaseTextView);
            totalPriceTextView = itemView.findViewById(R.id.priceTextView);
            checkBox = itemView.findViewById(R.id.expenseCheckBox);
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
