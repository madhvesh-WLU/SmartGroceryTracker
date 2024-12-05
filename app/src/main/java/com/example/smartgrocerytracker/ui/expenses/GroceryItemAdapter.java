package com.example.smartgrocerytracker.ui.expenses;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;

import java.util.ArrayList;
import java.util.List;

public class GroceryItemAdapter extends RecyclerView.Adapter<GroceryItemAdapter.GroceryItemViewHolder> {

    private List<GroceryItemModel> groceryItemList;
    private Context context;
    private String searchQuery = "";
    private boolean isMultiSelectionMode = false;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private final List<GroceryItemModel> selectedItems = new ArrayList<>();
    private long lastPosition = -1;
    public interface OnGroceryLongClickListener {
        void onGroceryLongClick();
    }

    private final OnGroceryLongClickListener longClickListener;

    public GroceryItemAdapter(Context context, List<GroceryItemModel> groceryItemList, OnGroceryLongClickListener longClickListener) {
        this.context = context;
        this.groceryItemList = groceryItemList;
        this.longClickListener = longClickListener;
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
        if (searchQuery != null && !searchQuery.isEmpty()) {
            holder.itemNameTextView.setText(getHighlightedText(item.getItemName(), searchQuery));
        } else {
            holder.itemNameTextView.setText(item.getItemName());
        }

        holder.categoryTextView.setText(item.getCategory());
        holder.quantityTextView.setText("Quantity: " + item.getQuantity());
        holder.priceTextView.setText("$" + String.format("%.2f", item.getPrice()));

        if (position == selectedPosition) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }


        holder.itemView.setOnClickListener(v -> {
            if (!isMultiSelectionMode) {
                openItemDetails(item);
            }
        });
        ;
        holder.itemView.setOnLongClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = (selectedPosition == position) ? RecyclerView.NO_POSITION : position;

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (selectedPosition != RecyclerView.NO_POSITION) {
                selectedItems.clear();
                selectedItems.add(groceryItemList.get(selectedPosition));
            } else {
                selectedItems.clear();
            }

            longClickListener.onGroceryLongClick();
            return true;
        });

        holder.checkBox.setOnClickListener(v -> {
            if (selectedPosition == position) {
                selectedPosition = RecyclerView.NO_POSITION;
                selectedItems.clear();
                notifyItemChanged(position);
            }
        });
        setAnimation(holder.itemView,position);
    }



    private void setAnimation(View viewToAnimate, int position) {

        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
            animation.setStartOffset(position * 30);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    private void openItemDetails(GroceryItemModel item) {
        GroceryItemDetailDialogFragment dialogFragment = GroceryItemDetailDialogFragment.newInstance(item);
        dialogFragment.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "grocery_item");
    }

    @Override
    public int getItemCount() {
        return groceryItemList.size();
    }

    public void updateData(List<GroceryItemModel> newItems, String query) {
        this.groceryItemList = newItems;
        this.searchQuery = query;
        notifyDataSetChanged();;
    }

    private Spannable getHighlightedText(String fullText, String query) {
        Spannable spannable = new SpannableString(fullText);
        String lowerFullText = fullText.toLowerCase();
        String lowerQuery = query.toLowerCase();
        int start = lowerFullText.indexOf(lowerQuery);
        if (start >= 0) {
            int end = start + query.length();
            int highlightColor = context.getResources().getColor(R.color.buttonblue, null);
            spannable.setSpan(
                    new ForegroundColorSpan(highlightColor),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return spannable;
    }

    public List<GroceryItemModel> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void clearSelection() {
        int previousPosition = selectedPosition;
        selectedPosition = RecyclerView.NO_POSITION;
        selectedItems.clear();
        notifyItemChanged(previousPosition);
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