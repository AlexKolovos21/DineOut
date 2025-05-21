package com.example.dineout;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.foundation.layout.fillMaxSize;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.material3.Surface;
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier;
import androidx.navigation.compose.NavHost;
import androidx.navigation.compose.composable;
import androidx.navigation.compose.rememberNavController;
import com.example.dineout.data.MenuItem;
import com.example.dineout.data.Order;
import com.example.dineout.data.Restaurant;
import com.example.dineout.ui.screens.*;
import com.example.dineout.ui.theme.DineOutTheme;
import java.util.*;

public class MainActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContent(() -> {
            DineOutTheme.INSTANCE.apply(() -> {
                Surface surface = new Surface(
                    Modifier.Companion.fillMaxSize(),
                    MaterialTheme.INSTANCE.getColorScheme().getBackground()
                );
                
                NavController navController = rememberNavController();
                MutableState<Restaurant> currentRestaurant = remember(new MutableState<Restaurant>(null));
                MutableState<Set<String>> favoriteRestaurants = remember(new MutableState<>(new HashSet<>()));
                
                // Cart state management
                MutableState<Map<MenuItem, Integer>> cartItems = remember(new MutableState<>(new HashMap<>()));
                MutableState<Integer> cartItemCount = remember(new MutableState<>(0));
                
                // Order history state
                MutableState<List<Order>> orderHistory = remember(new MutableState<>(new ArrayList<>()));
                
                // Function to update cart
                Function2<MenuItem, Integer, Unit> updateCart = (item, quantity) -> {
                    int currentQuantity = cartItems.getValue().getOrDefault(item, 0);
                    int newQuantity = quantity;
                    
                    // Update total count
                    cartItemCount.setValue(cartItemCount.getValue() - currentQuantity + (newQuantity > 0 ? newQuantity : 0));
                    
                    // Update cart items
                    Map<MenuItem, Integer> newCartItems = new HashMap<>(cartItems.getValue());
                    if (newQuantity > 0) {
                        newCartItems.put(item, newQuantity);
                    } else {
                        newCartItems.remove(item);
                    }
                    cartItems.setValue(newCartItems);
                    return Unit.INSTANCE;
                };

                NavHost navHost = new NavHost(navController, "home");
                navHost.addComposable("home", () -> {
                    HomeScreen.INSTANCE.apply(
                        restaurant -> {
                            currentRestaurant.setValue(restaurant);
                            navController.navigate("restaurant_detail");
                        },
                        () -> navController.navigate("map"),
                        () -> navController.navigate("history"),
                        favoriteRestaurants.getValue()
                    );
                });
                
                navHost.addComposable("map", () -> {
                    MapScreen.INSTANCE.apply(
                        () -> navController.popBackStack(),
                        restaurant -> {
                            currentRestaurant.setValue(restaurant);
                            navController.navigate("restaurant_detail");
                        },
                        favoriteRestaurants.getValue()
                    );
                });
                
                navHost.addComposable("restaurant_detail", () -> {
                    Restaurant restaurant = currentRestaurant.getValue();
                    if (restaurant != null) {
                        RestaurantDetailScreen.INSTANCE.apply(
                            restaurant,
                            () -> navController.popBackStack(),
                            () -> {
                                Set<String> newFavorites = new HashSet<>(favoriteRestaurants.getValue());
                                if (newFavorites.contains(restaurant.getId())) {
                                    newFavorites.remove(restaurant.getId());
                                } else {
                                    newFavorites.add(restaurant.getId());
                                }
                                favoriteRestaurants.setValue(newFavorites);
                            },
                            favoriteRestaurants.getValue().contains(restaurant.getId()),
                            () -> navController.navigate("menu")
                        );
                    }
                });
                
                navHost.addComposable("menu", () -> {
                    Restaurant restaurant = currentRestaurant.getValue();
                    if (restaurant != null) {
                        MenuScreen.INSTANCE.apply(
                            restaurant,
                            () -> navController.popBackStack(),
                            () -> {
                                Set<String> newFavorites = new HashSet<>(favoriteRestaurants.getValue());
                                if (newFavorites.contains(restaurant.getId())) {
                                    newFavorites.remove(restaurant.getId());
                                } else {
                                    newFavorites.add(restaurant.getId());
                                }
                                favoriteRestaurants.setValue(newFavorites);
                            },
                            favoriteRestaurants.getValue().contains(restaurant.getId()),
                            () -> navController.navigate("cart"),
                            cartItems.getValue(),
                            cartItemCount.getValue(),
                            updateCart
                        );
                    }
                });
                
                navHost.addComposable("cart", () -> {
                    CartScreen.INSTANCE.apply(
                        currentRestaurant.getValue(),
                        cartItems.getValue(),
                        () -> navController.popBackStack(),
                        order -> {
                            List<Order> newHistory = new ArrayList<>(orderHistory.getValue());
                            newHistory.add(order);
                            orderHistory.setValue(newHistory);
                            
                            cartItems.setValue(new HashMap<>());
                            cartItemCount.setValue(0);
                        },
                        updateCart
                    );
                });
                
                navHost.addComposable("history", () -> {
                    HistoryScreen.INSTANCE.apply(
                        orderHistory.getValue(),
                        () -> navController.popBackStack(),
                        order -> {
                            // Could navigate to order details in future
                        }
                    );
                });
                
                return surface;
            });
        });
    }
} 