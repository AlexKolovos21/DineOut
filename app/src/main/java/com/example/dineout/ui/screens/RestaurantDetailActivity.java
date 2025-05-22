package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.dineout.R;
import com.example.dineout.data.Restaurant;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {
    private TextView nameText;
    private TextView cuisineText;
    private TextView ratingText;
    private TextView addressText;
    private TextView descriptionText;
    private MaterialButton viewMenuButton;
    private MaterialButton viewMapButton;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Get restaurant from intent
        restaurant = getIntent().getParcelableExtra("restaurant");
        if (restaurant == null) {
            finish();
            return;
        }

        // Initialize views
        nameText = findViewById(R.id.restaurant_name);
        cuisineText = findViewById(R.id.restaurant_cuisine);
        ratingText = findViewById(R.id.restaurant_rating);
        addressText = findViewById(R.id.restaurant_address);
        descriptionText = findViewById(R.id.restaurant_description);
        viewMenuButton = findViewById(R.id.view_menu_button);
        viewMapButton = findViewById(R.id.view_map_button);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.restaurant_details);

        // Set restaurant details
        nameText.setText(restaurant.getName());
        cuisineText.setText(restaurant.getCuisine());
        ratingText.setText(getString(R.string.rating_format, restaurant.getRating()));
        addressText.setText(restaurant.getAddress());
        descriptionText.setText(restaurant.getDescription());

        // Setup view menu button
        viewMenuButton.setOnClickListener(v -> {
            // Populate menu items
            List<com.example.dineout.data.MenuItem> menuItems = new ArrayList<>();
            
            com.example.dineout.data.MenuItem item1 = new com.example.dineout.data.MenuItem("1", "Moussaka", "Traditional Greek dish with layers of eggplant and minced meat", 12.99);
            item1.setRestaurantInfo(restaurant);
            menuItems.add(item1);

            com.example.dineout.data.MenuItem item2 = new com.example.dineout.data.MenuItem("2", "Souvlaki", "Grilled meat skewers with pita bread", 8.99);
            item2.setRestaurantInfo(restaurant);
            menuItems.add(item2);

            com.example.dineout.data.MenuItem item3 = new com.example.dineout.data.MenuItem("3", "Greek Salad", "Fresh vegetables with feta cheese", 7.99);
            item3.setRestaurantInfo(restaurant);
            menuItems.add(item3);

            com.example.dineout.data.MenuItem item4 = new com.example.dineout.data.MenuItem("4", "Baklava", "Sweet pastry with nuts and honey", 5.99);
            item4.setRestaurantInfo(restaurant);
            menuItems.add(item4);

            restaurant.setMenu(menuItems);

            Intent intent = new Intent(this, MenuScreen.class);
            intent.putExtra("restaurant", restaurant);
            startActivity(intent);
        });

        // Setup view map button
        viewMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("restaurant", restaurant);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 