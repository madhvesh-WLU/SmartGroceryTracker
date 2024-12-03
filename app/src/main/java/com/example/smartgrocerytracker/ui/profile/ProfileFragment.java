package com.example.smartgrocerytracker.ui.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentHomeBinding;
import com.example.smartgrocerytracker.databinding.FragmentProfileBinding;
import com.example.smartgrocerytracker.ui.Login;
import com.example.smartgrocerytracker.utils.SecurePreferences;
import com.example.smartgrocerytracker.utils.TokenValidator;
import com.example.smartgrocerytracker.services.fetchUserServices;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    SharedPreferences sharedPreferences;
    static final String SharedPrefName = "UserPref";
    private static final int EDIT_PROFILE_REQUEST_CODE = 1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

        @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);



        displayUserData();

        binding.logout.setOnClickListener(v -> handleLogout());
            binding.editProfile.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE);
            });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == getActivity().RESULT_OK){
            // Refresh user data display
            displayUserData();
        }
    }


    private void displayUserData() {

//        UserProfile userDataManager = UserProfile.getInstance();
//        String username = userDataManager.getUsername();
//        String email = userDataManager.getEmail();

        sharedPreferences = requireContext().getSharedPreferences(SharedPrefName,MODE_PRIVATE);

        String username = sharedPreferences.getString("username",null);
        String email = sharedPreferences.getString("email",null);

        if (username != null && email != null) {
            binding.email.setText(email);
            binding.username.setText(username);
        }
    }




    private void handleLogout() {
        if(SecurePreferences.removeAuthToken(requireContext())){
            UserProfile userDataManager = UserProfile.getInstance();
            userDataManager.clearData();
            TokenValidator.redirectToLogin(requireContext());
        }
    }
}