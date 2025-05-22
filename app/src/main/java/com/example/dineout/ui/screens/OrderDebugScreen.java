package com.example.dineout.ui.screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.dineout.R;
import com.example.dineout.data.AppDatabase;
import com.example.dineout.data.Order;
import com.example.dineout.data.OrderEntity;
import com.example.dineout.utils.OrderManager;
import java.util.List;

public class OrderDebugScreen extends AppCompatActivity {
    private static final String TAG = "OrderDebugScreen";
    private TextView roomOrdersText;
    private TextView orderManagerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_debug_screen);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Debug Info");

        // Initialize views
        roomOrdersText = findViewById(R.id.room_orders_text);
        orderManagerText = findViewById(R.id.order_manager_text);

        // Load and display orders
        loadOrders();
    }

    private void loadOrders() {
        // Load from Room Database
        AppDatabase.getInstance(this).orderDao().getAllOrders().observe(this, orderEntities -> {
            StringBuilder roomOrders = new StringBuilder("Room Database Orders:\n\n");
            if (orderEntities != null) {
                for (OrderEntity entity : orderEntities) {
                    roomOrders.append("ID: ").append(entity.getId())
                            .append("\nRestaurant: ").append(entity.getRestaurantName())
                            .append("\nTotal: $").append(entity.getTotalAmount())
                            .append("\nStatus: ").append(entity.getStatus())
                            .append("\nDate: ").append(new java.util.Date(entity.getTimestamp()))
                            .append("\n\n");
                }
            } else {
                roomOrders.append("No orders in Room database");
            }
            roomOrdersText.setText(roomOrders.toString());
        });

        // Load from OrderManager
        List<Order> orderManagerOrders = OrderManager.getInstance(this).getOrders();
        StringBuilder managerOrders = new StringBuilder("OrderManager Orders:\n\n");
        if (!orderManagerOrders.isEmpty()) {
            for (Order order : orderManagerOrders) {
                managerOrders.append("ID: ").append(order.getId())
                        .append("\nRestaurant: ").append(order.getRestaurantName())
                        .append("\nTotal: $").append(order.getTotalAmount())
                        .append("\nStatus: ").append(order.getStatus())
                        .append("\nDate: ").append(order.getFormattedDate())
                        .append("\n\n");
            }
        } else {
            managerOrders.append("No orders in OrderManager");
        }
        orderManagerText.setText(managerOrders.toString());
    }
} 