package com.example.dineout.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

public class Cart implements Parcelable {
    private Map<MenuItem, Integer> items;
    private double total;

    public Cart() {
        this.items = new HashMap<>();
        this.total = 0.0;
    }

    protected Cart(Parcel in) {
        items = new HashMap<>();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            MenuItem item = in.readParcelable(MenuItem.class.getClassLoader());
            int quantity = in.readInt();
            items.put(item, quantity);
        }
        total = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(items.size());
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            dest.writeParcelable(entry.getKey(), flags);
            dest.writeInt(entry.getValue());
        }
        dest.writeDouble(total);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    public Map<MenuItem, Integer> getItems() {
        return items;
    }

    public void addItem(MenuItem item) {
        items.put(item, items.getOrDefault(item, 0) + 1);
        updateTotal();
    }

    public void removeItem(MenuItem item) {
        if (items.containsKey(item)) {
            int quantity = items.get(item);
            if (quantity > 1) {
                items.put(item, quantity - 1);
            } else {
                items.remove(item);
            }
            updateTotal();
        }
    }

    public void clear() {
        items.clear();
        total = 0.0;
    }

    public double getTotal() {
        return total;
    }

    private void updateTotal() {
        total = 0.0;
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
    }
} 