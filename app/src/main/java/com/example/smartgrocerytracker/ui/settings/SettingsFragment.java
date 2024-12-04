package com.example.smartgrocerytracker.ui.settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.ui.Login;
import com.example.smartgrocerytracker.ui.profile.ProfileFragment;
import com.example.smartgrocerytracker.ui.profile.UserProfile;
import com.example.smartgrocerytracker.utils.SecurePreferences;
import com.example.smartgrocerytracker.utils.TokenValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class SettingsFragment extends Fragment {

    private Switch nightModeSwitch, privateAccountSwitch;
    private TextView userName, tapToEdit;
    private ImageView userImage;
    private MaterialCardView securityPrivacyCard;

    private LinearLayout languagesCard, textSizeCard;
    private MaterialButton logoutButton;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppSettingsPrefs";
    private static final String NIGHT_MODE_KEY = "NightMode";
    private static final String PRIVATE_ACCOUNT_KEY = "PrivateAccount";
    private static final String NOTIFICATIONS_KEY = "Notifications";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false); // Ensure you have fragment_settings.xml

        initViews(view);
        setupListeners();
        loadPreferences();

        view.findViewById(R.id.app_description_card).setOnClickListener(v -> showAppDescriptionDialog());

        return view;
    }

    private void initViews(View view) {
        // Initialize Views using the inflated view
        nightModeSwitch = view.findViewById(R.id.night_mode_switch);
        privateAccountSwitch = view.findViewById(R.id.private_account_switch);
        userName = view.findViewById(R.id.user_name);
        tapToEdit = view.findViewById(R.id.tap_to_edit);
        userImage = view.findViewById(R.id.user_image);
        securityPrivacyCard = view.findViewById(R.id.security_privacy_card);
        textSizeCard = view.findViewById(R.id.text_size_card);
        languagesCard = view.findViewById(R.id.languages_card);
        logoutButton = view.findViewById(R.id.logout_button);

        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME,
                getActivity().MODE_PRIVATE);
    }

    private void setupListeners() {
        // Handle Night Mode Switch
        nightModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(NIGHT_MODE_KEY, isChecked);
            editor.apply();
            //applyNightMode(isChecked);
        });

        // Handle Private Account Switch
        privateAccountSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PRIVATE_ACCOUNT_KEY, isChecked);
            editor.apply();
        });

        // Handle User Profile Tap
        View.OnClickListener openProfileListener = v -> openUserProfile();
        userImage.setOnClickListener(openProfileListener);
        tapToEdit.setOnClickListener(openProfileListener);

        // Handle Security & Privacy Card Click
        securityPrivacyCard.setOnClickListener(v -> {
            // Implement navigation to Security & Privacy settings screen
            // For example, open a new fragment or activity
            // Example:
            // startActivity(new Intent(getActivity(), SecurityPrivacyActivity.class));
        });

        // Handle Text Size Card Click
        textSizeCard.setOnClickListener(v -> {
            // Implement navigation to Text Size settings screen
            startActivity(new Intent(getActivity(), TextsizeActivity.class));
        });

        // Handle Languages Card Click
        languagesCard.setOnClickListener(v -> {
            // Implement navigation to Language settings screen
            startActivity(new Intent(getActivity(), LanguageActivity.class));
        });

        // Handle Logout Button Click
        logoutButton.setOnClickListener(v -> handleLogout());
    }

    private void loadPreferences() {
        boolean isNightMode = sharedPreferences.getBoolean(NIGHT_MODE_KEY, false);
        boolean isPrivateAccount = sharedPreferences.getBoolean(PRIVATE_ACCOUNT_KEY, false);

        // Set initial states
        nightModeSwitch.setChecked(isNightMode);
        privateAccountSwitch.setChecked(isPrivateAccount);
    }

//    private void applyNightMode(boolean isEnabled) {
//        // Implement night mode application here
//        if (isEnabled) {
//            requireActivity().setTheme(R.style.Theme_AppCompat_DayNight);
//        } else {
//            requireActivity().setTheme(R.style.Theme_AppCompat_Light);
//        }
//        requireActivity().recreate();
//    }

    private void openUserProfile() {
        ProfileFragment fragment = new ProfileFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment) // Ensure you have a container with this ID
                .addToBackStack(null) // Optional: add to back stack
                .commit();
    }

    private void logout() {
        // Clear the shared preferences to log out the user
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(getActivity(), Login.class));



        // Optionally, call getActivity().finish() to ensure the current fragment/activity is finished
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void handleLogout() {
        if(SecurePreferences.removeAuthToken(requireContext())){
            UserProfile userDataManager = UserProfile.getInstance();
            userDataManager.clearData();
            TokenValidator.redirectToLogin(requireContext());
        }
    }
    private void showAppDescriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_app_description, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Apply the zoom-in animation when the dialog is shown
        dialog.setOnShowListener(dialogInterface -> {
            View dialogRoot = dialogView.getRootView();

            // Create ObjectAnimators for scale X and Y
            ObjectAnimator scaleXIn = ObjectAnimator.ofFloat(dialogRoot, "scaleX", 0.7f, 1.0f);
            ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(dialogRoot, "scaleY", 0.7f, 1.0f);

            scaleXIn.setDuration(400);
            scaleYIn.setDuration(400);

            // Use a DecelerateInterpolator to make it smooth
            scaleXIn.setInterpolator(new DecelerateInterpolator());
            scaleYIn.setInterpolator(new DecelerateInterpolator());

            // Play the animations together
            AnimatorSet zoomInSet = new AnimatorSet();
            zoomInSet.playTogether(scaleXIn, scaleYIn);
            zoomInSet.start();
        });

        // Close button click listener
        Button closeButton = dialogView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> {
            View dialogRoot = dialogView.getRootView();

            // Create ObjectAnimators for scale X and Y (Zoom-out effect)
            ObjectAnimator scaleXOut = ObjectAnimator.ofFloat(dialogRoot, "scaleX", 1.0f, 0.7f);
            ObjectAnimator scaleYOut = ObjectAnimator.ofFloat(dialogRoot, "scaleY", 1.0f, 0.7f);

            scaleXOut.setDuration(300);
            scaleYOut.setDuration(300);

            scaleXOut.setInterpolator(new DecelerateInterpolator());
            scaleYOut.setInterpolator(new DecelerateInterpolator());

            // Play the animations together
            AnimatorSet zoomOutSet = new AnimatorSet();
            zoomOutSet.playTogether(scaleXOut, scaleYOut);

            // Add a listener to dismiss the dialog after the animation
            zoomOutSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dialog.dismiss();
                }
            });

            // Start the zoom-out animation
            zoomOutSet.start();
        });

        dialog.show();
    }
}
