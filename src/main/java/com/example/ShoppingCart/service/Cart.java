package com.example.ShoppingCart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class Cart {
    private static final Logger logger = Logger.getLogger(Cart.class.getName());
    private final Map<String, CartItem> items = new ConcurrentHashMap<>();
    private double discountPercentage = 0;

    public Cart(double discountPercentage) throws IllegalArgumentException {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
    }

    /**
     * Add an item to the cart with the specified quantity.
     * If the item already exists, adds to the existing quantity.
     */
    public void addItem(Item item, int quantity) throws IllegalArgumentException {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        String key = item.getName();
        if (items.containsKey(key)) {
            CartItem existing = items.get(key);
            logger.info("Updating quantity for existing item: " + key + " - adding " + quantity);
            existing.updateQuantity(existing.getQuantity() + quantity);
        } else {
            logger.info("Adding new item to cart: " + key + " with quantity " + quantity);
            items.put(key, new CartItem(item, quantity));
        }
    }

    public void removeItem(String itemName) {
        CartItem cartItem = items.get(itemName);
        if (cartItem != null) {
            logger.warning("Removing item from cart: " + itemName);
            cartItem.getStockItem().increaseQuantity(cartItem.getQuantity());
            items.remove(itemName);
        } else {
            logger.warning("Attempted to remove non-existent item: " + itemName);
        }
    }

    public void removeItem(Item item) {
        CartItem cartItem = items.get(item.getName());
        cartItem.getStockItem().increaseQuantity(cartItem.getQuantity());
        items.remove(item.getName());
    }

    public void updateItemQuantity(String itemName, int quantity) throws IllegalArgumentException {
        CartItem cartItem = items.get(itemName);
        if (cartItem == null) {
            throw new IllegalArgumentException("Item not in cart: " + itemName);
        }
        if (quantity <= 0) {
            this.removeItem(itemName);
        } else {
            cartItem.updateQuantity(quantity);
        }
    }

    public int getSubtotal() {
        return items.values().stream()
                .mapToInt(CartItem::getTotalPrice)
                .sum();
    }

    /**
     * Apply a discount percentage (0-100).
     */
    public void setDiscountPercentage(double discountPercentage) throws IllegalArgumentException {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * Calculate final total after discount.
     */
    public double getFinalTotal() {
        int subtotal = getSubtotal();
        double discountAmount = subtotal * (discountPercentage / 100.0);
        return subtotal - discountAmount;
    }

    /**
     * Clear the entire cart.
     */
    public void clear() {
        logger.info("Clearing the cart...");
        for (CartItem item : items.values()) {
            item.getStockItem().increaseQuantity(item.getQuantity());
        }
        items.clear();
        discountPercentage = 0;
    }

    /**
     * Get all items in the cart.
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
