package com.example.ShoppingCart.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.persistence.*;

@Entity
@Table(name = "items")
public class Item {
    private static final Logger logger = Logger.getLogger(Item.class.getName());
    private static final Map<String, Item> stockRegistry = new HashMap<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private int price;

    public Item(String name, int quantity, int price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (stockRegistry.containsKey(name)) {
            throw new IllegalArgumentException("Item with name '" + name + "' already exists");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        this.name = name;
        this.quantity = quantity;
        this.price = price;

        logger.info("Item created: " + name + " (ID: " + id + ", Qty: " + quantity + ", Price: " + price + ")");
        stockRegistry.put(name, this);
    }

    public static Item getItemByName(String name) {
        return stockRegistry.get(name);
    }

    public static Collection<Item> getAllItems() {
        return stockRegistry.values();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public void increaseQuantity(int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) throws IllegalArgumentException {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity -= quantity;
    }
}
