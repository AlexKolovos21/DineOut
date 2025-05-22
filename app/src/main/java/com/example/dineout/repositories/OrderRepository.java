package com.example.dineout.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.dineout.data.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private static final String PREF_NAME = "order_prefs";
    private static final String KEY_ORDERS = "orders";
    private final SharedPreferences preferences;
    private final Gson gson;

    public interface OnOrderSavedListener {
        void onOrderSaved(Order order);
        void onError(String error);
    }

    public OrderRepository(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveOrder(Order order, OnOrderSavedListener listener) {
        try {
            List<Order> orders = getOrders();
            orders.add(order);
            String ordersJson = gson.toJson(orders);
            preferences.edit().putString(KEY_ORDERS, ordersJson).apply();
            listener.onOrderSaved(order);
        } catch (Exception e) {
            listener.onError("Failed to save order: " + e.getMessage());
        }
    }

    public List<Order> getOrders() {
        String ordersJson = preferences.getString(KEY_ORDERS, null);
        if (ordersJson == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<Order>>() {}.getType();
        return gson.fromJson(ordersJson, type);
    }
} 