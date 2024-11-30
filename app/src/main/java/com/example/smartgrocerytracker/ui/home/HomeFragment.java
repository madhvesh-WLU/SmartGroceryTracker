// HomeFragment.java
package com.example.smartgrocerytracker.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.smartgrocerytracker.MainActivity;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setUserHead();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        // Grocery List Button
        binding.grocerylistMainButton.setOnClickListener(v ->
                navController.navigate(R.id.nav_expense_fragment));

        binding.monthlyMainButton.setOnClickListener(v ->
                navController.navigate(R.id.nav_search_fragment));

        SharedPreferences sharedPreference = getContext().getSharedPreferences("ActiveBudget", MODE_PRIVATE);
        String budgetAmount = sharedPreference.getString("amount",null);
        String spentAmount = sharedPreference.getString("spent_amount",null);

        binding.valueTotalBudget.setText(budgetAmount == null? "No Active Budget" : budgetAmount);
        binding.valueLeftToSpend.setText(spentAmount == null? "$0" : spentAmount);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void setUserHead() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");
        if ( binding.welcomeview != null) {
            binding.welcomeview.setText("Hello, " + username);
        } else {
            Log.e("UserName", "TextView not found.");
        }
    }
}
