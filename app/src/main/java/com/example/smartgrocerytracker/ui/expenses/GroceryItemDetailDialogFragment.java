package com.example.smartgrocerytracker.ui.expenses;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.utils.LanguageUtil;

import java.io.Serializable;
import java.util.Objects;

public class GroceryItemDetailDialogFragment extends DialogFragment {

    private static final String ARG_GROCERY_ITEM = "grocery_item";

    public static GroceryItemDetailDialogFragment newInstance(GroceryItemModel groceryItem) {
        GroceryItemDetailDialogFragment fragment = new GroceryItemDetailDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GROCERY_ITEM, (Serializable) groceryItem);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        GroceryItemModel item = (GroceryItemModel) getArguments().getSerializable(ARG_GROCERY_ITEM);
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.grocery_item_detail_modal);

        TextView itemNameTextView = dialog.findViewById(R.id.item_name_value);
        TextView categoryTextView = dialog.findViewById(R.id.category_value);
        TextView quantityTextView = dialog.findViewById(R.id.quantity_value);
        TextView priceTextView = dialog.findViewById(R.id.price_value);
        TextView description = dialog.findViewById(R.id.description_value);
        ImageButton closeButton = dialog.findViewById(R.id.close_button);

        assert item != null;
        itemNameTextView.setText(item.getItemName());
        categoryTextView.setText(item.getCategory());
        quantityTextView.setText(" " + item.getQuantity());
        priceTextView.setText("$" + item.getPrice());
        //description.setText(item.getDescription());

        closeButton.setOnClickListener(v -> dismiss());
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        LanguageUtil.setLocale(context);
    }
}
