package com.example.smartgrocerytracker.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.example.smartgrocerytracker.Config;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.ui.ReviewActivity;
import com.example.smartgrocerytracker.utils.ScreenLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class uploadImage {

    public static void uploadImageToGeminiAI(Context context, File imageFile, RequestQueue requestQueue) {
        String uploadUrl = Config.SCAN_IMAGE_DATA; // Replace with your backend URL

        // Initialize loader
        ScreenLoader loader = new ScreenLoader(context);
        ViewGroup rootView = ((Activity) context).findViewById(android.R.id.content);
        loader.show(rootView); // Show loader

        Log.d("UploadLog", "File Path: " + imageFile.getAbsolutePath());
        Log.d("UploadLog", "File Name: " + imageFile.getName());
        Log.d("UploadLog", "File Size: " + imageFile.length() + " bytes");

        MultipartRequest multipartRequest = new MultipartRequest(
                uploadUrl,
                imageFile,
                "file",
                context,
                response -> {
                    loader.hide(rootView);
                    Log.i("UploadLog", "Response: " + response.toString());
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String storeName = data.getString("store_name");
                        String storeAddress = data.optString("store_address","asdasd");
                        double totalAmount = data.getDouble("total_amount");

                        // Extract grocery items
                        JSONArray itemsArray = data.getJSONArray("items");
                        List<GroceryItemModel> groceryItems = new ArrayList<>();
                        for (int i = 0; i < itemsArray.length(); i++) {
                            JSONObject item = itemsArray.getJSONObject(i);
                            groceryItems.add(new GroceryItemModel(
                                    null,
                                    item.getString("item_name"),
                                    null,
                                    item.getDouble("quantity"),
                                    item.getString("category"),
                                    item.getDouble("price"),
                                    false
                            ));
                        }

                        // Pass data to ReviewActivity
                        Intent intent = new Intent(context, ReviewActivity.class);
                        intent.putExtra("storeName", storeName);
                        intent.putExtra("storeAddress", storeAddress);
                        intent.putExtra("totalAmount", totalAmount);
                        intent.putExtra("groceryItems", (ArrayList<GroceryItemModel>) groceryItems);

                        ((Activity) context).startActivityForResult(intent, 101);

                    } catch (JSONException e) {
                        Log.e("UploadLog", "Error parsing JSON response: " + e.getMessage());
                        Toast.makeText(context, "Error processing the server response.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loader.hide(rootView); // Hide loader on error
                    Log.e("UploadLog", "Error uploading image: " + error.toString());
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String body = new String(error.networkResponse.data);
                        Log.e("UploadLog", body);
                        Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
                    } else if (error.networkResponse != null && error.networkResponse.statusCode == 429) {
                    } else {
                        Toast.makeText(context, "An unexpected error occurred.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(multipartRequest);
    }
}