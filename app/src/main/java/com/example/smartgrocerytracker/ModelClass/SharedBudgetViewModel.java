package com.example.smartgrocerytracker.ModelClass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.smartgrocerytracker.ModelClass.BudgetModel;

public class SharedBudgetViewModel extends ViewModel {
    private static final MutableLiveData<BudgetModel> budgetModelLiveData = new MutableLiveData<>();

    public static void setBudgetModel(BudgetModel budgetModel) {
        budgetModelLiveData.setValue(budgetModel);
    }

    public LiveData<BudgetModel> getBudgetModel() {
        return budgetModelLiveData;
    }
}