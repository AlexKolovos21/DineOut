package com.example.dineout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.adapters.RestaurantAdapter;
import com.example.dineout.data.Restaurant;
import com.example.dineout.ui.screens.CartScreen;
import com.example.dineout.ui.screens.OrderHistoryScreen;
import com.example.dineout.ui.screens.RestaurantDetailScreen;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RestaurantAdapter.OnRestaurantClickListener {
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.restaurantRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantAdapter(this);
        recyclerView.setAdapter(adapter);

        // Load sample data
        loadSampleData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cart) {
            startActivity(new Intent(this, CartScreen.class));
            return true;
        } else if (id == R.id.action_history) {
            startActivity(new Intent(this, OrderHistoryScreen.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailScreen.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    private void loadSampleData() {
        List<Restaurant> restaurants = new ArrayList<>();
        
        // Sample restaurant 1
        Restaurant restaurant1 = new Restaurant(
            "1",
            "Greek Delight",
            "Greek",
            4.5,
            37.9715, 23.7267,
            "123 Main St",
            "+1 555-0123",
            "Authentic Greek cuisine with a modern twist",
            "$$"
        );
        restaurants.add(restaurant1);

        // Sample restaurant 2
        Restaurant restaurant2 = new Restaurant(
            "2",
            "Mediterranean Breeze",
            "Mediterranean",
            4.3,
            37.9738, 23.7275,
            "456 Oak Ave",
            "+1 555-0124",
            "Fresh seafood and Mediterranean specialties",
            "$$$"
        );
        restaurants.add(restaurant2);

        // Sample restaurant 3
        Restaurant restaurant3 = new Restaurant(
            "3",
            "Souvlaki Express",
            "Greek Fast Food",
            4.0,
            37.9750, 23.7280,
            "789 Pine Rd",
            "+1 555-0125",
            "Quick and delicious Greek street food",
            "$"
        );
        restaurants.add(restaurant3);

        adapter.updateRestaurants(restaurants);
    }
} 