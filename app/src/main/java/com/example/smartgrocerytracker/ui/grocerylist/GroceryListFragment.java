package com.example.smartgrocerytracker.ui.grocerylist;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.databinding.FragmentGrocerylistBinding;
import com.example.smartgrocerytracker.databinding.FragmentProfileBinding;

public class GroceryListFragment extends Fragment {

    private FragmentGrocerylistBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentGrocerylistBinding.inflate(inflater, container, false);
        binding.addGList.setOnClickListener(v ->

        {
            FancyGroceryOptionsDialog optionsDialog = new FancyGroceryOptionsDialog();
            optionsDialog.show(getParentFragmentManager(), "FancyGroceryOptionsDialog");

        });
        return binding.getRoot();
    }

}