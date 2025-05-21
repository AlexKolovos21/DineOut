package com.example.dineout.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class Order implements Parcelable {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_PREPARING = "Preparing";
    public static final String STATUS_READY = "Ready for Pickup";
    public static final String STATUS_DELIVERING = "Out for Delivery";
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String id;
    private Map<MenuItem, Integer> items;
    private String deliveryAddress;
    private String paymentMethod;
    private double totalAmount;
    private long timestamp;
    private String restaurantId;
    private String restaurantName;
    private String status;

    public Order(Map<MenuItem, Integer> items, String deliveryAddress, String paymentMethod, double totalAmount, String restaurantId, String restaurantName) {
        this.id = UUID.randomUUID().toString();
        this.items = new HashMap<>(items);
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.timestamp = System.currentTimeMillis();
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.status = STATUS_PENDING;
    }

    protected Order(Parcel in) {
        id = in.readString();
        items = new HashMap<>();
        in.readMap(items, MenuItem.class.getClassLoader());
        deliveryAddress = in.readString();
        paymentMethod = in.readString();
        totalAmount = in.readDouble();
        timestamp = in.readLong();
        restaurantId = in.readString();
        restaurantName = in.readString();
        status = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeMap(items);
        dest.writeString(deliveryAddress);
        dest.writeString(paymentMethod);
        dest.writeDouble(totalAmount);
        dest.writeLong(timestamp);
        dest.writeString(restaurantId);
        dest.writeString(restaurantName);
        dest.writeString(status);
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public int getTotalItems() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Getters
    public String getId() {
        return id;
    }

    public Map<MenuItem, Integer> getItems() {
        return new HashMap<>(items);
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getSubtotal() {
        return totalAmount - 2.99; // Subtract delivery fee
    }

    public double getDeliveryFee() {
        return 2.99;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getStatus() {
        return status;
    }
} 