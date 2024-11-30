package com.example.smartgrocerytracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.utils.ReviewGroceryItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_screen);

        TextView storeName = findViewById(R.id.store_name);
        TextView storeAddress = findViewById(R.id.store_address);
        TextView totalAmount = findViewById(R.id.total_amount);
        RecyclerView itemList = findViewById(R.id.item_list);

        // Retrieve data from intent
        String storeNameStr = getIntent().getStringExtra("storeName");
        String storeAddressStr = getIntent().getStringExtra("storeAddress");
        double totalAmountValue = getIntent().getDoubleExtra("totalAmount", 0);
        List<GroceryItemModel> groceryItems = (ArrayList<GroceryItemModel>) getIntent().getSerializableExtra("groceryItems");

        // Set data to views
        storeName.setText(storeNameStr);
        storeAddress.setText(storeAddressStr);
        totalAmount.setText(String.format("Total Amount: $%.2f", totalAmountValue));


        if (groceryItems != null && !groceryItems.isEmpty()) {
        // Set up RecyclerView
        ReviewGroceryItemAdapter adapter = new ReviewGroceryItemAdapter(groceryItems);
        itemList.setLayoutManager(new LinearLayoutManager(this));
        itemList.setAdapter(adapter);
        } else {
            Log.e("ReviewActivity", "Grocery items list is null or empty.");
        }
    }
}