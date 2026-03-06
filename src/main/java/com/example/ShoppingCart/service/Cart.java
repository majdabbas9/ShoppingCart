package com.example.ShoppingCart.service;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Entity
@Table(name = "carts")
public class Cart {
    private static final Logger logger = Logger.getLogger(Cart.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "discount_percentage", nullable = false)
    private double discountPercentage = 0;

    protected Cart() {
    }

    public Cart(double discountPercentage) throws IllegalArgumentException {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Add an item to the cart with the specified quantity.
     * If the item already exists, adds to the existing quantity.
     */
    public void addItem(Item item, int quantity) throws IllegalArgumentException {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        CartItem existing = items.stream()
                .filter(ci -> ci.getStockItem().getName().equals(item.getName()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            logger.info("Updating quantity for existing item: " + item.getName() + " - adding " + quantity);
            existing.updateQuantity(existing.getQuantity() + quantity);
        } else {
            logger.info("Adding new item to cart: " + item.getName() + " with quantity " + quantity);
            CartItem newItem = new CartItem(item, quantity);
            newItem.setCart(this);
            items.add(newItem);
        }
    }

    public void removeItem(String itemName) {
        CartItem cartItem = items.stream()
                .filter(ci -> ci.getStockItem().getName().equals(itemName))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            logger.warning("Removing item from cart: " + itemName);
            cartItem.getStockItem().increaseQuantity(cartItem.getQuantity());
            items.remove(cartItem);
        } else {
            logger.warning("Attempted to remove non-existent item: " + itemName);
        }
    }

    public void removeItem(Item item) {
        if (item != null) {
            removeItem(item.getName());
        }
    }

    public void updateItemQuantity(String itemName, int quantity) throws IllegalArgumentException {
        CartItem cartItem = items.stream()
                .filter(ci -> ci.getStockItem().getName().equals(itemName))
                .findFirst()
                .orElse(null);

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
        return items.stream()
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
        for (CartItem item : items) {
            item.getStockItem().increaseQuantity(item.getQuantity());
        }
        items.clear();
        discountPercentage = 0;
    }

    /**
     * Get all items in the cart.
     */
    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
