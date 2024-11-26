package com.example.smartgrocerytracker.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.smartgrocerytracker.R;

public class SettingsFragment extends Fragment {

    private Spinner languageSpinner;
    private Button logoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize views
        languageSpinner = view.findViewById(R.id.language_spinner);
        logoutButton = view.findViewById(R.id.logout_button);

        // Handle Profile Edit Click
        view.findViewById(R.id.tap_to_edit).setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_settingsFragment_to_profileFragment);
        });

        // Handle Language Selection
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = (String) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), "Language selected: " + selectedLanguage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action
            }
        });

        // Handle Logout
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_SHORT).show();
            // Add logout logic here
        });

        return view;
    }
}
