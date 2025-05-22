package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.adapters.OrderHistoryAdapter;
import com.example.dineout.data.Order;
import com.example.dineout.utils.OrderManager;
import java.util.List;

public class OrderHistoryScreen extends AppCompatActivity implements OrderHistoryAdapter.OnOrderClickListener {
    private static final String TAG = "OrderHistoryScreen";
    private RecyclerView orderHistoryRecyclerView;
    private TextView emptyHistoryText;
    private OrderHistoryAdapter orderHistoryAdapter;
    private OrderManager orderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_screen);

        try {
            // Initialize views
            orderHistoryRecyclerView = findViewById(R.id.order_history_recycler_view);
            emptyHistoryText = findViewById(R.id.empty_history_text);

            // Setup toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.order_history);

            // Initialize OrderManager
            orderManager = OrderManager.getInstance(this);

            // Setup RecyclerView
            orderHistoryAdapter = new OrderHistoryAdapter(this);
            orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            orderHistoryRecyclerView.setAdapter(orderHistoryAdapter);

            // Load orders
            loadOrders();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing order history", e);
            Toast.makeText(this, "Error initializing order history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadOrders() {
        try {
            List<Order> orders = orderManager.getOrders();
            Log.d(TAG, "Loading orders, count: " + orders.size());
            
            if (orders.isEmpty()) {
                orderHistoryRecyclerView.setVisibility(View.GONE);
                emptyHistoryText.setVisibility(View.VISIBLE);
            } else {
                orderHistoryRecyclerView.setVisibility(View.VISIBLE);
                emptyHistoryText.setVisibility(View.GONE);
                orderHistoryAdapter.submitList(orders);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading orders", e);
            Toast.makeText(this, "Error loading orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onOrderClick(Order order) {
        try {
            Intent intent = new Intent(this, OrderDetailScreen.class);
            intent.putExtra("order", order);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error viewing order details", e);
            Toast.makeText(this, "Error viewing order details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
} 