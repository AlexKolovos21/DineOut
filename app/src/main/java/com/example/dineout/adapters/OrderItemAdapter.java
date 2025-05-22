package com.example.dineout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.data.MenuItem;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderItemAdapter extends ListAdapter<MenuItem, OrderItemAdapter.OrderItemViewHolder> {
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
    private Map<MenuItem, Integer> itemQuantities;

    public OrderItemAdapter() {
        super(new DiffUtil.ItemCallback<MenuItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    public void submitList(List<MenuItem> items, Map<MenuItem, Integer> quantities) {
        this.itemQuantities = quantities;
        super.submitList(items != null ? new ArrayList<>(items) : null);
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_order, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        MenuItem item = getItem(position);
        int quantity = itemQuantities != null ? itemQuantities.getOrDefault(item, 0) : 0;
        holder.bind(item, quantity);
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemNameText;
        private final TextView itemPriceText;
        private final TextView quantityText;

        OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameText = itemView.findViewById(R.id.order_item_name);
            itemPriceText = itemView.findViewById(R.id.order_item_price);
            quantityText = itemView.findViewById(R.id.order_item_quantity);
        }

        void bind(MenuItem item, int quantity) {
            itemNameText.setText(item.getName());
            itemPriceText.setText(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(item.getPrice()));
            quantityText.setText("x" + quantity);
        }
    }
} 