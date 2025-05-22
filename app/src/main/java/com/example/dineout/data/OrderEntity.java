package com.example.dineout.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.Map;

@Entity(tableName = "orders")
@TypeConverters({Converters.class})
public class OrderEntity {
    @PrimaryKey
    @NonNull
    private String id;
    private String deliveryAddress;
    private String paymentMethod;
    private double totalAmount;
    private long timestamp;
    private String restaurantId;
    private String restaurantName;
    private String status;
    private Map<MenuItem, Integer> items;

    // Required empty constructor for Room
    public OrderEntity() {
    }

    // Constructor for creating from Order
    public OrderEntity(Order order) {
        this.id = order.getId();
        this.items = order.getItems();
        this.deliveryAddress = order.getDeliveryAddress();
        this.paymentMethod = order.getPaymentMethod();
        this.totalAmount = order.getTotalAmount();
        this.timestamp = order.getTimestamp();
        this.restaurantId = order.getRestaurantId();
        this.restaurantName = order.getRestaurantName();
        this.status = order.getStatus();
    }

    public Order toOrder() {
        return new Order(items, deliveryAddress, paymentMethod, totalAmount, restaurantId, restaurantName);
    }

    // Getters and Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public Map<MenuItem, Integer> getItems() { return items; }
    public void setItems(Map<MenuItem, Integer> items) { this.items = items; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    
    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 