package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.adapters.CartAdapter;
import com.example.dineout.managers.CartManager;
import com.google.android.material.button.MaterialButton;

public class CartScreen extends AppCompatActivity implements CartManager.CartUpdateListener {
    private RecyclerView cartRecyclerView;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private TextView emptyCartText;
    private MaterialButton checkoutButton;
    private CartAdapter adapter;
    private CartManager cartManager;

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
        getSupportActionBar().setTitle("Your Cart");

        // Initialize CartManager
        cartManager = CartManager.getInstance();
        cartManager.addCartUpdateListener(this);

        // Setup RecyclerView
        adapter = new CartAdapter(cartManager);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(adapter);

        // Setup checkout button
        checkoutButton.setOnClickListener(v -> {
            if (cartManager.getTotalItems() > 0) {
                // TODO: Implement actual checkout process
                Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
                // For now, just clear the cart
                cartManager.clearCart();
                finish();
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Initial update
        onCartUpdated();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCartUpdated() {
        int totalItems = cartManager.getTotalItems();
        double subtotal = cartManager.getTotal();
        double deliveryFee = totalItems > 0 ? 5.99 : 0.0; // Example delivery fee
        double total = subtotal + deliveryFee;

        // Update UI visibility
        boolean hasItems = totalItems > 0;
        cartRecyclerView.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        emptyCartText.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        checkoutButton.setEnabled(hasItems);

        // Update prices
        subtotalText.setText(String.format("$%.2f", subtotal));
        deliveryFeeText.setText(String.format("$%.2f", deliveryFee));
        totalText.setText(String.format("$%.2f", total));

        // Update adapter
        adapter.refreshItems();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeCartUpdateListener(this);
    }
} 