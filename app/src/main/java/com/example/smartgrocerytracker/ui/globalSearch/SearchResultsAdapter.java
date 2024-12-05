// File: SearchResultsAdapter.java
package com.example.smartgrocerytracker.ui.globalSearch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.ModelClass.BillInfo;
import com.example.smartgrocerytracker.ModelClass.GroceryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Object item);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Object item, int position);
    }
    private static final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final int VIEW_TYPE_BILL_NAME = 0;
    public static final int VIEW_TYPE_GROCERY_NAME = 1;
    private static final int VIEW_TYPE_CATEGORY = 2;

    private int currentViewType = VIEW_TYPE_BILL_NAME;
    List<Object> data = new ArrayList<>();
    private final OnItemClickListener clickListener;
    private final OnItemLongClickListener longClickListener;

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
            ((BillNameViewHolder) holder).bind((BillInfo) item, clickListener,longClickListener, position);
        } else if (holder instanceof GroceryNameViewHolder && item instanceof GroceryItem) {
            try {
                ((GroceryNameViewHolder) holder).bind((GroceryItem) item, clickListener, longClickListener, position);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else if (holder instanceof CategoryViewHolder && item instanceof GroceryItem) {
            ((CategoryViewHolder) holder).bind((GroceryItem) item, clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    static class BillNameViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView, priceView, categoryView, quantityView;
        CheckBox checkBox;
        public BillNameViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_name);
            priceView = itemView.findViewById(R.id.price);
            categoryView = itemView.findViewById(R.id.category);
            quantityView = itemView.findViewById(R.id.quantity);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            checkBox.setVisibility(View.GONE);
        }

        void bind(BillInfo billInfo, OnItemClickListener listener,OnItemLongClickListener longClickListener,int position) {
            textView.setText(billInfo.getBillName());
            priceView.setText("- $" + billInfo.getBillAmount());
            quantityView.setText("Qty: " + billInfo.getTotalQuantity());
            categoryView.setText(billInfo.getDescription());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(billInfo);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(billInfo, position);
                }
                return true;
            });
        }
    }


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

        void bind(GroceryItem groceryItem, OnItemClickListener listener, OnItemLongClickListener longClickListener, int position) throws ParseException {
            textView.setText(groceryItem.getItemName());
            categoryView.setText(groceryItem.getCategory().toUpperCase());
            priceView.setText("$" + groceryItem.getPrice());
            quantityView.setText("Qty: " + groceryItem.getQuantity());
            billNameView.setText(groceryItem.getBillName());
            BillQuantityView.setText("Bill Qty: " + groceryItem.getTotalQuantity());
            totalPriceView.setText("$" + groceryItem.getBillAmount());
            Date parsedDate = isoFormat.parse(groceryItem.getDateOfPurchase());
            String formattedDate = isoFormat.format(parsedDate);
            DateView.setText(formattedDate);


            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(groceryItem);
                }
            });


            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onItemLongClick(groceryItem, position);
                    return true;
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