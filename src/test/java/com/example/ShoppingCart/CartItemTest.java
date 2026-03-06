package com.example.ShoppingCart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.example.ShoppingCart.service.CartItem;
import com.example.ShoppingCart.service.Item;

@DisplayName("CartItem Logic Tests")
class CartItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item("TestItem-" + System.nanoTime(), 20, 75);
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {
        @Test
        // creates cart item with valid quantity and decreases stock
        void createsCartItemWithValidQuantity() {
            CartItem cartItem = new CartItem(item, 5);
            assertEquals(5, cartItem.getQuantity());
            assertEquals(15, item.getQuantity());
        }

        @Test
        // decreases stock quantity when cart item is created
        void decreasesStockQuantity() {
            new CartItem(item, 5);
            assertEquals(15, item.getQuantity());
        }

        @Test
        // two cart items with same item both decrease stock
        void twoCartItemsWithSameItemBothDecreaseStock() {
            new CartItem(item, 5);
            assertEquals(15, item.getQuantity());
            new CartItem(item, 3);
            assertEquals(12, item.getQuantity());
        }

        @Test
        // checking if throws when quantity is zero
        void throwsWhenQuantityZero() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new CartItem(item, 0));
            assertEquals("Quantity must be positive", ex.getMessage());
        }

        @Test
        // checking if throws when quantity is negative
        void throwsWhenQuantityNegative() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new CartItem(item, -1));
            assertEquals("Quantity must be positive", ex.getMessage());
        }

        @Test
        // checking if throws when quantity exceeds stock
        void throwsWhenQuantityExceedsStock() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new CartItem(item, 25));
            assertTrue(ex.getMessage().startsWith("Not enough quantity in stock"));
        }
    }

    @Nested
    @DisplayName("update Quantity")
    class UpdateQuantityTests {
        @Test
        // checking if quantity incease done correctly
        void increasesQuantity() {
            CartItem cartItem = new CartItem(item, 5);
            cartItem.updateQuantity(8);
            assertEquals(8, cartItem.getQuantity());
            assertEquals(12, item.getQuantity());
        }

        @Test
        // checking if quantity decrease done correcrly
        void decreasesQuantity() {
            CartItem cartItem = new CartItem(item, 10);
            cartItem.updateQuantity(3);
            assertEquals(3, cartItem.getQuantity());
            assertEquals(17, item.getQuantity());
        }

        @Test
        // checking if throws when updating quantity to 0
        void throwsWhenNewQuantityZero() {
            CartItem cartItem = new CartItem(item, 5);
            assertThrows(IllegalArgumentException.class, () -> cartItem.updateQuantity(0));
        }

        @Test
        // checking if throws when updating quanitity to negative
        void throwsWhenNewQuantityNegative() {
            CartItem cartItem = new CartItem(item, 5);
            assertThrows(IllegalArgumentException.class, () -> cartItem.updateQuantity(-1));
        }

        @Test
        // checking if throws when not sufficient quantity
        void throwsWhenIncreaseExceedsStock() {
            CartItem cartItem = new CartItem(item, 5);
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> cartItem.updateQuantity(25));
            assertEquals("Not enough quantity in stock", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("get Item's Total Price")
    // asserting total price is correctly calculated
    class GetTotalPriceTests {
        @Test
        void totalPriceSingleQuantity() {
            CartItem cartItem = new CartItem(item, 1);
            assertEquals(75, cartItem.getTotalPrice());
        }

        @Test
        void totalPriceMultipleQuantity() {
            CartItem cartItem = new CartItem(item, 4);
            assertEquals(300, cartItem.getTotalPrice());
        }

        @Test
        void totalPriceUpdatesAfterQuantityChange() {
            CartItem cartItem = new CartItem(item, 2);
            assertEquals(150, cartItem.getTotalPrice());
            cartItem.updateQuantity(5);
            assertEquals(375, cartItem.getTotalPrice());
        }
    }
}
