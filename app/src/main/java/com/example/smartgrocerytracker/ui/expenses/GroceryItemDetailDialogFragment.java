package com.example.smartgrocerytracker.ui.expenses;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;

import java.io.Serializable;

public class GroceryItemDetailDialogFragment extends DialogFragment {

    private static final String ARG_GROCERY_ITEM = "grocery_item";

    public static GroceryItemDetailDialogFragment newInstance(GroceryItemModel groceryItem) {
        GroceryItemDetailDialogFragment fragment = new GroceryItemDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GROCERY_ITEM, (Serializable) groceryItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        GroceryItemModel item = (GroceryItemModel) getArguments().getSerializable(ARG_GROCERY_ITEM);

        // Use DialogFragment to show item details
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.grocery_item_detail_modal);

        TextView itemNameTextView = dialog.findViewById(R.id.item_name);
        TextView categoryTextView = dialog.findViewById(R.id.category);
        TextView quantityTextView = dialog.findViewById(R.id.quantity);
        TextView priceTextView = dialog.findViewById(R.id.price);

        itemNameTextView.setText(item.getItemName());
        categoryTextView.setText(item.getCategory());
        quantityTextView.setText("Quantity: " + item.getQuantity());
        priceTextView.setText("$" + item.getPrice());

        return dialog;
    }
}
