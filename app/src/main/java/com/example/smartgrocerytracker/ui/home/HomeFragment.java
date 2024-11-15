// HomeFragment.java
package com.example.smartgrocerytracker.ui.home;

import android.os.Bundle;
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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        // Grocery List Button
        binding.grocerylist.setOnClickListener(v ->
                navController.navigate(R.id.nav_grocerylist));

        binding.monthspend.setOnClickListener(v ->
                navController.navigate(R.id.nav_search_fragment));


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // To avoid memory leaks
    }
}
