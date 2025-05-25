package com.example.dineout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dineout.data.MenuItem
import com.example.dineout.data.Order
import com.example.dineout.data.Restaurant
import com.example.dineout.data.sampleRestaurants
import com.example.dineout.ui.screens.*
import com.example.dineout.ui.theme.DineOutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DineOutTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    var currentRestaurant by remember { mutableStateOf<Restaurant?>(null) }
                    var favoriteRestaurants by remember { mutableStateOf(setOf<String>()) }
                    
                    // Cart state management
                    var cartItems by remember { mutableStateOf(mapOf<MenuItem, Int>()) }
                    var cartItemCount by remember { mutableStateOf(0) }
                    
                    // Order history state
                    var orderHistory by remember { mutableStateOf(listOf<Order>()) }
                    
                    // Function to update cart
                    val updateCart = { item: MenuItem, quantity: Int ->
                        val currentQuantity = cartItems[item] ?: 0
                        val newQuantity = quantity
                        
                        // Update total count
                        cartItemCount = cartItemCount - currentQuantity + (if (newQuantity > 0) newQuantity else 0)
                        
                        // Update cart items
                        cartItems = if (newQuantity > 0) {
                            cartItems + (item to newQuantity)
                        } else {
                            cartItems - item
                        }
                    }

                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onRestaurantClick = { restaurant ->
                                    currentRestaurant = restaurant
                                    navController.navigate("restaurant_detail")
                                },
                                onMapClick = {
                                    navController.navigate("map")
                                },
                                onHistoryClick = {
                                    navController.navigate("history")
                                },
                                favoriteRestaurants = favoriteRestaurants
                            )
                        }
                        composable("map") {
                            MapScreen(
                                onBackClick = { navController.popBackStack() },
                                onRestaurantClick = { restaurant ->
                                    currentRestaurant = restaurant
                                    navController.navigate("restaurant_detail")
                                },
                                favoriteRestaurants = favoriteRestaurants
                            )
                        }
                        composable("restaurant_detail") {
                            currentRestaurant?.let { restaurant ->
                                RestaurantDetailScreen(
                                    restaurant = restaurant,
                                    onBackClick = { navController.popBackStack() },
                                    onFavoriteClick = {
                                        favoriteRestaurants = if (restaurant.id in favoriteRestaurants) {
                                            favoriteRestaurants - restaurant.id
                                        } else {
                                            favoriteRestaurants + restaurant.id
                                        }
                                    },
                                    isFavorite = restaurant.id in favoriteRestaurants,
                                    onMenuClick = {
                                        navController.navigate("menu")
                                    }
                                )
                            }
                        }
                        composable("menu") {
                            currentRestaurant?.let { restaurant ->
                                MenuScreen(
                                    restaurant = restaurant,
                                    onBackClick = { navController.popBackStack() },
                                    onFavoriteClick = {
                                        favoriteRestaurants = if (restaurant.id in favoriteRestaurants) {
                                            favoriteRestaurants - restaurant.id
                                        } else {
                                            favoriteRestaurants + restaurant.id
                                        }
                                    },
                                    isFavorite = restaurant.id in favoriteRestaurants,
                                    onCartClick = {
                                        navController.navigate("cart")
                                    },
                                    cartItems = cartItems,
                                    cartItemCount = cartItemCount,
                                    onUpdateCart = updateCart
                                )
                            }
                        }
                        composable("cart") {
                            CartScreen(
                                restaurant = currentRestaurant,
                                cartItems = cartItems,
                                onBackClick = { navController.popBackStack() },
                                onCheckoutClick = { order ->
                                    // Add the order to history
                                    orderHistory = orderHistory + order
                                    
                                    // Clear cart after checkout but don't navigate away
                                    // The CartScreen will handle showing the confirmation
                                    cartItems = emptyMap()
                                    cartItemCount = 0
                                },
                                onUpdateQuantity = updateCart
                            )
                        }
                        composable("order_detail/{orderId}") { backStackEntry ->
                            val orderId = backStackEntry.arguments?.getString("orderId")
                            val order = orderHistory.find { it.id == orderId }
                            order?.let {
                                OrderDetailScreen(
                                    order = it,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                        composable("history") {
                            HistoryScreen(
                                orders = orderHistory,
                                onBackClick = { navController.popBackStack() },
                                onOrderClick = { order ->
                                    navController.navigate("order_detail/${order.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}