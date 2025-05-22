package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.adapters.CartAdapter;
import com.example.dineout.data.Cart;
import com.example.dineout.data.Restaurant;
import com.example.dineout.managers.CartManager;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartScreen extends AppCompatActivity implements CartManager.CartUpdateListener {
    private RecyclerView cartRecyclerView;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private TextView emptyCartText;
    private MaterialButton checkoutButton;
    private CartAdapter adapter;
    private CartManager cartManager;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_screen);

        // Initialize views
        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        subtotalText = findViewById(R.id.subtotal_text);
        deliveryFeeText = findViewById(R.id.delivery_fee_text);
        totalText = findViewById(R.id.total_text);
        emptyCartText = findViewById(R.id.empty_cart_text);
        checkoutButton = findViewById(R.id.checkout_button);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.cart);

        // Initialize CartManager
        cartManager = CartManager.getInstance();
        cartManager.addCartUpdateListener(this);

        // Setup RecyclerView
        adapter = new CartAdapter(cartManager);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(adapter);

        // Update UI
        updateUI();

        // Setup checkout button
        checkoutButton.setOnClickListener(v -> {
            Map<com.example.dineout.data.MenuItem, Integer> cartItems = cartManager.getCartItems();
            if (cartItems.isEmpty()) {
                Toast.makeText(this, R.string.empty_cart, Toast.LENGTH_SHORT).show();
                return;
            }

            // Get restaurant information from the first item
            com.example.dineout.data.MenuItem firstItem = cartItems.keySet().iterator().next();
            String restaurantId = firstItem.getRestaurantId();
            String restaurantName = firstItem.getRestaurantName();

            if (restaurantId == null || restaurantName == null) {
                Toast.makeText(this, R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a Cart object from the cart items
            Cart cart = new Cart();
            for (Map.Entry<com.example.dineout.data.MenuItem, Integer> entry : cartItems.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    cart.addItem(entry.getKey());
                }
            }

            // Create a Restaurant object with default values for required fields
            Restaurant restaurant = new Restaurant(
                restaurantId,                // id
                restaurantName,             // name
                firstItem.getRestaurantCuisine(),     // cuisine
                firstItem.getRestaurantRating(),      // rating
                firstItem.getRestaurantLatitude(),    // latitude
                firstItem.getRestaurantLongitude(),   // longitude
                firstItem.getRestaurantAddress(),     // address
                firstItem.getRestaurantPhone(),       // phone
                firstItem.getRestaurantDescription(), // description
                firstItem.getRestaurantPriceRange()   // priceRange
            );

            // Navigate to checkout screen
            Intent intent = new Intent(this, CheckoutScreen.class);
            intent.putExtra("restaurant", restaurant);
            intent.putExtra("cart", cart);
            startActivity(intent);
        });
    }

    private void updateUI() {
        Map<com.example.dineout.data.MenuItem, Integer> cartItems = cartManager.getCartItems();
        if (cartItems.isEmpty()) {
            cartRecyclerView.setVisibility(View.GONE);
            emptyCartText.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(false);
        } else {
            cartRecyclerView.setVisibility(View.VISIBLE);
            emptyCartText.setVisibility(View.GONE);
            checkoutButton.setEnabled(true);

            // Update adapter
            adapter.submitList(new ArrayList<>(cartItems.keySet()));

            // Update totals
            double subtotal = cartManager.getTotal();
            double deliveryFee = 2.99;
            double total = subtotal + deliveryFee;

            subtotalText.setText(currencyFormat.format(subtotal));
            deliveryFeeText.setText(currencyFormat.format(deliveryFee));
            totalText.setText(currencyFormat.format(total));
        }
    }

    @Override
    public void onCartUpdated() {
        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeCartUpdateListener(this);
    }
} 