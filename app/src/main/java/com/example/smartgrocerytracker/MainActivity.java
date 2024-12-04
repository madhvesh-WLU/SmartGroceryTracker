package com.example.smartgrocerytracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.BudgetModel;
import com.example.smartgrocerytracker.databinding.ActivityMainBinding;
import com.example.smartgrocerytracker.services.fetchBudgetDetails;
import com.example.smartgrocerytracker.services.fetchUserServices;
import com.example.smartgrocerytracker.services.uploadImage;
import com.example.smartgrocerytracker.ui.FullScreenImageActivity;
import com.example.smartgrocerytracker.ui.ReviewActivity;
import com.example.smartgrocerytracker.ui.grocerylist.BudgetActivity;
import com.example.smartgrocerytracker.ui.home.HomeFragment;
import com.example.smartgrocerytracker.utils.BudgetDialog;
import com.example.smartgrocerytracker.utils.MediaUtils;
import com.example.smartgrocerytracker.ModelClass.SharedBudgetViewModel;
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
    private SharedBudgetViewModel sharedBudgetViewModel;
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
        // Initialize Shared ViewModel
        sharedBudgetViewModel = new ViewModelProvider(this).get(SharedBudgetViewModel.class);

        // FAB to open BudgetDialog
        binding.appBarMain.fab.setOnClickListener(view -> showBudgetInputDialog());
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

            if (MediaUtils.checkCameraPermission(this) && MediaUtils.checkStoragePermission(this)) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

            startActivityForResult(chooserIntent, CAMERA_PERMISSION_CODE);
            }else{
                MediaUtils.requestStoragePermission(this);
            }
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Choose an option")
//                    .setItems(new CharSequence[]{"Take Photo", "Select from Gallery"}, (dialog, which) -> {
//                        if (which == 0) {
//                            if (MediaUtils.checkCameraPermission(this)) {
//                                MediaUtils.openCamera(this);
//                            }
//                        } else if (which == 1) {
//                            if (MediaUtils.checkStoragePermission(this)) {
//                                MediaUtils.openGallery(this);
//                            } else {
//                                MediaUtils.requestStoragePermission(this);
//                            }
//                        }
//                    })
//                    .show();
        });
    }


    private void setupNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_global_search, R.id.nav_map, R.id.nav_expense_fragment)
                .build();

//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.appBarMain.bottomNavigationView, navController);

        binding.appBarMain.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                navController.navigate(R.id.nav_home);
                return true;
            } else if (itemId == R.id.nav_global_search) {
                navController.navigate(R.id.nav_global_search);
                return true;
            } else if (itemId == R.id.nav_map) {
                navController.navigate(R.id.nav_map);
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
        // Call fetchUserDetails and update SharedBudgetViewModel
        fetchUserServices.fetchUserDetails(this, requestQueue, sharedBudgetViewModel);
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
        // Access SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        String budgetId = sharedPreferences.getString("budget_id", null);

        // Check if budgetId exists
        if (budgetId == null || budgetId.equals("null")) {
            // No active budget, navigate to BudgetActivity for new budget creation
            Intent intent = new Intent(this, BudgetActivity.class);
            intent.putExtra("isEditMode", false); // Pass 'false' to indicate a new budget setup
            startActivity(intent);
        } else {
            BudgetDialog budgetDialog = new BudgetDialog(this, updatedBudgetModel -> {
               SharedBudgetViewModel.setBudgetModel(updatedBudgetModel);
           });
           budgetDialog.showBudgetDialog();
        }

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

        // Handle Chooser Result
        if (requestCode == CAMERA_PERMISSION_CODE && resultCode == RESULT_OK) {
            Uri imageUri = null;

            if (data != null) {
                // Check if the data has a URI (Gallery)
                imageUri = data.getData();

                if (imageUri != null) {
                    // Image selected from gallery
                    File imageFile = MediaUtils.getFileFromUri(imageUri, this);
                    if (imageFile != null && imageFile.exists()) {
                        uploadImage.uploadImageToGeminiAI(this, imageFile, requestQueue);
                    } else {
                        Toast.makeText(this, "Failed to process selected image.", Toast.LENGTH_SHORT).show();
                    }
                } else if (data.getExtras() != null) {
                    // Image captured from camera (thumbnail)
                    Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                    if (capturedImage != null) {
                        File imageFile = MediaUtils.convertBitmapToFile(this, capturedImage, "captured_image.jpg");
                        if (imageFile != null) {
                            uploadImage.uploadImageToGeminiAI(this, imageFile, requestQueue);
                        } else {
                            Toast.makeText(this, "Failed to process captured image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                // Handle the case where data is null (might be full-size image saved to URI)
                // Implement this if you're using EXTRA_OUTPUT to save the image to a specific URI
                Toast.makeText(this, "No image captured.", Toast.LENGTH_SHORT).show();
            }
        }

        // Handle ReviewActivity result
        if (requestCode == 101) { // ReviewActivity result
            if (resultCode == RESULT_OK) {
                // Submission successful
                Toast.makeText(this, "Items successfully stored!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle Retake: Open camera again
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }

        // Handle other results as necessary
    }
}

