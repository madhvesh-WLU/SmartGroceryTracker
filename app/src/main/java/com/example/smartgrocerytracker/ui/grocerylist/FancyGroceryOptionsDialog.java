package com.example.smartgrocerytracker.ui.grocerylist;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static com.example.smartgrocerytracker.utils.MediaUtils.CAMERA_REQUEST_CODE;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.uploadImage;
import com.example.smartgrocerytracker.utils.MediaUtils;

import java.io.File;
import java.util.Objects;

public class FancyGroceryOptionsDialog extends DialogFragment {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 102;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private RequestQueue requestQueue;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_grocery_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View dialogContent = view.findViewById(R.id.dialog_content);
        requestQueue = Volley.newRequestQueue(requireContext());
        dialogContent.setScaleX(0f);
        dialogContent.setScaleY(0f);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(dialogContent, "scaleX", 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(dialogContent, "scaleY", 1f);
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        String budgetId = sharedPreferences.getString("budget_id", null);

        // Manual receipt option
        view.findViewById(R.id.manual_receipt_option).setOnClickListener(v -> {
            if (!Objects.equals(budgetId, "null")) {
                BillInputDialogFragment billInputDialog = new BillInputDialogFragment();
                billInputDialog.show(getParentFragmentManager(), "BillInputDialog");
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Please select Budget", Toast.LENGTH_LONG).show();
            }
        });

        // Smart OCR option
        view.findViewById(R.id.smart_ocr_option).setOnClickListener(v -> {
            if (!Objects.equals(budgetId, "null")) {
                showCameraOrGalleryChooser();
            } else {
                Toast.makeText(requireContext(), "Please select Budget", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Show a chooser dialog to select between camera and gallery.
     */
    private void showCameraOrGalleryChooser() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        startActivityForResult(chooserIntent, CAMERA_PERMISSION_CODE);
    }

    // Handle the results in onActivityResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(requireContext(), "Items successfully stored!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle Retake: Open camera again
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }

        if (requestCode == CAMERA_PERMISSION_CODE && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                Uri imageUri = data.getData();
                handleImageFromGallery(imageUri);
            } else if (data.getExtras() != null) {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                handleImageFromCamera(capturedImage);
            } else {
                Toast.makeText(requireContext(), "No image selected or captured", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handle image selected from the gallery.
     *
     * @param imageUri URI of the selected image.
     */
    private void handleImageFromGallery(Uri imageUri) {
        File imageFile = MediaUtils.getFileFromUri(imageUri,requireContext());
        if (imageFile != null && imageFile.exists()) {
            uploadImage.uploadImageToGeminiAI(requireContext(), imageFile, requestQueue);
        } else {
            Toast.makeText(requireContext(), "Failed to process selected image.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handle image captured using the camera.
     *
     * @param bitmap Bitmap of the captured image.
     */
    private void handleImageFromCamera(Bitmap bitmap) {
        File imageFile = MediaUtils.convertBitmapToFile(requireContext(), bitmap, "captured_image.jpg");
        if (imageFile != null) {
            uploadImage.uploadImageToGeminiAI(requireContext(), imageFile, requestQueue);
        } else {
            Toast.makeText(requireContext(), "Failed to process captured image.", Toast.LENGTH_SHORT).show();
        }
    }
}