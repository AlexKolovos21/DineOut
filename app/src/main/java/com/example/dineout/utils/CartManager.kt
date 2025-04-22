package com.example.dineout.utils

import com.example.dineout.data.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/**
 * A utility class to manage the shopping cart functionality across the application.
 * This class provides methods for adding, removing, and updating cart items.
 */
object CartManager {
    private val _cartItems = MutableStateFlow<Map<MenuItem, Int>>(emptyMap())
    val cartItems: StateFlow<Map<MenuItem, Int>> = _cartItems
    
    /**
     * Adds an item to the cart or increases its quantity if already present
     */
    fun addItem(item: MenuItem, quantity: Int = 1) {
        _cartItems.update { currentItems ->
            val currentQuantity = currentItems[item] ?: 0
            currentItems + (item to currentQuantity + quantity)
        }
    }
    
    /**
     * Updates the quantity of an item in the cart
     */
    fun updateQuantity(item: MenuItem, quantity: Int) {
        _cartItems.update { currentItems ->
            if (quantity <= 0) {
                currentItems - item
            } else {
                currentItems + (item to quantity)
            }
        }
    }
    
    /**
     * Removes an item from the cart
     */
    fun removeItem(item: MenuItem) {
        _cartItems.update { currentItems ->
            currentItems - item
        }
    }
    
    /**
     * Clears all items from the cart
     */
    fun clearCart() {
        _cartItems.update { emptyMap() }
    }
    
    /**
     * Calculates the total price of all items in the cart
     */
    fun calculateTotal(): Double {
        return _cartItems.value.entries.sumOf { (item, quantity) -> 
            item.price * quantity 
        }
    }
    
    /**
     * Checks if the cart is empty
     */
    fun isEmpty(): Boolean {
        return _cartItems.value.isEmpty()
    }
    
    /**
     * Returns the total number of items in the cart
     */
    fun getTotalItems(): Int {
        return _cartItems.value.values.sum()
    }
} 