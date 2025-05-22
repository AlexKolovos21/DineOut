package com.example.dineout.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.dineout.data.Cart;
import com.google.gson.Gson;

public class CartRepository {
    private static final String PREF_NAME = "cart_prefs";
    private static final String KEY_CART = "cart";
    private final SharedPreferences preferences;
    private final Gson gson;

    public CartRepository(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveCart(Cart cart) {
        String cartJson = gson.toJson(cart);
        preferences.edit().putString(KEY_CART, cartJson).apply();
    }

    public Cart getCart() {
        String cartJson = preferences.getString(KEY_CART, null);
        if (cartJson == null) {
            return new Cart();
        }
        return gson.fromJson(cartJson, Cart.class);
    }

    public void clearCart() {
        preferences.edit().remove(KEY_CART).apply();
    }
} 