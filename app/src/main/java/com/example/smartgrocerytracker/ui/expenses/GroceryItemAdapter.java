package com.example.smartgrocerytracker.ui.expenses;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroceryItemAdapter extends RecyclerView.Adapter<GroceryItemAdapter.GroceryItemViewHolder> {

    private List<GroceryItemModel> groceryItemList;
    Context context;
    private Set<Integer> selectedPositions = new HashSet<>();
    // Constructor with context
    public GroceryItemAdapter(Context context, List<GroceryItemModel> groceryItemList) {
        this.context = context;  // Initialize context
        this.groceryItemList = groceryItemList;
    }

    @NonNull
    @Override
    public GroceryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grocery_item_row, parent, false);
        return new GroceryItemViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull GroceryItemViewHolder holder, int position) {
        GroceryItemModel item = groceryItemList.get(position);
        selectDeletePositions(holder,position);
        holder.itemNameTextView.setText(item.getItemName());
        holder.categoryTextView.setText(item.getCategory());
        holder.quantityTextView.setText("Quantity: " + item.getQuantity());
        holder.priceTextView.setText("$" + item.getPrice());

        holder.itemView.setOnClickListener(v -> {
            // Create a new instance of the DialogFragment and pass the item data
            GroceryItemDetailDialogFragment dialogFragment = GroceryItemDetailDialogFragment.newInstance(item);
            dialogFragment.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "grocery_item_details");
        });
    }




    @Override
    public int getItemCount() {
        return groceryItemList.size();
    }

    public void updateData(List<GroceryItemModel> newItems) {
        this.groceryItemList = newItems;
        notifyDataSetChanged();
    }
    // Method to handle long press and checkbox visibility
    private void selectDeletePositions(@NonNull GroceryItemViewHolder holder, int position) {
        // Set the checkbox visibility and item background color based on selection state
        if (selectedPositions.contains(position)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            holder.checkBox.setVisibility(View.VISIBLE);  // Show checkbox
            holder.checkBox.setChecked(true);  // Optionally check the checkbox
        } else {
//            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.checkBox.setVisibility(View.GONE);  // Hide checkbox
            holder.checkBox.setChecked(false);  // Uncheck the checkbox
        }

        // Long press listener to toggle selection and show/hide checkbox
        holder.itemView.setOnLongClickListener(v -> {
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position);
                holder.itemView.setBackgroundColor(Color.WHITE);
                holder.checkBox.setVisibility(View.GONE);  // Hide checkbox
            } else {
                selectedPositions.add(position);
                holder.itemView.setBackgroundColor(Color.LTGRAY);
                holder.checkBox.setVisibility(View.VISIBLE);  // Show checkbox
                holder.checkBox.setChecked(true);  // Optionally check the checkbox
            }
            notifyItemChanged(position);  // Notify item change for background color and checkbox visibility
            return true;
        });

        // Handle checkbox click to mark items for deletion
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Add to deletion list (this can be a custom list for deletion)
            } else {
                // Remove from deletion list
            }
        });
    }
    public void deleteSelectedItem() {
        if (!selectedPositions.isEmpty()) {
            List<Integer> sortedPositions = new ArrayList<>(selectedPositions);
            Collections.sort(sortedPositions, Collections.reverseOrder());

            for (int position : sortedPositions) {
                groceryItemList.remove(position);
            }
            selectedPositions.clear();

            notifyDataSetChanged();
        }
    }

    public static class GroceryItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameTextView, categoryTextView, quantityTextView, priceTextView;
        CheckBox checkBox;
        public GroceryItemViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name);
            categoryTextView = itemView.findViewById(R.id.category);
            quantityTextView = itemView.findViewById(R.id.quantity);
            priceTextView = itemView.findViewById(R.id.price);
            checkBox = itemView.findViewById(R.id.item_checkbox);
        }
    }
}
