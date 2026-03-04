package com.example.ShoppingCart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cart {
    private final Map<String, CartItem> items = new ConcurrentHashMap<>();
    private double discountPercentage = 0;

    public Cart(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
    }
    /**
     * Add an item to the cart with the specified quantity.
     * If the item already exists, adds to the existing quantity.
     */
    public void addItem(Item item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        String key = item.getName();
        if (items.containsKey(key)) {
            CartItem existing = items.get(key);
            existing.updateQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(key, new CartItem(item, quantity));
        }
    }

    public void removeItem(String itemName) {
        CartItem cartItem = items.get(itemName);
        cartItem.getStockItem().increaseQuantity(cartItem.getQuantity());
        items.remove(itemName);
    }

    public void removeItem(Item item) {
        CartItem cartItem = items.get(item.getName());
        cartItem.getStockItem().increaseQuantity(cartItem.getQuantity());
        items.remove(item.getName());
    }

    public void updateItemQuantity(String itemName, int quantity) {
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
    public void setDiscountPercentage(double discountPercentage) {
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
