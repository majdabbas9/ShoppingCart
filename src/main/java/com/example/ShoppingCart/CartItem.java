package com.example.ShoppingCart;

public class CartItem {
    private final Item stockItem;
    private int quantity;

    public CartItem(Item stockItem, int quantity) {
        if (quantity <= 0) {
            Logger.getInstance().error("Attempted to create CartItem with non-positive quantity: " + quantity);
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > stockItem.getQuantity()) {
            Logger.getInstance().error("Not enough stock for item: " + stockItem.getName() + " (Required: " + quantity
                    + ", Available: " + stockItem.getQuantity() + ")");
            throw new IllegalArgumentException("Not enough quantity in stock");
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

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (newQuantity - this.quantity > stockItem.getQuantity()) {
            throw new IllegalArgumentException("Not enough quantity in stock");
        }
        if (newQuantity > this.quantity) {
            this.stockItem.decreaseQuantity(newQuantity - this.quantity);
        } else {
            this.stockItem.increaseQuantity(this.quantity - newQuantity);
        }
        this.quantity = newQuantity;
    }

    public int getTotalPrice() {
        return this.quantity * stockItem.getPrice();
    }
}
