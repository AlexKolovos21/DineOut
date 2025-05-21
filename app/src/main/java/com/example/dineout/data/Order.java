package com.example.dineout.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Order implements Parcelable {
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_CONFIRMED = "Confirmed";
    public static final String STATUS_PREPARING = "Preparing";
    public static final String STATUS_READY = "Ready for Pickup";
    public static final String STATUS_DELIVERING = "Out for Delivery";
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_CANCELLED = "Cancelled";

    private final String id;
    private final String restaurantId;
    private final String restaurantName;
    private final Map<MenuItem, Integer> items;
    private final double total;
    private final long date;
    private final String status;
    private final String deliveryAddress;
    private final String paymentMethod;

    public Order(String id, String restaurantId, String restaurantName, Map<MenuItem, Integer> items,
                double total, long date, String status, String deliveryAddress, String paymentMethod) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.items = new HashMap<>(items);
        this.total = total;
        this.date = date;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
    }

    protected Order(Parcel in) {
        id = in.readString();
        restaurantId = in.readString();
        restaurantName = in.readString();
        items = new HashMap<>();
        in.readMap(items, MenuItem.class.getClassLoader());
        total = in.readDouble();
        date = in.readLong();
        status = in.readString();
        deliveryAddress = in.readString();
        paymentMethod = in.readString();
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
        dest.writeString(restaurantId);
        dest.writeString(restaurantName);
        dest.writeMap(items);
        dest.writeDouble(total);
        dest.writeLong(date);
        dest.writeString(status);
        dest.writeString(deliveryAddress);
        dest.writeString(paymentMethod);
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(new Date(date));
    }

    public int getTotalItems() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Getters
    public String getId() { return id; }
    public String getRestaurantId() { return restaurantId; }
    public String getRestaurantName() { return restaurantName; }
    public Map<MenuItem, Integer> getItems() { return new HashMap<>(items); }
    public double getTotal() { return total; }
    public long getDate() { return date; }
    public String getStatus() { return status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getPaymentMethod() { return paymentMethod; }
}

class CartItem {
    private final MenuItem menuItem;
    private final int quantity;
    private final String specialInstructions;

    public CartItem(MenuItem menuItem, int quantity, String specialInstructions) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
    }

    public CartItem(MenuItem menuItem, int quantity) {
        this(menuItem, quantity, "");
    }

    // Getters
    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public String getSpecialInstructions() { return specialInstructions; }
}

enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
} 