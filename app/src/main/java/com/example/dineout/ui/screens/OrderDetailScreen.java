package com.example.dineout.ui.screens;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.adapters.CartAdapter;
import com.example.dineout.data.Order;
import com.example.dineout.managers.CartManager;
import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetailScreen extends AppCompatActivity {
    private TextView restaurantNameText;
    private TextView orderDateText;
    private TextView orderStatusText;
    private TextView deliveryAddressText;
    private TextView paymentMethodText;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private RecyclerView itemsRecyclerView;
    private CartAdapter cartAdapter;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_screen);

        // Get order from intent
        Order order = getIntent().getParcelableExtra("order");
        if (order == null) {
            finish();
            return;
        }

        // Initialize views
        restaurantNameText = findViewById(R.id.restaurant_name);
        orderDateText = findViewById(R.id.order_date);
        orderStatusText = findViewById(R.id.order_status);
        deliveryAddressText = findViewById(R.id.delivery_address);
        paymentMethodText = findViewById(R.id.payment_method);
        subtotalText = findViewById(R.id.subtotal_amount);
        deliveryFeeText = findViewById(R.id.delivery_fee_amount);
        totalText = findViewById(R.id.total_amount);
        itemsRecyclerView = findViewById(R.id.items_recycler_view);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_details);

        // Setup RecyclerView
        cartAdapter = new CartAdapter(CartManager.getInstance());
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemsRecyclerView.setAdapter(cartAdapter);

        // Set order details
        restaurantNameText.setText(order.getRestaurantName());
        orderDateText.setText(order.getFormattedDate());
        orderStatusText.setText(order.getStatus());
        deliveryAddressText.setText(order.getDeliveryAddress());
        paymentMethodText.setText(order.getPaymentMethod());

        subtotalText.setText(currencyFormat.format(order.getSubtotal()));
        deliveryFeeText.setText(currencyFormat.format(order.getDeliveryFee()));
        totalText.setText(currencyFormat.format(order.getTotalAmount()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 