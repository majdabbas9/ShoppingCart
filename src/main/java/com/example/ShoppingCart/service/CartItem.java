package com.example.ShoppingCart.service;

import jakarta.persistence.*;
import java.util.logging.Logger;

@Entity
@Table(name = "cart_items")
public class CartItem {
    private static final Logger logger = Logger.getLogger(CartItem.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item stockItem;

    @Column(nullable = false)
    private int quantity;

    protected CartItem() {
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
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
