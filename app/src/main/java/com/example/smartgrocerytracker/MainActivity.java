package com.example.smartgrocerytracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.databinding.ActivityMainBinding;
import com.example.smartgrocerytracker.services.fetchUserServices;
import com.example.smartgrocerytracker.services.uploadImage;
import com.example.smartgrocerytracker.ui.FullScreenImageActivity;
import com.example.smartgrocerytracker.ui.ReviewActivity;
import com.example.smartgrocerytracker.utils.BudgetDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 102;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.appBarMain.toolbar);
        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);
        // Set up navigation for DrawerLayout and BottomNavigationView
        setupNavigation();

        // Set up button to navigate to GroceryListFragment
        TextView groceryListButton = findViewById(R.id.grocerylist);
        groceryListButton.setOnClickListener(v -> navigateToDestination(R.id.nav_expense_fragment, true));

        // Fetch user details if needed
        handleFetchUserDetails();

        // Set up FAB for camera
//        binding.appBarMain.fab.setOnClickListener(view -> {
//            // Check for camera permissions
//            if (checkCameraPermission()) {
//                openCameraForScanning();
//            } else {
//                requestCameraPermission();
//            }
////            GifImageView gifImageView = findViewById(R.id.gifimageview);
////            gifImageView.setImageResource(R.drawable.giphy);
//        });


        binding.appBarMain.fab.setOnClickListener(view -> {
            // Show dialog to choose action
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose an option")
                    .setItems(new CharSequence[]{"Take Photo", "Select from Gallery"}, (dialog, which) -> {
                        if (which == 0) {
                            if (checkCameraPermission()) {
                                openCameraForScanning();
                            } else {
                                requestCameraPermission();
                            }
                        } else if (which == 1) {
                            openGalleryForImageSelection();
                        }
                    })
                    .show();
        });    }



    private void openCameraForScanning() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//        } else {
        // Log the issue for debugging
//            Toast.makeText(this, "No camera app found on the device.", Toast.LENGTH_SHORT).show();
        // Optional: Simulate a captured image for testing purposes
//            simulateCapturedImage();
//        }
    }

    private void openGalleryForImageSelection() {
        if (checkStoragePermission()) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK);
            galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        }else {
            // Request permission
            requestStoragePermission();
        }

    }


    private void setupNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_expense_fragment)
                .build();

//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.appBarMain.bottomNavigationView, navController);

//        // Handle re-selection of navigation items
//        binding.appBarMain.bottomNavigationView.setOnItemReselectedListener(item -> {
//            if (item.getItemId() == R.id.nav_grocerylist) {
//                // Use popUpTo to clear any existing instance of GroceryListFragment before navigating
//                navigateToDestination(R.id.nav_grocerylist, true);  // Refresh GroceryListFragment
//            }
//        });

        binding.appBarMain.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                navController.navigate(R.id.nav_home);
                return true;
            } else if (itemId == R.id.nav_gallery) {
                navController.navigate(R.id.yearSearchFragment);
                return true;
            } else if (itemId == R.id.nav_slideshow) {
                navController.navigate(R.id.nav_slideshow);
                return true;
            }else if (itemId == R.id.nav_expense_fragment) {
                navigateToDestination(R.id.nav_expense_fragment, false);
                return true;
            } else {
                return false;
            }
        });
    }
//checking it again checking it again
    private void navigateToDestination(int destinationId, boolean clearPrevious) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        // Use NavOptions to clear any existing instance of destination if required
        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)  // Avoids duplicating fragment if already at the top
                .setPopUpTo(destinationId, clearPrevious)  // Clears previous instance if specified
                .build();

        // Navigate to the destination with the specified options
        navController.navigate(destinationId, null, navOptions);
    }

    private void handleFetchUserDetails() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        fetchUserServices.fetchUserDetails(MainActivity.this, queue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings2) {
            showBudgetInputDialog();
            return true;
        } else if (itemId == R.id.action_settings3) {
            NavController navControllers = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navControllers.navigate(R.id.settingsFragment);
            return true;
        } else if (itemId == R.id.action_settings) {
            navigateToDestination(R.id.nav_profile, false);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showBudgetInputDialog() {
        BudgetDialog budgetDialog = new BudgetDialog(this);
        budgetDialog.showBudgetDialog();
    }

    //Check for Location Permission and dialog
    private boolean checkCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d("CameraPermission", "Is camera permission granted: true");
            return true; // Permission is granted
        } else {
            // Check if "Don't ask again" is selected
            if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                showSettingsDialog(); // Show dialog to go to settings
            } else {
                requestCameraPermission(); // Request permission again
            }
            return false;
        }
    }

    private void showSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Camera permission is required to use this feature. Please enable it in the app settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    //Check for Storage Permission and dialog
//    private boolean checkStoragePermission() {
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            return true; // Permission is granted
//        } else {
//            // Check if "Don't ask again" is checked
//            if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                // User has selected "Don't ask again"
//                showPermissionDeniedDialog();
//            } else {
//                // Request permission
//                requestStoragePermission();
//            }
//            return false;
//        }
//    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("You have denied storage permission. Please enable it in the app settings to access the gallery.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

//    private void requestStoragePermission() {
//        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == STORAGE_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission was granted
//                Toast.makeText(this, "Permission granted. You can now access the gallery.", Toast.LENGTH_SHORT).show();
//                openGalleryForImageSelection();
//            } else {
//                // Permission was denied
//                if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    // User selected "Don't ask again"
//                    showPermissionDeniedDialog();
//                } else {
//                    // Permission denied without "Don't ask again"
//                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }
    private File getFileFromUri(Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            // Query the content resolver to get the file path
            String[] projection = {MediaStore.Images.Media.DATA};
            try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equals(uri.getScheme())) {
            filePath = uri.getPath();
        }
        return filePath != null ? new File(filePath) : null;
    }
    private File convertBitmapToFile(Bitmap bitmap, String fileName) {
        try {
            // Create a temporary file in the cache directory
            File file = new File(getCacheDir(), fileName);
            file.createNewFile();

            // Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapData = bos.toByteArray();

            // Write the bytes to the file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showCapturedImageFullScreen(Uri imageUri) {
    Intent fullScreenIntent = new Intent(this, FullScreenImageActivity.class);
    fullScreenIntent.putExtra(FullScreenImageActivity.IMAGE_URI_KEY, imageUri.toString());
    startActivity(fullScreenIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, STORAGE_PERMISSION_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) { // Check for ReviewActivity result
            if (resultCode == RESULT_OK) {
                // Submission successful
                Toast.makeText(this, "Items successfully stored!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle Retake: Open camera again
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                if (capturedImage != null) {
                    File imageFile = convertBitmapToFile(capturedImage, "captured_image.jpg");
                    if (imageFile != null) {
                        uploadImage.uploadImageToGeminiAI(this, imageFile, requestQueue);
                    } else {
                        Toast.makeText(this, "Failed to process captured image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    File imageFile = getFileFromUri(imageUri);
                    if (imageFile != null && imageFile.exists()) {
                        uploadImage.uploadImageToGeminiAI(this, imageFile, requestQueue);
                    } else {
                        Toast.makeText(this, "Failed to process selected image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to select image from gallery", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}
