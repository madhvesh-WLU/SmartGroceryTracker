package com.example.smartgrocerytracker.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentHomeBinding;
import com.google.android.material.textview.MaterialTextView;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setUserHead() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Guest");
        if ( binding.welcomeTextView != null) {
            binding.welcomeTextView.setText("Hello, " + username);
            Log.i("UserName", username);
        } else {
            Log.e("UserName", "TextView not found.");
        }
    }
}
