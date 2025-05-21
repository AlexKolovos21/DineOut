package com.example.dineout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.data.MenuItem;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
    private List<MenuItem> menuItems;
    private OnMenuItemClickListener listener;
    private final NumberFormat currencyFormat;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem item, int quantity);
    }

    public MenuAdapter(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        this.listener = listener;
    }

    public void updateItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView descriptionText;
        private final TextView priceText;
        private final TextView quantityText;
        private final MaterialButton decreaseButton;
        private final MaterialButton increaseButton;
        private final MaterialButton addToCartButton;
        private int quantity = 1;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.menu_item_name);
            descriptionText = itemView.findViewById(R.id.menu_item_description);
            priceText = itemView.findViewById(R.id.menu_item_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            decreaseButton = itemView.findViewById(R.id.decrease_quantity_button);
            increaseButton = itemView.findViewById(R.id.increase_quantity_button);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);

            decreaseButton.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    updateQuantity();
                }
            });

            increaseButton.setOnClickListener(v -> {
                quantity++;
                updateQuantity();
            });

            addToCartButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMenuItemClick(menuItems.get(position), quantity);
                    // Reset quantity after adding to cart
                    quantity = 1;
                    updateQuantity();
                }
            });
        }

        private void updateQuantity() {
            quantityText.setText(String.valueOf(quantity));
            decreaseButton.setEnabled(quantity > 1);
        }

        void bind(MenuItem item) {
            nameText.setText(item.getName());
            descriptionText.setText(item.getDescription());
            priceText.setText(currencyFormat.format(item.getPrice()));
            quantity = 1;
            updateQuantity();
        }
    }
} 