package com.example.smartgrocerytracker.ui.grocerylist;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.smartgrocerytracker.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class FancyGroceryOptionsDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_grocery_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View dialogContent = view.findViewById(R.id.dialog_content);

        dialogContent.setScaleX(0f);
        dialogContent.setScaleY(0f);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(dialogContent, "scaleX", 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(dialogContent, "scaleY", 1f);
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
            String budgetId = sharedPreferences.getString("budget_id", null);

        view.findViewById(R.id.manual_receipt_option).setOnClickListener(v -> {
            if (!Objects.equals(budgetId, "null")) {
            BillInputDialogFragment billInputDialog = new BillInputDialogFragment();
            billInputDialog.show(getParentFragmentManager(), "BillInputDialog");
            dismiss();
            }
            else{
            Toast.makeText(requireContext(),"Please select Budget",Toast.LENGTH_LONG).show();
            }

        });

        view.findViewById(R.id.smart_ocr_option).setOnClickListener(v -> {
            if (!Objects.equals(budgetId, "null")) {
            Toast.makeText(requireContext(),"Next Feature Release",Toast.LENGTH_LONG).show();
            dismiss();
            }
            else{

            Toast.makeText(requireContext(),"Please select Budget",Toast.LENGTH_LONG).show();
            }

        });
    }
}
