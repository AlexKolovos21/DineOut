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
import com.example.dineout.data.MenuItem;
import com.example.dineout.managers.CartManager;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
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
    private TextView cartItemsCountText;
    private TextView cartTotalText;
    private MaterialButton viewCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_screen);

        // Get restaurant data from intent
        restaurant = getIntent().getParcelableExtra("restaurant");
        if (restaurant == null) {
            Toast.makeText(this, "Error: Restaurant data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        restaurantNameText = findViewById(R.id.restaurant_name);
        restaurantCuisineText = findViewById(R.id.restaurant_cuisine);
        restaurantRatingText = findViewById(R.id.restaurant_rating);
        menuRecyclerView = findViewById(R.id.menu_recycler_view);
        cartItemsCountText = findViewById(R.id.cart_items_count);
        cartTotalText = findViewById(R.id.cart_total);
        viewCartButton = findViewById(R.id.view_cart_button);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(restaurant.getName());

        // Set restaurant details
        restaurantNameText.setText(restaurant.getName());
        restaurantCuisineText.setText(restaurant.getCuisine());
        restaurantRatingText.setText(String.format(Locale.getDefault(), "%.1f ★", restaurant.getRating()));

        // Setup RecyclerView
        menuAdapter = new MenuAdapter(new ArrayList<>());
        menuAdapter.setOnMenuItemClickListener(this);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuRecyclerView.setAdapter(menuAdapter);

        // Initialize CartManager
        cartManager = CartManager.getInstance();
        cartManager.addCartUpdateListener(this);

        // Setup view cart button
        viewCartButton.setOnClickListener(v -> {
            if (cartManager.getTotalItems() > 0) {
                Intent intent = new Intent(this, CartScreen.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Load menu items
        loadMenuItems();
    }

    private void loadMenuItems() {
        // TODO: Load menu items from API or database
        // For now, create some sample menu items
        List<MenuItem> menuItems = new ArrayList<>();
        
        // Add sample menu items based on restaurant cuisine
        String cuisine = restaurant.getCuisine().toLowerCase();
        if (cuisine.contains("greek")) {
            MenuItem item1 = new MenuItem("1", "Moussaka", "Layers of eggplant, potatoes, and ground meat topped with béchamel sauce", 15.99);
            item1.setRestaurantId(restaurant.getId());
            item1.setRestaurantName(restaurant.getName());
            menuItems.add(item1);

            MenuItem item2 = new MenuItem("2", "Souvlaki", "Grilled meat skewers served with pita bread and tzatziki", 12.99);
            item2.setRestaurantId(restaurant.getId());
            item2.setRestaurantName(restaurant.getName());
            menuItems.add(item2);

            MenuItem item3 = new MenuItem("3", "Greek Salad", "Fresh vegetables with feta cheese and olive oil", 8.99);
            item3.setRestaurantId(restaurant.getId());
            item3.setRestaurantName(restaurant.getName());
            menuItems.add(item3);
        } else if (cuisine.contains("mediterranean")) {
            MenuItem item4 = new MenuItem("4", "Hummus", "Chickpea dip with olive oil and spices", 6.99);
            item4.setRestaurantId(restaurant.getId());
            item4.setRestaurantName(restaurant.getName());
            menuItems.add(item4);

            MenuItem item5 = new MenuItem("5", "Falafel Plate", "Crispy chickpea patties with tahini sauce", 11.99);
            item5.setRestaurantId(restaurant.getId());
            item5.setRestaurantName(restaurant.getName());
            menuItems.add(item5);

            MenuItem item6 = new MenuItem("6", "Shawarma", "Marinated meat wrapped in pita bread", 13.99);
            item6.setRestaurantId(restaurant.getId());
            item6.setRestaurantName(restaurant.getName());
            menuItems.add(item6);
        } else {
            MenuItem item7 = new MenuItem("7", "Burger", "Classic beef burger with cheese and vegetables", 9.99);
            item7.setRestaurantId(restaurant.getId());
            item7.setRestaurantName(restaurant.getName());
            menuItems.add(item7);

            MenuItem item8 = new MenuItem("8", "Fries", "Crispy golden fries", 4.99);
            item8.setRestaurantId(restaurant.getId());
            item8.setRestaurantName(restaurant.getName());
            menuItems.add(item8);

            MenuItem item9 = new MenuItem("9", "Milkshake", "Creamy vanilla milkshake", 5.99);
            item9.setRestaurantId(restaurant.getId());
            item9.setRestaurantName(restaurant.getName());
            menuItems.add(item9);
        }
        
        menuAdapter.updateItems(menuItems);
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
            if (cartManager.getTotalItems() > 0) {
                Intent intent = new Intent(this, CartScreen.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMenuItemClick(MenuItem item, int quantity) {
        for (int i = 0; i < quantity; i++) {
            cartManager.addItem(item);
        }
        Toast.makeText(this, quantity + "x " + item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCartUpdated() {
        int totalItems = cartManager.getTotalItems();
        double total = cartManager.getTotal();
        
        cartItemsCountText.setText(totalItems + " items");
        cartTotalText.setText(currencyFormat.format(total));
        viewCartButton.setEnabled(totalItems > 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeCartUpdateListener(this);
    }
} 