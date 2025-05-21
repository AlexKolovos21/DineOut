package com.example.dineout.managers;

import com.example.dineout.data.MenuItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static CartManager instance;
    private final Map<MenuItem, Integer> cartItems;
    private final List<CartUpdateListener> listeners;

    private CartManager() {
        cartItems = new HashMap<>();
        listeners = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(MenuItem item) {
        addItem(item, 1);
    }

    public void addItem(MenuItem item, int quantity) {
        cartItems.put(item, cartItems.getOrDefault(item, 0) + quantity);
        notifyListeners();
    }

    public void removeItem(MenuItem item) {
        cartItems.remove(item);
        notifyListeners();
    }

    public void updateItemQuantity(MenuItem item, int quantity) {
        if (quantity <= 0) {
            cartItems.remove(item);
        } else {
            cartItems.put(item, quantity);
        }
        notifyListeners();
    }

    public void clearCart() {
        cartItems.clear();
        notifyListeners();
    }

    public Map<MenuItem, Integer> getCartItems() {
        return new HashMap<>(cartItems);
    }

    public int getItemQuantity(MenuItem item) {
        return cartItems.getOrDefault(item, 0);
    }

    public int getTotalItems() {
        int total = 0;
        for (int quantity : cartItems.values()) {
            total += quantity;
        }
        return total;
    }

    public double getTotal() {
        double total = 0;
        for (Map.Entry<MenuItem, Integer> entry : cartItems.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public void addCartUpdateListener(CartUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeCartUpdateListener(CartUpdateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (CartUpdateListener listener : listeners) {
            listener.onCartUpdated();
        }
    }

    public interface CartUpdateListener {
        void onCartUpdated();
    }
} 