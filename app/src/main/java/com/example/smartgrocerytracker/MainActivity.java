package com.example.smartgrocerytracker;

import static com.example.smartgrocerytracker.utils.TokenValidator.handleAuthentication;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.services.fetchUserServices;
import com.example.smartgrocerytracker.utils.BudgetDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgrocerytracker.databinding.ActivityMainBinding;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        handleFetchUserDetails();

        setSupportActionBar(binding.appBarMain.toolbar);
//        // Set navigation icon color
//        if (binding.appBarMain.toolbar != null) {
//            Drawable navIcon = binding.appBarMain.toolbar.getNavigationIcon();
//            if (navIcon != null) {
//                navIcon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
//                binding.appBarMain.toolbar.setNavigationIcon(navIcon);
//            }
//        }
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Next Feature Release", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        // Initialize DrawerLayout and BottomNavigationView
//        DrawerLayout drawer = binding.drawerLayout;
        BottomNavigationView bottomNavigationView = binding.appBarMain.bottomNavigationView;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_grocerylist)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//                .setOpenableLayout(drawer)
//      NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings2) {
            id = 1;
        } else if (id == R.id.action_settings3) {
            id = 2;

        } else if (id == R.id.action_settings) {
            id = 3;
        }

        switch (id) {
            case 1:
                showBudgetInputDialog();
                Snackbar.make(binding.getRoot(), "Budget action clicked", Snackbar.LENGTH_SHORT).show();
                return true;

            case 2:
                Snackbar.make(binding.getRoot(), "Settings action clicked", Snackbar.LENGTH_SHORT).show();
                return true;

            case 3:

                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_profile);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void showBudgetInputDialog() {
        BudgetDialog budgetDialog = new BudgetDialog(this);
        budgetDialog.showBudgetDialog();
    }

    private void handleFetchUserDetails() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        fetchUserServices.fetchUserDetails(MainActivity.this,queue);
    }




    //    @Override
//    protected void onResume() {
//        super.onResume();
//        handleAuthentication(this);
//    }
}
