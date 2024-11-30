package com.example.smartgrocerytracker.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import android.widget.TextView;

import java.util.List;

public class ReviewGroceryItemAdapter extends RecyclerView.Adapter<ReviewGroceryItemAdapter.ViewHolder> {

    private List<GroceryItemModel> groceryItemList;

    public ReviewGroceryItemAdapter(List<GroceryItemModel> groceryItemList) {
        this.groceryItemList = groceryItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item_grocery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroceryItemModel item = groceryItemList.get(position);
        holder.itemName.setText(item.getItemName());
        holder.price.setText(String.format("$%.2f", item.getPrice()));
        holder.category.setText(item.getCategory());
    }

    @Override
    public int getItemCount() {
        return groceryItemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, price, category;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
            category = itemView.findViewById(R.id.item_category);
        }
    }
}
