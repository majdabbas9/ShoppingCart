package com.example.ShoppingCart;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Item {
    private static int currID = 0;
    private static final Map<String, Item> stockRegistry = new HashMap<>();

    private final int id;
    private final String name;
    private int quantity;
    private final int price;

    public Item(String name, int quantity, int price) {
        if (name == null || name.trim().isEmpty()) {
            Logger.getInstance().error("Attempted to create item with empty name.");
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (stockRegistry.containsKey(name)) {
            Logger.getInstance().error("Attempted to create duplicate item: " + name);
            throw new IllegalArgumentException("Item with name '" + name + "' already exists");
        }
        if (quantity < 0) {
            Logger.getInstance().error("Attempted to create item with negative quantity: " + quantity);
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (price <= 0) {
            Logger.getInstance().error("Attempted to create item with non-positive price: " + price);
            throw new IllegalArgumentException("Price must be positive");
        }

        this.id = ++currID;
        this.name = name;
        this.quantity = quantity;
        this.price = price;

        Logger.getInstance()
                .info("Item created: " + name + " (ID: " + id + ", Qty: " + quantity + ", Price: " + price + ")");
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

    public void increaseQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity += quantity;
    }

    public void decreaseQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity -= quantity;
    }
}
