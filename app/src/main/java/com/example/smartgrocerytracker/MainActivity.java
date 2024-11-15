package com.example.smartgrocerytracker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class MainActivity extends AppCompatActivity {

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
        Button groceryListButton = findViewById(R.id.grocerylist);
        groceryListButton.setOnClickListener(v -> navigateToDestination(R.id.nav_grocerylist, true));

        // Fetch user details if needed
        handleFetchUserDetails();

        // Set up FAB action
        binding.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Next Feature Release", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show()
        );
    }

    private void setupNavigation() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_grocerylist)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.appBarMain.bottomNavigationView, navController);

        // Handle re-selection of navigation items
        binding.appBarMain.bottomNavigationView.setOnItemReselectedListener(item -> {
            if (item.getItemId() == R.id.nav_grocerylist) {
                // Use popUpTo to clear any existing instance of GroceryListFragment before navigating
                navigateToDestination(R.id.nav_grocerylist, true);  // Refresh GroceryListFragment
            }
        });

        // Handle item selection in BottomNavigationView using if-else
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
            } else if (itemId == R.id.nav_grocerylist) {
                navigateToDestination(R.id.nav_grocerylist, false);  // Normal navigation to GroceryList
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
            Snackbar.make(binding.getRoot(), "Budget action clicked", Snackbar.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.action_settings3) {
            Snackbar.make(binding.getRoot(), "Settings action clicked", Snackbar.LENGTH_SHORT).show();
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
