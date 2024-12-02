package com.example.smartgrocerytracker;


public class Config {

//    public static final String BASE_URL = "http://10.0.2.2:8000";
//   public static final String BASE_URL = "http://10.0.0.19:8000";
 public static final String BASE_URL = "http://165.227.35.235";



    public static final String EXPENSES_URL = BASE_URL + "/expenses/";
    public static final String ACTIVE_BUDGET_EXPENSES_URL = BASE_URL + "/expenses/active_budget_id/";
    public static final String ADD_EXPENSES_URL = BASE_URL + "/expenses/create/";
    public static final String UPDATE_EXPENSES_URL = BASE_URL + "/expenses/update/";
    public static final String GET_FULL_EXPENSES_URL = BASE_URL + "/expenses/";
    public static final String SEARCH_EXPENSES_URL = BASE_URL + "/expenses/search/";

    public static final String ADD_GROCERY_ITEM_URL = BASE_URL + "/grocery-item/create/";
    public static final String DELETE_GROCERY_ITEM_URL = BASE_URL + "/grocery-item/delete/";
    public static final String SEARCH_GROCERY_ITEM_URL = BASE_URL + "/grocery-item/search/";


    public static final String LOGIN_URL = BASE_URL + "/login/";
    public static final String REGISTERATION_URL = BASE_URL + "/users/";
    public static final String USER_FETCH_URL = BASE_URL + "/user/";

    public static final String BUDGET_STORE_URL = BASE_URL + "/budget/set";
    public static final String GET_BUDGET_URL = BASE_URL + "/budget/get/";
    public static final String UPDATE_BUDGET_URL = BASE_URL + "/budget/update/";


    public static final String SCAN_IMAGE_DATA = BASE_URL + "/image/scan";



}
