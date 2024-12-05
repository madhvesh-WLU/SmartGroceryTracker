package com.example.smartgrocerytracker.ui;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.ScanningAddExpenseServices;
import com.example.smartgrocerytracker.utils.ReviewGroceryItemAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class ReviewActivity extends AppCompatActivity {
    public static final int RESULT_RETAKE = 1;
    public static final int RESULT_SUBMIT = 2;

    private TextInputEditText storeName;
    private TextView storeAddress;
    private TextView totalAmount;
    private RecyclerView itemList;
    private Button btnRetake;
    private Button btnSubmit;
    private TextInputEditText dateOfPurchase;
    private TextInputEditText description;
    private String  TAG = "ReviewActivity";

    private String selectedDate = null;
    private static final TimeZone TIME_ZONE_EST = TimeZone.getTimeZone("America/Toronto");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_screen);

        storeName = findViewById(R.id.store_name);
        storeAddress = findViewById(R.id.store_address);
        totalAmount = findViewById(R.id.total_amount);
        itemList = findViewById(R.id.item_list);
        btnRetake = findViewById(R.id.btn_retake);
        btnSubmit = findViewById(R.id.btn_submit);
        dateOfPurchase = findViewById(R.id.date_of_purchase);
        description = findViewById(R.id.description);

        String storeNameStr = getIntent().getStringExtra("storeName");
        String storeAddressStr = getIntent().getStringExtra("storeAddress");
        double totalAmountValue = getIntent().getDoubleExtra("totalAmount", 0);
        List<GroceryItemModel> groceryItems = (ArrayList<GroceryItemModel>) getIntent().getSerializableExtra("groceryItems");

        storeName.setText(storeNameStr);
        storeAddress.setText(storeAddressStr);
        totalAmount.setText(String.format("Total Amount: $%.2f", totalAmountValue));

        Calendar calendar = Calendar.getInstance(TIME_ZONE_EST);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TIME_ZONE_EST);

        selectedDate = dateFormat.format(calendar.getTime());
        dateOfPurchase.setText(selectedDate);

        dateOfPurchase.setOnClickListener(v -> {
            Calendar datePickerCalendar = Calendar.getInstance(TIME_ZONE_EST);
            int year = datePickerCalendar.get(Calendar.YEAR);
            int month = datePickerCalendar.get(Calendar.MONTH);
            int day = datePickerCalendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(ReviewActivity.this, (view, year1, month1, dayOfMonth) -> {
                // Set the selected date in the Calendar instance
                Calendar selectedCalendar = Calendar.getInstance(TIME_ZONE_EST);
                selectedCalendar.set(Calendar.YEAR, year1);
                selectedCalendar.set(Calendar.MONTH, month1);
                selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Format the selected date
                selectedDate = dateFormat.format(selectedCalendar.getTime());
                dateOfPurchase.setText(selectedDate);
            }, year, month, day);
            datePickerDialog.show();
        });

        if (groceryItems != null && !groceryItems.isEmpty()) {
            ReviewGroceryItemAdapter adapter = new ReviewGroceryItemAdapter(groceryItems);
            itemList.setLayoutManager(new LinearLayoutManager(this));
            itemList.setAdapter(adapter);
        } else {
            Log.e(TAG, "Grocery items list is null or empty.");
        }

        btnRetake.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        btnSubmit.setOnClickListener(v -> {
            localstoreCall(groceryItems,totalAmountValue);
        });
    }

    public void localstoreCall(List<GroceryItemModel> groceryItems,double totalAmountValue){
        SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        String budget_id = sharedPreferences.getString("budget_id", null);
        if (!Objects.equals(budget_id, "null")) {
            String storeNameInput = storeName.getText().toString().trim();
            String descriptionInput = description.getText().toString().trim();

            if (storeNameInput.isEmpty()) {
                Toast.makeText(this, "Please enter Store Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedDate == null) {
                Toast.makeText(this, "Please select Date of Purchase", Toast.LENGTH_SHORT).show();
                return;
            }
            if (descriptionInput.isEmpty()) {
                Toast.makeText(this, "Please enter Description", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare data for API call
            Map<String, Object> expenseDetails = new HashMap<>();
            expenseDetails.put("bill_name", storeNameInput);
            expenseDetails.put("date_of_purchase", selectedDate);
            expenseDetails.put("total_quantity", groceryItems.size());
            expenseDetails.put("bill_amount", totalAmountValue);
            expenseDetails.put("description", descriptionInput);
            expenseDetails.put("budget_id", budget_id);

            JSONArray itemsArray = new JSONArray();
            for (GroceryItemModel item : groceryItems) {
                JSONObject itemJson = new JSONObject();
                float quantity = Float.parseFloat(String.valueOf(item.getQuantity()));
                try {
                    itemJson.put("item_name", item.getItemName());
                    itemJson.put("quantity",quantity);
                    itemJson.put("category", item.getCategory());
                    itemJson.put("price", item.getPrice());
                    itemJson.put("purchased", item.isPurchased());
                    itemsArray.put(itemJson);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            expenseDetails.put("grocery_items", itemsArray);

            JSONObject jsonObject = new JSONObject(expenseDetails);

            ScanningAddExpenseServices.makeStoreItemsApiCall(
                    this,
                    jsonObject,
                    new ScanningAddExpenseServices.ApiCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(ReviewActivity.this, "Items stored successfully!", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(String errorMessage) {

                            Toast.makeText(ReviewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        } else {
            Toast.makeText(this, "Please select a Budget", Toast.LENGTH_LONG).show();
        }
    }


}