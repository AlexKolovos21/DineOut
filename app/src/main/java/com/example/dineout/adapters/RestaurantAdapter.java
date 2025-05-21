package com.example.dineout.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.data.Restaurant;
import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private final List<Restaurant> restaurants;
    private final OnRestaurantClickListener listener;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public RestaurantAdapter(OnRestaurantClickListener listener) {
        this.restaurants = new ArrayList<>();
        this.listener = listener;
    }

    public void updateRestaurants(List<Restaurant> newRestaurants) {
        restaurants.clear();
        restaurants.addAll(newRestaurants);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.bind(restaurant);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView cuisineText;
        private final TextView ratingText;
        private final TextView priceRangeText;

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.restaurant_name);
            cuisineText = itemView.findViewById(R.id.restaurant_cuisine);
            ratingText = itemView.findViewById(R.id.restaurant_rating);
            priceRangeText = itemView.findViewById(R.id.restaurant_price_range);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRestaurantClick(restaurants.get(position));
                }
            });
        }

        void bind(Restaurant restaurant) {
            nameText.setText(restaurant.getName());
            cuisineText.setText(restaurant.getCuisine());
            ratingText.setText(String.format("%.1f", restaurant.getRating()));
            priceRangeText.setText(restaurant.getPriceRange());
        }
    }
} 