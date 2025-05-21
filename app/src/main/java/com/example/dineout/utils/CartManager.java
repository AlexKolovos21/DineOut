package com.example.dineout.utils;

import com.example.dineout.data.MenuItem;
import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private final Map<MenuItem, Integer> cartItems;
    private CartUpdateListener listener;

    public interface CartUpdateListener {
        void onCartUpdated(Map<MenuItem, Integer> items, double total);
    }

    private CartManager() {
        cartItems = new HashMap<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void setCartUpdateListener(CartUpdateListener listener) {
        this.listener = listener;
        notifyCartUpdated();
    }

    public void addItem(MenuItem item) {
        int quantity = cartItems.getOrDefault(item, 0) + 1;
        cartItems.put(item, quantity);
        notifyCartUpdated();
    }

    public void removeItem(MenuItem item) {
        cartItems.remove(item);
        notifyCartUpdated();
    }

    public void updateQuantity(MenuItem item, int quantity) {
        if (quantity <= 0) {
            removeItem(item);
        } else {
            cartItems.put(item, quantity);
            notifyCartUpdated();
        }
    }

    public void clearCart() {
        cartItems.clear();
        notifyCartUpdated();
    }

    public Map<MenuItem, Integer> getCartItems() {
        return new HashMap<>(cartItems);
    }

    public int getItemCount() {
        return cartItems.values().stream().mapToInt(Integer::intValue).sum();
    }

    public double getTotal() {
        return cartItems.entrySet().stream()
            .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
            .sum();
    }

    private void notifyCartUpdated() {
        if (listener != null) {
            listener.onCartUpdated(getCartItems(), getTotal());
        }
    }
} 