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
import com.example.dineout.managers.CartManager;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends ListAdapter<MenuItem, CartAdapter.CartViewHolder> {
    private final CartManager cartManager;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
    private Map<MenuItem, Integer> itemQuantities;

    public CartAdapter(CartManager cartManager) {
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
        this.cartManager = cartManager;
    }

    public void submitList(List<MenuItem> items, Map<MenuItem, Integer> quantities) {
        this.itemQuantities = quantities;
        super.submitList(items != null ? new ArrayList<>(items) : null);
    }

    @Override
    public void submitList(List<MenuItem> items) {
        if (cartManager != null) {
            submitList(items, cartManager.getCartItems());
        } else {
            super.submitList(items != null ? new ArrayList<>(items) : null);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        MenuItem item = getItem(position);
        int quantity = itemQuantities != null ? itemQuantities.getOrDefault(item, 0) : 0;
        holder.bind(item, quantity);
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemNameText;
        private final TextView itemPriceText;
        private final TextView quantityText;
        private final MaterialButton removeButton;
        private final MaterialButton addButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameText = itemView.findViewById(R.id.cart_item_name);
            itemPriceText = itemView.findViewById(R.id.cart_item_price);
            quantityText = itemView.findViewById(R.id.cart_item_quantity);
            removeButton = itemView.findViewById(R.id.remove_item_button);
            addButton = itemView.findViewById(R.id.increase_quantity_button);
        }

        void bind(MenuItem item, int quantity) {
            itemNameText.setText(item.getName());
            itemPriceText.setText(currencyFormat.format(item.getPrice()));
            quantityText.setText(String.valueOf(quantity));

            if (cartManager != null) {
                // Show buttons only if we're in cart mode
                removeButton.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);

                removeButton.setOnClickListener(v -> {
                    cartManager.removeItem(item);
                    notifyDataSetChanged();
                });

                addButton.setOnClickListener(v -> {
                    cartManager.addItem(item);
                    notifyDataSetChanged();
                });
            } else {
                // Hide buttons in order details mode
                removeButton.setVisibility(View.GONE);
                addButton.setVisibility(View.GONE);
            }
        }
    }
} 