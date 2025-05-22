package com.example.dineout.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.dineout.data.MenuItem;
import com.example.dineout.data.Order;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OrderManager {
    private static final String TAG = "OrderManager";
    private static final String PREF_NAME = "order_preferences";
    private static final String KEY_SAVED_ORDERS = "saved_orders";
    private static OrderManager instance;
    private final List<Order> orders;
    private final SharedPreferences preferences;
    private final Gson gson;

    private OrderManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<MenuItem, Integer>>(){}.getType(), new MenuItemMapTypeAdapter())
            .create();
        orders = loadOrders();
    }

    public static synchronized OrderManager getInstance(Context context) {
        if (instance == null) {
            instance = new OrderManager(context.getApplicationContext());
        }
        return instance;
    }

    private List<Order> loadOrders() {
        String json = preferences.getString(KEY_SAVED_ORDERS, "[]");
        Type type = new TypeToken<List<Order>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private void saveOrders() {
        String json = gson.toJson(orders);
        preferences.edit().putString(KEY_SAVED_ORDERS, json).apply();
    }

    public void addOrder(Order order) {
        if (order != null) {
            Log.d(TAG, "Adding order: " + order.getId());
            orders.add(0, order); // Add to the beginning of the list
            saveOrders();
            Log.d(TAG, "Total orders: " + orders.size());
        } else {
            Log.e(TAG, "Attempted to add null order");
        }
    }

    public List<Order> getOrders() {
        Log.d(TAG, "Getting orders, count: " + orders.size());
        return Collections.unmodifiableList(new ArrayList<>(orders));
    }

    public Order getOrder(String orderId) {
        if (orderId == null) {
            Log.e(TAG, "Attempted to get order with null ID");
            return null;
        }
        
        for (Order order : orders) {
            if (orderId.equals(order.getId())) {
                Log.d(TAG, "Found order: " + orderId);
                return order;
            }
        }
        Log.d(TAG, "Order not found: " + orderId);
        return null;
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        Order order = getOrder(orderId);
        if (order != null) {
            Log.d(TAG, "Updating order status: " + orderId + " to " + newStatus);
            order.setStatus(newStatus);
        } else {
            Log.e(TAG, "Attempted to update status of non-existent order: " + orderId);
        }
    }

    public void clearOrders() {
        Log.d(TAG, "Clearing all orders");
        orders.clear();
        saveOrders();
    }
} 