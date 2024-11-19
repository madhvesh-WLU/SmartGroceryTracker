package com.example.smartgrocerytracker.ui.expenses;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.smartgrocerytracker.ModelClass.ExpenseModel;
import com.example.smartgrocerytracker.ModelClass.GroceryItemModel;
import com.example.smartgrocerytracker.R;
import com.example.smartgrocerytracker.services.fetchExpensesServices;
import com.example.smartgrocerytracker.services.fetchGroceryListServices;
import com.example.smartgrocerytracker.ui.grocerylist.FancyGroceryOptionsDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ExpenseFragment extends Fragment implements ExpenseViewAdapter.OnExpenseClickListener {

    private RecyclerView recyclerView;
    private ExpenseViewAdapter adapter;
    private List<ExpenseModel> expenseList;
    private FloatingActionButton addButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        addButton = view.findViewById(R.id.add_gList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        expenseList = new ArrayList<>();
        adapter = new ExpenseViewAdapter(expenseList, this);
        recyclerView.setAdapter(adapter);

        setupOptionButton();

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchExpensesServices.fetchExpenses(requireContext(), queue, expenses -> {
            expenseList.clear();
            expenseList.addAll(expenses);
            adapter.notifyDataSetChanged();
        });
    }

//    @Override
//    public void onExpenseClick(ExpenseModel expense) {
//        RequestQueue queue = Volley.newRequestQueue(requireContext());
//        fetchGroceryListServices.fetchGroceryList(requireContext(), queue, expense.getExpenseId(), fullExpense -> {
//            Bundle bundle = new Bundle();
//            bundle.putString("bill_name", fullExpense.getBillName());
//            bundle.putString("date_of_purchase", fullExpense.getDateOfPurchase());
//            bundle.putString("total_price", String.valueOf(fullExpense.getBillAmount()));
//            bundle.putSerializable("grocery_items", (ArrayList<GroceryItemModel>) fullExpense.getGroceryItems());
//
//            NavController navController = NavHostFragment.findNavController(this);
//            navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
//        });
//    }
//
    @Override
    public void onExpenseClick(ExpenseModel expense) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        fetchGroceryListServices.fetchGroceryList(requireContext(), queue, expense.getExpenseId(), new fetchGroceryListServices.ExpenseListFetchListener() {
            @Override
            public void onExpensesFetched(ExpenseModel expense) {
                Bundle bundle = new Bundle();
            bundle.putString("bill_name", expense.getBillName());
            bundle.putString("date_of_purchase", expense.getDateOfPurchase());
            bundle.putString("total_price", String.valueOf(expense.getBillAmount()));
            bundle.putString("expense_id", expense.getExpenseId());
            bundle.putSerializable("grocery_items", (ArrayList<GroceryItemModel>) expense.getGroceryItems());

            NavController navController = NavHostFragment.findNavController(requireParentFragment());
            navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
            }

//            @Override
//            public void onExpensesListFetched(List<GroceryItemModel> groceryItems) {
//                // Handle the GroceryItemModel list data
//                Log.d("GroceryListener", "Grocery items fetched: " + groceryItems.size());
//            }
        });

    }


//    @Override
//    public void onExpenseClick(ExpenseModel expense) {
//        Bundle bundle = new Bundle();
//        bundle.putString("bill_name", expense.getBillName());
//        bundle.putString("date_of_purchase", expense.getDateOfPurchase());
//        bundle.putString("total_price", String.valueOf(expense.getBillAmount()));
//        bundle.putString("expense_id", expense.getExpenseId());
//
////        TODO over here basedupon expenseID i want to make api call to fetch all informatin of expense and also send that data to expenseListFragment to display overall data
//
//        NavController navController = NavHostFragment.findNavController(this);
//        navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
//    }

    private void setupOptionButton() {
        addButton.setOnClickListener(v -> {
            FancyGroceryOptionsDialog optionsDialog = new FancyGroceryOptionsDialog();
            optionsDialog.show(getParentFragmentManager(), "FancyGroceryOptionsDialog");
        });
    }
}











//    @Override
//    public void onExpenseClick(ExpenseModel expense) {
//
//        //api call
//        String expenseId = expense.getExpenseId();
//        RequestQueue queue = Volley.newRequestQueue(requireContext());
//        fetchGroceryListServices.fetchGroceryList(requireContext(),queue,expenseId);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("bill_name", expense.getBillName());
//        bundle.putString("date_of_purchase", expense.getDateOfPurchase());
//        bundle.putString("store_quantity", String.valueOf(expense.getQuantity()));
//        bundle.putString("total_price", String.valueOf(expense.getBillAmount()));
//        bundle.putString("store_name", expense.getBillName());
//
//
//
//        NavController navController = NavHostFragment.findNavController(this);
//        navController.navigate(R.id.action_expenseFragment_to_expenseListFragment, bundle);
//    }




//    private void showEditDialog(ExpenseModel expense) {
//        // Example of showing a dialog to edit the expense
//        ExpenseEditDialog dialog = new ExpenseEditDialog(requireContext(), expense, updatedExpense -> {
//            // Update the list and notify adapter
//            int index = expenseList.indexOf(expense);
//            if (index != -1) {
//                expenseList.set(index, updatedExpense);
//                adapter.notifyItemChanged(index);
//            }
//        });
//        dialog.show();
//    }
