// File: SearchResultsAdapter.java
package com.example.smartgrocerytracker.ui.globalSearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.ModelClass.BillInfo;
import com.example.smartgrocerytracker.ModelClass.GroceryItem;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(GroceryItem groceryItem, int position);
    }

    public static final int VIEW_TYPE_BILL_NAME = 0;
    public static final int VIEW_TYPE_GROCERY_NAME = 1;
    private static final int VIEW_TYPE_CATEGORY = 2;

    private int currentViewType = VIEW_TYPE_BILL_NAME; // Default view type
    List<Object> data = new ArrayList<>();
    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;

    // Updated constructor to accept both listeners
    public SearchResultsAdapter(OnItemClickListener clickListener, OnItemLongClickListener longClickListener) {
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void setViewType(int viewType) {
        this.currentViewType = viewType;
        notifyDataSetChanged();
    }

    public void setData(List<?> data) {
        this.data = new ArrayList<>(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return currentViewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_BILL_NAME) {
            View view = inflater.inflate(R.layout.grocery_item_row, parent, false);
            return new BillNameViewHolder(view);
        } else if (viewType == VIEW_TYPE_GROCERY_NAME) {
            View view = inflater.inflate(R.layout.item_year_search, parent, false);
            return new GroceryNameViewHolder(view);
        } else { // VIEW_TYPE_CATEGORY
            View view = inflater.inflate(R.layout.item_year_search, parent, false);
            return new CategoryViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = data.get(position);

        if (holder instanceof BillNameViewHolder && item instanceof BillInfo) {
            ((BillNameViewHolder) holder).bind((BillInfo) item, clickListener);
        } else if (holder instanceof GroceryNameViewHolder && item instanceof GroceryItem) {
            ((GroceryNameViewHolder) holder).bind((GroceryItem) item, clickListener, longClickListener, position);
        } else if (holder instanceof CategoryViewHolder && item instanceof GroceryItem) {
            ((CategoryViewHolder) holder).bind((GroceryItem) item, clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // ViewHolder for BillInfo
    static class BillNameViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView, priceView, categoryView, quantityView;

        public BillNameViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_name);
            priceView = itemView.findViewById(R.id.price);
            categoryView = itemView.findViewById(R.id.category);
            quantityView = itemView.findViewById(R.id.quantity);
        }

        void bind(BillInfo billInfo, OnItemClickListener listener) {
            textView.setText(billInfo.getBillName());
            priceView.setText("- $" + billInfo.getBillAmount());
            quantityView.setText("Qty: " + billInfo.getTotalQuantity());
            categoryView.setText(billInfo.getDescription());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(billInfo);
                }
            });
        }
    }

    // ViewHolder for GroceryName
    static class GroceryNameViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView, categoryView, priceView, quantityView, billNameView, BillQuantityView, totalPriceView, DateView;

        public GroceryNameViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_name_global);
            categoryView = itemView.findViewById(R.id.item_category_global);
            priceView = itemView.findViewById(R.id.item_price_global);
            quantityView = itemView.findViewById(R.id.item_quantity_global);
            billNameView = itemView.findViewById(R.id.bill_name_global);
            BillQuantityView = itemView.findViewById(R.id.total_quantity_global);
            totalPriceView = itemView.findViewById(R.id.bill_price_global);
            DateView = itemView.findViewById(R.id.date_of_bill_global);
        }

        void bind(GroceryItem groceryItem, OnItemClickListener listener, OnItemLongClickListener longClickListener, int position) {
            textView.setText(groceryItem.getItemName());
            categoryView.setText(groceryItem.getCategory().toUpperCase());
            priceView.setText("$" + groceryItem.getPrice());
            quantityView.setText("Qty: " + groceryItem.getQuantity());
            billNameView.setText(groceryItem.getBillName());
            BillQuantityView.setText("Bill Qty: " + groceryItem.getTotalQuantity());
            totalPriceView.setText("$" + groceryItem.getBillAmount());
            DateView.setText(groceryItem.getDateOfPurchase());

            // Handle regular click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(groceryItem);
                }
            });

            // Handle long click
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(groceryItem, position);
                    return true; // Indicate that the event was handled
                }
                return false;
            });
        }
    }

    // ViewHolder for Category (uses GroceryItem data)
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView, categoryView, priceView, quantityView, billNameView, BillQuantityView, totalPriceView, DateView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_name_global);
            categoryView = itemView.findViewById(R.id.item_category_global);
            priceView = itemView.findViewById(R.id.item_price_global);
            quantityView = itemView.findViewById(R.id.item_quantity_global);
            billNameView = itemView.findViewById(R.id.bill_name_global);
            BillQuantityView = itemView.findViewById(R.id.total_quantity_global);
            totalPriceView = itemView.findViewById(R.id.bill_price_global);
            DateView = itemView.findViewById(R.id.date_of_bill_global);
        }

        void bind(GroceryItem groceryItem, OnItemClickListener listener) {
            textView.setText(groceryItem.getItemName());
            categoryView.setText(groceryItem.getCategory().toUpperCase());
            priceView.setText("$" + groceryItem.getPrice());
            quantityView.setText("Qty: " + groceryItem.getQuantity());

            billNameView.setText(groceryItem.getBillName());
            BillQuantityView.setText("Bill Qty: " + groceryItem.getTotalQuantity());
            totalPriceView.setText("$" + groceryItem.getBillAmount());
            DateView.setText(groceryItem.getDateOfPurchase());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(groceryItem);
                }
            });
        }
    }
}