package com.example.smartgrocerytracker;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
import com.example.smartgrocerytracker.ui.grocerylist.BudgetActivity;
import com.example.smartgrocerytracker.utils.BudgetDialog;
import com.example.smartgrocerytracker.utils.MediaUtils;
import com.example.smartgrocerytracker.ModelClass.SharedBudgetViewModel;
import com.example.smartgrocerytracker.utils.TextSizeUtils;
import com.example.smartgrocerytracker.utils.LanguageUtil;

import org.jetbrains.annotations.Nullable;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private RequestQueue requestQueue;
    private static final String TAG = "MainActivity";
    private SharedBudgetViewModel sharedBudgetViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtil.setLocale(this);
        super.onCreate(savedInstanceState);

        TextSizeUtils.applyTextSize(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // Initialize Shared ViewModel for budget live updates
        sharedBudgetViewModel = new ViewModelProvider(this).get(SharedBudgetViewModel.class);
        requestQueue = Volley.newRequestQueue(this);

        //retrieves user info on app-startup...
        handleFetchUserDetails();

        // Set up navigation bottomNavigationView
        setupNavigation();


        // Set up button to navigate to GroceryListFragment
        binding.appBarMain.fab.setOnClickListener(view -> showBudgetInputDialog());
        TextView groceryListButton = findViewById(R.id.grocerylist);
        if (groceryListButton == null) {
            Log.e(TAG, "Grocerylist TextView is null");
        } else {
            groceryListButton.setOnClickListener(v -> navigateToDestination(R.id.nav_expense_fragment, true));
        }

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
        });
    }


    private void setupNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_global_search, R.id.nav_map, R.id.nav_expense_fragment)
                .build();

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
                binding.appBarMain.toolbar.setTitle("Expense List");
                navigateToDestination(R.id.nav_expense_fragment, false);
                return true;
            } else {
                return false;
            }
        });
    }

    private void navigateToDestination(int destinationId, boolean clearPrevious) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(destinationId, clearPrevious)
                .build();

        navController.navigate(destinationId, null, navOptions);
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
        SharedPreferences sharedPreferences = getSharedPreferences("UserPref", MODE_PRIVATE);
        String budgetId = sharedPreferences.getString("budget_id", null);

        if (budgetId == null || budgetId.equals("null")) {
            // No active budget, route to BudgetActivity for new budget setting up
            Intent intent = new Intent(this, BudgetActivity.class);
            intent.putExtra("isEditMode", false);
            startActivity(intent);
        } else {
            BudgetDialog budgetDialog = new BudgetDialog(this, updatedBudgetModel -> {
               SharedBudgetViewModel.setBudgetModel(updatedBudgetModel);
           });
           budgetDialog.showBudgetDialog();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    private void handleFetchUserDetails() {
        fetchUserServices.fetchUserDetails(this, requestQueue, sharedBudgetViewModel);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PERMISSION_CODE && resultCode == RESULT_OK) {
            Uri imageUri = null;
            if (data != null) {
                imageUri = data.getData();
                if (imageUri != null) {
                    // Image selected from gallery.
                    File imageFile = MediaUtils.getFileFromUri(imageUri, this);
                    if (imageFile != null && imageFile.exists()) {
                        uploadImage.uploadImageToGeminiAI(this, imageFile, requestQueue);
                    } else {
                        Toast.makeText(this, "Failed to process selected image.", Toast.LENGTH_SHORT).show();
                    }
                } else if (data.getExtras() != null) {
                    // Image captured from camera capure.
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
                Toast.makeText(this, "No image captured.", Toast.LENGTH_SHORT).show();
            }
        }

        // ReviewActivity result On Scan-result
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                // Submission successful
                Toast.makeText(this, "Items successfully stored!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Handle Retake: Open camera again
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
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
}

