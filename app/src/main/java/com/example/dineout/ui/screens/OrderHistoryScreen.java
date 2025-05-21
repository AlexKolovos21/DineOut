package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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
    private RecyclerView orderHistoryRecyclerView;
    private TextView emptyHistoryText;
    private OrderHistoryAdapter orderHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_screen);

        // Initialize views
        orderHistoryRecyclerView = findViewById(R.id.order_history_recycler_view);
        emptyHistoryText = findViewById(R.id.empty_history_text);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.order_history);

        // Setup RecyclerView
        orderHistoryAdapter = new OrderHistoryAdapter(this);
        orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderHistoryRecyclerView.setAdapter(orderHistoryAdapter);

        // Load orders
        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = OrderManager.getInstance().getOrders();
        if (orders.isEmpty()) {
            orderHistoryRecyclerView.setVisibility(View.GONE);
            emptyHistoryText.setVisibility(View.VISIBLE);
        } else {
            orderHistoryRecyclerView.setVisibility(View.VISIBLE);
            emptyHistoryText.setVisibility(View.GONE);
            orderHistoryAdapter.submitList(orders);
        }
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(this, OrderDetailScreen.class);
        intent.putExtra("order", order);
        startActivity(intent);
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