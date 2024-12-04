//package com.example.smartgrocerytracker.ui.expenses;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.smartgrocerytracker.Config;
//import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
//import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
//import com.example.smartgrocerytracker.R;
//import com.example.smartgrocerytracker.utils.SecurePreferences;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class GroceryItemAdapter extends RecyclerView.Adapter<GroceryItemAdapter.GroceryItemViewHolder> {
//
//    private List<GroceryItemModel> groceryItemList;
//    Context context;
//    private Set<Integer> selectedPositions = new HashSet<>();
//    // Constructor with context
//    public GroceryItemAdapter(Context context, List<GroceryItemModel> groceryItemList) {
//        this.context = context;  // Initialize context
//        this.groceryItemList = groceryItemList;
//    }
//
//    @NonNull
//    @Override
//    public GroceryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.grocery_item_row, parent, false);
//        return new GroceryItemViewHolder(view);
//    }
//
//
//    @Override
//    public void onBindViewHolder(@NonNull GroceryItemViewHolder holder, int position) {
//        GroceryItemModel item = groceryItemList.get(position);
//        selectDeletePositions(holder,position);
//        holder.itemNameTextView.setText(item.getItemName());
//        holder.categoryTextView.setText(item.getCategory());
//        holder.quantityTextView.setText("Quantity: " + item.getQuantity());
//        holder.priceTextView.setText("$" + item.getPrice());
//
//        holder.itemView.setOnClickListener(v -> {
//            GroceryItemDetailDialogFragment dialogFragment = GroceryItemDetailDialogFragment.newInstance(item);
//            dialogFragment.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "grocery_item_details");
//        });
//    }
//
//
//
//
//    @Override
//    public int getItemCount() {
//        return groceryItemList.size();
//    }
//
//    public void updateData(List<GroceryItemModel> newItems) {
//        this.groceryItemList = newItems;
//        notifyDataSetChanged();
//    }
//
//
//    // Method to handle long press and checkbox visibility
//    private void selectDeletePositions(@NonNull GroceryItemViewHolder holder, int position) {
//        // Set the checkbox visibility and item background color based on selection state
//        if (selectedPositions.contains(position)) {
//            holder.itemView.setBackgroundColor(Color.LTGRAY);
//            holder.checkBox.setVisibility(View.VISIBLE);  // Show checkbox
//            holder.checkBox.setChecked(true);  // Optionally check the checkbox
//        } else {
////            holder.itemView.setBackgroundColor(Color.WHITE);
//            holder.checkBox.setVisibility(View.GONE);  // Hide checkbox
//            holder.checkBox.setChecked(false);  // Uncheck the checkbox
//        }
//
//        // Long press listener to toggle selection and show/hide checkbox
//        holder.itemView.setOnLongClickListener(v -> {
//            if (selectedPositions.contains(position)) {
//                selectedPositions.remove(position);
//                holder.itemView.setBackgroundColor(Color.WHITE);
//                holder.checkBox.setVisibility(View.GONE);  // Hide checkbox
//            } else {
//                selectedPositions.add(position);
//                holder.itemView.setBackgroundColor(Color.LTGRAY);
//                holder.checkBox.setVisibility(View.VISIBLE);  // Show checkbox
//                holder.checkBox.setChecked(true);  // Optionally check the checkbox
//            }
//            notifyItemChanged(position);  // Notify item change for background color and checkbox visibility
//            return true;
//        });
//
//        // Handle checkbox click to mark items for deletion
//        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                // Add to deletion list (this can be a custom list for deletion)
//            } else {
//                // Remove from deletion list
//            }
//        });
//    }
//    public void deleteSelectedItem(String expenseId) {
//        try {
//          if (!selectedPositions.isEmpty()) {
//            JSONArray groceryIdsArray = new JSONArray();
//            for (int position : selectedPositions) {
//                String groceryId = groceryItemList.get(position).getItemId();
//                groceryIdsArray.put(groceryId);
//            }
//            JSONObject payload = new JSONObject();
//                payload.put("grocery_ids", groceryIdsArray);
//                sendDeleteRequestToAPI(payload,expenseId);
//          }
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void sendDeleteRequestToAPI(JSONObject payload,String expenseId) {
//        RequestQueue queue = Volley.newRequestQueue((androidx.fragment.app.FragmentActivity) context);
//        String token = SecurePreferences.getAuthToken(context);
//
//        String url = Config.DELETE_GROCERY_ITEM_URL + expenseId;
//        final String TAG = "fetchExpensesServices";
//        Log.i("asd", payload.toString());
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, payload,
//                response -> {
//                    // Handle the successful response
//                    Toast.makeText((androidx.fragment.app.FragmentActivity) context, "Items deleted successfully!", Toast.LENGTH_SHORT).show();
//                    removeItemsFromList();
//                },
//                error -> {
//                    // Handle errors
//                    Toast.makeText((androidx.fragment.app.FragmentActivity) context, "Failed to delete items!", Toast.LENGTH_SHORT).show();
//                    Log.e(TAG, "Volley error: " + error.getMessage());
//                    error.printStackTrace();
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + token);
//                return headers;
//            }
//        };
//        queue.add(request);
//    }
//
//        // Method to update local list after deletion
//    private void removeItemsFromList() {
//        // Sort and remove selected items from the list
//        List<Integer> sortedPositions = new ArrayList<>(selectedPositions);
//        Collections.sort(sortedPositions, Collections.reverseOrder());
//
//        for (int position : sortedPositions) {
//            groceryItemList.remove(position);
//        }
//        selectedPositions.clear();
//
//        notifyDataSetChanged(); // Refresh the RecyclerView
//    }
//
//    public static class GroceryItemViewHolder extends RecyclerView.ViewHolder {
//
//        TextView itemNameTextView, categoryTextView, quantityTextView, priceTextView;
//        CheckBox checkBox;
//        public GroceryItemViewHolder(View itemView) {
//            super(itemView);
//            itemNameTextView = itemView.findViewById(R.id.item_name);
//            categoryTextView = itemView.findViewById(R.id.category);
//            quantityTextView = itemView.findViewById(R.id.quantity);
//            priceTextView = itemView.findViewById(R.id.price);
//            checkBox = itemView.findViewById(R.id.item_checkbox);
//        }
//    }
//}


package com.example.smartgrocerytracker.ui.expenses;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.utils.SecurePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GroceryItemAdapter extends RecyclerView.Adapter<GroceryItemAdapter.GroceryItemViewHolder> {

    private List<GroceryItemModel> groceryItemList;
    private Context context;
    private Set<Integer> selectedPositions = new HashSet<>();
    private String searchQuery = ""; // To store the current search query for highlighting

    private long lastPosition = -1;
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

        // Highlight matching text in item_name
        if (searchQuery != null && !searchQuery.isEmpty()) {
            holder.itemNameTextView.setText(getHighlightedText(item.getItemName(), searchQuery));
        } else {
            holder.itemNameTextView.setText(item.getItemName());
        }

        holder.categoryTextView.setText(item.getCategory());
        holder.quantityTextView.setText("Quantity: " + item.getQuantity());
        holder.priceTextView.setText("-$" + item.getPrice());

        selectDeletePositions(holder, position);

        holder.itemView.setOnClickListener(v -> {
            GroceryItemDetailDialogFragment dialogFragment = GroceryItemDetailDialogFragment.newInstance(item);
            dialogFragment.show(((androidx.fragment.app.FragmentActivity) context).getSupportFragmentManager(), "grocery_item_details");
        });

        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // Apply the animation only if the view is being bound for the first time
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
            animation.setStartOffset(position * 100); // Delay each item by 100ms * position
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    @Override
    public int getItemCount() {
        return groceryItemList.size();
    }

    /**
     * Updates the adapter's data and the search query, then refreshes the RecyclerView.
     *
     * @param newItems The new list of grocery items.
     * @param query    The current search query for highlighting.
     */
    public void updateData(List<GroceryItemModel> newItems, String query) {
        this.groceryItemList = newItems;
        this.searchQuery = query; // Update the current query
        notifyDataSetChanged();
    }

    /**
     * Highlights the matching query text within the full text.
     *
     * @param fullText The complete text (e.g., item name).
     * @param query    The search query to highlight.
     * @return A Spannable with the matching query highlighted.
     */
    private Spannable getHighlightedText(String fullText, String query) {
        Spannable spannable = new SpannableString(fullText);
        String lowerFullText = fullText.toLowerCase();
        String lowerQuery = query.toLowerCase();
        int start = lowerFullText.indexOf(lowerQuery);
        if (start >= 0) {
            int end = start + query.length();
            // You can define a highlight color in your colors.xml
            int highlightColor = context.getResources().getColor(R.color.buttonblue, null);
            spannable.setSpan(
                    new ForegroundColorSpan(highlightColor), // Highlight color
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return spannable;
    }

    // Method to handle long press and checkbox visibility
    private void selectDeletePositions(@NonNull GroceryItemViewHolder holder, int position) {
        // Set the checkbox visibility and item background color based on selection state
        if (selectedPositions.contains(position)) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
            holder.checkBox.setVisibility(View.VISIBLE);  // Show checkbox
            holder.checkBox.setChecked(true);  // Optionally check the checkbox
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
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

    public void deleteSelectedItem(String expenseId) {
        try {
            if (!selectedPositions.isEmpty()) {
                JSONArray groceryIdsArray = new JSONArray();
                for (int position : selectedPositions) {
                    String groceryId = groceryItemList.get(position).getItemId();
                    groceryIdsArray.put(groceryId);
                }
                JSONObject payload = new JSONObject();
                payload.put("grocery_ids", groceryIdsArray);
                sendDeleteRequestToAPI(payload, expenseId);
                Log.i("Delete array list",payload.toString());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDeleteRequestToAPI(JSONObject payload, String expenseId) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String token = SecurePreferences.getAuthToken(context);

        String url = Config.DELETE_GROCERY_ITEM_URL + expenseId;
        final String TAG = "fetchExpensesServices";
        Log.i("DeleteRequestPayload", payload.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, payload,
                response -> {
                    // Handle the successful response
                    Toast.makeText(context, "Items deleted successfully!", Toast.LENGTH_SHORT).show();
                    removeItemsFromList();
                },
                error -> {
                    // Handle errors
                    Toast.makeText(context, "Failed to delete items!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Volley error: " + error.toString());

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        Log.e(TAG, "Error response body: " + body);
                    }
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    // Directly use the payload's string representation
                    return payload.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        queue.add(request);
    }
    // Method to update local list after deletion
    private void removeItemsFromList() {
        // Sort and remove selected items from the list
        List<Integer> sortedPositions = new ArrayList<>(selectedPositions);
        Collections.sort(sortedPositions, Collections.reverseOrder());

        for (int position : sortedPositions) {
            groceryItemList.remove(position);
        }
        selectedPositions.clear();

        notifyDataSetChanged(); // Refresh the RecyclerView
    }

    public static class GroceryItemViewHolder extends RecyclerView.ViewHolder {

        TextView itemNameTextView, categoryTextView, quantityTextView, priceTextView;
        CheckBox checkBox;

        public GroceryItemViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.item_name); // Update ID based on your layout
            categoryTextView = itemView.findViewById(R.id.category);
            quantityTextView = itemView.findViewById(R.id.quantity);
            priceTextView = itemView.findViewById(R.id.price);
            checkBox = itemView.findViewById(R.id.item_checkbox);
        }
    }
}