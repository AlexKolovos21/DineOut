package com.example.dineout.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dineout.R;
import com.example.dineout.adapters.MenuAdapter;
import com.example.dineout.data.Restaurant;
import com.example.dineout.managers.CartManager;
import java.text.NumberFormat;
import java.util.Locale;

public class MenuScreen extends AppCompatActivity implements MenuAdapter.OnMenuItemClickListener, CartManager.CartUpdateListener {
    private Restaurant restaurant;
    private TextView restaurantNameText;
    private TextView restaurantCuisineText;
    private TextView restaurantRatingText;
    private RecyclerView menuRecyclerView;
    private MenuAdapter menuAdapter;
    private CartManager cartManager;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);

        // Get restaurant from intent
        restaurant = getIntent().getParcelableExtra("restaurant");
        if (restaurant == null) {
            Toast.makeText(this, "Error: Restaurant information not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        restaurantNameText = findViewById(R.id.restaurant_name);
        restaurantCuisineText = findViewById(R.id.restaurant_cuisine);
        restaurantRatingText = findViewById(R.id.restaurant_rating);
        menuRecyclerView = findViewById(R.id.menu_recycler_view);

        // Check if all required views were found
        if (restaurantNameText == null || restaurantCuisineText == null || 
            restaurantRatingText == null || menuRecyclerView == null) {
            Toast.makeText(this, "Error: Could not initialize UI components", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(restaurant.getName());
            }
        }

        // Set restaurant details
        restaurantNameText.setText(restaurant.getName());
        restaurantCuisineText.setText(restaurant.getCuisine());
        restaurantRatingText.setText(getString(R.string.rating_format, restaurant.getRating()));

        // Setup RecyclerView
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new MenuAdapter(restaurant.getMenu());
        menuAdapter.setOnMenuItemClickListener(this);
        menuRecyclerView.setAdapter(menuAdapter);

        // Initialize CartManager
        cartManager = CartManager.getInstance();
        cartManager.addCartUpdateListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(this, CartScreen.class);
            intent.putExtra("restaurant_id", restaurant.getId());
            intent.putExtra("restaurant_name", restaurant.getName());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(com.example.dineout.data.MenuItem item, int quantity) {
        for (int i = 0; i < quantity; i++) {
            cartManager.addItem(item);
        }
        Toast.makeText(this, quantity + "x " + item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCartUpdated() {
        // Update UI if needed
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeCartUpdateListener(this);
    }
} 