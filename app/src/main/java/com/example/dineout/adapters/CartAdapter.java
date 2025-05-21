package com.example.dineout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final CartManager cartManager;
    private final NumberFormat currencyFormat;
    private List<Map.Entry<MenuItem, Integer>> cartItems;

    public CartAdapter(CartManager cartManager) {
        this.cartManager = cartManager;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        updateItems();
    }

    private void updateItems() {
        cartItems = new ArrayList<>(cartManager.getCartItems().entrySet());
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
        Map.Entry<MenuItem, Integer> entry = cartItems.get(position);
        holder.bind(entry.getKey(), entry.getValue());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void refreshItems() {
        updateItems();
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView priceText;
        private final TextView quantityText;
        private final MaterialButton decreaseButton;
        private final MaterialButton increaseButton;
        private final MaterialButton removeButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.cart_item_name);
            priceText = itemView.findViewById(R.id.cart_item_price);
            quantityText = itemView.findViewById(R.id.cart_item_quantity);
            decreaseButton = itemView.findViewById(R.id.decrease_quantity_button);
            increaseButton = itemView.findViewById(R.id.increase_quantity_button);
            removeButton = itemView.findViewById(R.id.remove_item_button);

            decreaseButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map.Entry<MenuItem, Integer> entry = cartItems.get(position);
                    int newQuantity = entry.getValue() - 1;
                    cartManager.updateItemQuantity(entry.getKey(), newQuantity);
                }
            });

            increaseButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map.Entry<MenuItem, Integer> entry = cartItems.get(position);
                    int newQuantity = entry.getValue() + 1;
                    cartManager.updateItemQuantity(entry.getKey(), newQuantity);
                }
            });

            removeButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map.Entry<MenuItem, Integer> entry = cartItems.get(position);
                    cartManager.removeItem(entry.getKey());
                }
            });
        }

        void bind(MenuItem item, int quantity) {
            nameText.setText(item.getName());
            priceText.setText(currencyFormat.format(item.getPrice() * quantity));
            quantityText.setText(String.valueOf(quantity));
            decreaseButton.setEnabled(quantity > 1);
        }
    }
} 