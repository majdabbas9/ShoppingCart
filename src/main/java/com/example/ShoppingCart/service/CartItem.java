package com.example.ShoppingCart.service;

import java.util.logging.Logger;

public class CartItem {
    private static final Logger logger = Logger.getLogger(CartItem.class.getName());
    private final Item stockItem;
    private int quantity;

    public CartItem(Item stockItem, int quantity) throws IllegalArgumentException {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > stockItem.getQuantity()) {
            throw new IllegalArgumentException("Not enough quantity in stock for item " + stockItem.getName());
        }
        stockItem.decreaseQuantity(quantity);
        this.stockItem = stockItem;
        this.quantity = quantity;
    }

    public Item getStockItem() {
        return stockItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void updateQuantity(int newQuantity) throws IllegalArgumentException {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (newQuantity - this.quantity > stockItem.getQuantity()) {
            throw new IllegalArgumentException("Not enough quantity in stock");
        }
        if (newQuantity > this.quantity) {
            try {
                this.stockItem.decreaseQuantity(newQuantity - this.quantity);
            } catch (IllegalArgumentException e) {
                logger.severe("Failed to update quantity: " + e.getMessage());
            }
        } else {
            try {
                this.stockItem.increaseQuantity(this.quantity - newQuantity);
            } catch (IllegalArgumentException e) {
                logger.severe("Failed to update quantity: " + e.getMessage());
            }
        }
        this.quantity = newQuantity;
    }

    public int getTotalPrice() {
        return this.quantity * stockItem.getPrice();
    }
}
