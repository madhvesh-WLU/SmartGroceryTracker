package com.example.smartgrocerytracker.ui.grocerylist;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.ui.grocerylist.ExpenseAdapter;
import com.example.smartgrocerytracker.databinding.FragmentGrocerylistBinding;
import com.example.smartgrocerytracker.services.fetchExpensesServices;
import java.util.ArrayList;
import java.util.List;

public class GroceryListFragment extends Fragment implements fetchExpensesServices.ExpenseFetchListener {

    private FragmentGrocerylistBinding binding;
    private ExpenseAdapter adapter;
    private List<ExpenseModel> expenseList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGrocerylistBinding.inflate(inflater, container, false);

        setupRecyclerView();

        binding.addGList.setOnClickListener(v -> {
            FancyGroceryOptionsDialog optionsDialog = new FancyGroceryOptionsDialog();
            optionsDialog.show(getParentFragmentManager(), "FancyGroceryOptionsDialog");
        });

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(expenseList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchExpenses();
    }

    private void fetchExpenses() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchExpensesServices.fetchExpenses(requireContext(), queue, this);
    }

    @Override
    public void onExpensesFetched(List<ExpenseModel> expenses) {
        if (expenses.isEmpty()) {
            Toast.makeText(requireContext(), "No expenses found", Toast.LENGTH_SHORT).show();
        } else {
            expenseList.clear();
            expenseList.addAll(expenses);
            adapter.notifyDataSetChanged();  // Notify adapter to update RecyclerView
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Prevent memory leaks by clearing the binding reference
    }
}
