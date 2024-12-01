package com.example.smartgrocerytracker.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.ScanningAddExpenseServices;
import com.example.smartgrocerytracker.utils.ReviewGroceryItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReviewActivity extends AppCompatActivity {
    public static final int RESULT_RETAKE = 1;
    public static final int RESULT_SUBMIT = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_screen);

        TextView storeName = findViewById(R.id.store_name);
        TextView storeAddress = findViewById(R.id.store_address);
        TextView totalAmount = findViewById(R.id.total_amount);
        RecyclerView itemList = findViewById(R.id.item_list);
        Button btnRetake = findViewById(R.id.btn_retake);
        Button btnSubmit = findViewById(R.id.btn_submit);
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
        // Handle Retake button action
        btnRetake.setOnClickListener(v -> {
            setResult(RESULT_CANCELED); // Notify caller for retake
            finish(); // Finish activity and return to caller
        });
        SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        String budget_id = sharedPreferences.getString("budget_id", null);
        float billAmount = Float.parseFloat(String.valueOf(totalAmountValue));
        // Collect input data in a HashMap
        Map<String, Object> expenseDetails = new HashMap<>();
        expenseDetails.put("bill_name", storeNameStr);
        expenseDetails.put("date_of_purchase", "2024-11-29");
        expenseDetails.put("total_quantity", 5);
        expenseDetails.put("bill_amount", billAmount);
        expenseDetails.put("description", "Checking Scanning");
        expenseDetails.put("budget_id", budget_id);


            JSONArray itemsArray = new JSONArray();
            for (GroceryItemModel item : groceryItems) {
                JSONObject itemJson = new JSONObject();
                try {
                    itemJson.put("item_name", item.getItemName());
                    itemJson.put("quantity", item.getQuantity());
                    itemJson.put("category", item.getCategory());
                    itemJson.put("price", item.getPrice());
                    itemJson.put("purchased", item.isPurchased());
                    itemsArray.put(itemJson);
                }catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
//            requestPayload.put("items", itemsArray);
//        List<Map<String, Object>> groceryItemss = new ArrayList<>();
        expenseDetails.put("grocery_items", itemsArray);

        JSONObject jsonObject = new JSONObject(expenseDetails);

        btnSubmit.setOnClickListener(v -> {
            if (!Objects.equals(budget_id, "null")) {
            ScanningAddExpenseServices.makeStoreItemsApiCall(
                    this,
                    jsonObject,
                    new ScanningAddExpenseServices.ApiCallback() {
                        @Override
                        public void onSuccess() {
                            // Notify caller of success and finish activity
                            Toast.makeText(ReviewActivity.this, "Items stored successfully!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {
                            // Show error message to the user
                            Toast.makeText(ReviewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            }
            else{
            Toast.makeText(this,"Please select Budget",Toast.LENGTH_LONG).show();
            }
        });
    }
}