package com.example.smartgrocerytracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.databinding.ActivityMainBinding;
import com.example.smartgrocerytracker.services.fetchUserServices;
import com.example.smartgrocerytracker.utils.BudgetDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // Set up navigation for DrawerLayout and BottomNavigationView
        setupNavigation();

        // Set up button to navigate to GroceryListFragment
        TextView groceryListButton = findViewById(R.id.grocerylist);
        groceryListButton.setOnClickListener(v -> navigateToDestination(R.id.nav_expense_fragment, true));

        // Fetch user details if needed
        handleFetchUserDetails();

        // Set up FAB for camera
        binding.appBarMain.fab.setOnClickListener(view -> {
            // Check for camera permissions
            if (checkCameraPermission()) {
                openCameraForScanning();
            } else {
                requestCameraPermission();
            }
            GifImageView gifImageView = findViewById(R.id.gifimageview);
            gifImageView.setImageResource(R.drawable.giphy);
        });
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

    private boolean checkCameraPermission() {
        boolean isGranted = checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        Log.d("CameraPermission", "Is camera permission granted: " + isGranted);
        return isGranted;
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }


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

    // Simulate a captured image for testing in case the camera app is unavailable
    private void simulateCapturedImage() {
        Bitmap simulatedImage = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        simulatedImage.eraseColor(android.graphics.Color.RED); // Example: a red bitmap
        showCapturedImageDialog(simulatedImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
            showCapturedImageDialog(capturedImage);
        }
    }

    private void showCapturedImageDialog(Bitmap image) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_captured_image, null);
        ImageView imageView = dialogView.findViewById(R.id.capturedImageView);
        imageView.setImageBitmap(image);

        new AlertDialog.Builder(this)
                .setTitle("Captured Bill")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    Toast.makeText(this, "Bill saved successfully!", Toast.LENGTH_SHORT).show();
                    // Save image logic here
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
