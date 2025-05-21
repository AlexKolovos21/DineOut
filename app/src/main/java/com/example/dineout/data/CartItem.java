package com.example.dineout.data;

public class CartItem {
    private final MenuItem menuItem;
    private final int quantity;
    private final String specialInstructions;

    public CartItem(MenuItem menuItem, int quantity, String specialInstructions) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.specialInstructions = specialInstructions;
    }

    public CartItem(MenuItem menuItem, int quantity) {
        this(menuItem, quantity, "");
    }

    // Getters
    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public String getSpecialInstructions() { return specialInstructions; }
} 