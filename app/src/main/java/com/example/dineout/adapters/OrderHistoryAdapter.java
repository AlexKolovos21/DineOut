package com.example.dineout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.data.Order;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderHistoryAdapter extends ListAdapter<Order, OrderHistoryAdapter.OrderViewHolder> {
    private final OnOrderClickListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderHistoryAdapter(OnOrderClickListener listener) {
        super(new DiffUtil.ItemCallback<Order>() {
            @Override
            public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getItem(position);
        holder.bind(order);
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView restaurantNameText;
        private final TextView orderDateText;
        private final TextView orderStatusText;
        private final TextView totalAmountText;
        private final RecyclerView itemsRecyclerView;
        private final OrderItemAdapter orderItemAdapter;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantNameText = itemView.findViewById(R.id.restaurant_name);
            orderDateText = itemView.findViewById(R.id.order_date);
            orderStatusText = itemView.findViewById(R.id.order_status);
            totalAmountText = itemView.findViewById(R.id.total_amount);
            itemsRecyclerView = itemView.findViewById(R.id.items_recycler_view);

            orderItemAdapter = new OrderItemAdapter();
            itemsRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            itemsRecyclerView.setAdapter(orderItemAdapter);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onOrderClick(getItem(position));
                }
            });
        }

        void bind(Order order) {
            restaurantNameText.setText(order.getRestaurantName());
            orderDateText.setText(order.getFormattedDate());
            orderStatusText.setText(order.getStatus());
            totalAmountText.setText(currencyFormat.format(order.getTotalAmount()));

            // Set order items
            orderItemAdapter.submitList(new ArrayList<>(order.getItems().keySet()), order.getItems());
        }
    }
} 