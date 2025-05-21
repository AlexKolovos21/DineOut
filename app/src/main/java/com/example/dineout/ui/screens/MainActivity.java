package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dineout.R;
import com.example.dineout.adapters.RestaurantAdapter;
import com.example.dineout.data.Restaurant;
import com.example.dineout.utils.CartManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener {
    private RecyclerView restaurantRecyclerView;
    private ProgressBar progressBar;
    private RestaurantAdapter restaurantAdapter;
    private FloatingActionButton mapFab;
    private FloatingActionButton cartFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        restaurantRecyclerView = findViewById(R.id.restaurantRecyclerView);
        progressBar = findViewById(R.id.progress_bar);
        mapFab = findViewById(R.id.map_fab);
        cartFab = findViewById(R.id.cart_fab);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        // Setup RecyclerView
        restaurantAdapter = new RestaurantAdapter(this);
        restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantRecyclerView.setAdapter(restaurantAdapter);

        // Setup FABs
        mapFab.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        });

        cartFab.setOnClickListener(v -> {
            if (CartManager.getInstance().getCartItems().isEmpty()) {
                Toast.makeText(this, R.string.empty_cart, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, CartScreen.class);
                startActivity(intent);
            }
        });

        // Load restaurants
        loadRestaurants();
    }

    private void loadRestaurants() {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);
        restaurantRecyclerView.setVisibility(View.GONE);

        // Simulate loading data
        // In a real app, this would be an API call
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<Restaurant> restaurants = getDummyRestaurants();
            restaurantAdapter.updateRestaurants(restaurants);
            progressBar.setVisibility(View.GONE);
            restaurantRecyclerView.setVisibility(View.VISIBLE);
        }, 1000);
    }

    private List<Restaurant> getDummyRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(new Restaurant("1", "Greek Delight", "Greek", 4.5, 0.0, 0.0, "$$", "123 Main St", "Authentic Greek cuisine with a modern twist", ""));
        restaurants.add(new Restaurant("2", "Mediterranean Breeze", "Mediterranean", 4.3, 0.0, 0.0, "$$$", "456 Oak Ave", "Fresh seafood and Mediterranean specialties", ""));
        restaurants.add(new Restaurant("3", "Souvlaki Express", "Greek Fast Food", 4.0, 0.0, 0.0, "$", "789 Pine Rd", "Quick and delicious Greek street food", ""));
        return restaurants;
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            if (CartManager.getInstance().getCartItems().isEmpty()) {
                Toast.makeText(this, R.string.empty_cart, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, CartScreen.class);
                startActivity(intent);
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 