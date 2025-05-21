package com.example.dineout.utils;

import com.example.dineout.data.Order;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderManager {
    private static OrderManager instance;
    private final List<Order> orders;

    private OrderManager() {
        orders = new ArrayList<>();
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void addOrder(Order order) {
        orders.add(0, order); // Add to the beginning of the list
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public Order getOrder(String orderId) {
        for (Order order : orders) {
            if (order.getId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }
} 