package com.example.ShoppingCart;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Cart Logic Tests")
class CartTest {

    private Cart cart;
    private Item apple;
    private Item banana;

    @BeforeEach
    void setUp() {
        cart = new Cart(0);
        apple = new Item("Apple-" + System.nanoTime(), 10, 100);
        banana = new Item("Banana-" + System.nanoTime(), 5, 50);
    }

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {
        @Test
        // assert the constructor does not throw an error
        void validDiscountPercentage() {
            assertDoesNotThrow(() -> new Cart(0));
            assertDoesNotThrow(() -> new Cart(50));
            assertDoesNotThrow(() -> new Cart(100));
        }

        @Test
        // assert if constructor throw an error when Discount is less than 0
        void throwsWhenDiscountNegative() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Cart(-1));
            assertEquals("Discount must be between 0 and 100", ex.getMessage());
        }

        @Test
        // assert if constructor throw an error when Discount is more than 100
        void throwsWhenDiscountOver100() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> new Cart(101));
            assertEquals("Discount must be between 0 and 100", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("add Item")
    class AddItemTests {
        @Test
        // assert when adding an item to the cart the item stock is updated and the quantity of that item in the cart is updated
        void addsNewItemToCart() {
            cart.addItem(apple, 2);
            List<CartItem> items = cart.getItems();
            assertEquals(1, items.size());
            assertEquals(apple.getName(), items.get(0).getStockItem().getName());
            assertEquals(2, items.get(0).getQuantity());
            assertEquals(8, apple.getQuantity());
        }

        @Test
        // assert if adding the same item twice will update the item quantity correctly
        void incrementsQuantityWhenItemExists() {
            cart.addItem(apple, 2);
            cart.addItem(apple, 3);
            List<CartItem> items = cart.getItems();
            assertEquals(1, items.size());
            assertEquals(5, items.get(0).getQuantity());
            assertEquals(5, apple.getQuantity());
        }

        @Test
        // assert if the constructor throws an error when adding null item to cart
        void throwsWhenItemIsNull() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> cart.addItem(null, 1));
            assertEquals("Item cannot be null", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("remove Item")
    class RemoveItemTests {
        @Test
        // assert when remving an item from cart the item stock is updated correctly
        void removesItemByName() {
            cart.addItem(apple, 2);
            cart.removeItem(apple.getName());
            assertEquals(10, apple.getQuantity());
            assertTrue(cart.isEmpty());
        }

        @Test
        // same as above but with removing by object
        void removesItemByObject() {
            cart.addItem(apple, 2);
            cart.removeItem(apple);
            assertEquals(10, apple.getQuantity());
            assertTrue(cart.isEmpty());
        }
    }

    @Nested
    @DisplayName("update Item's Quantity")
    class UpdateItemQuantityTests {
        @Test
        // assert if updating item quantity will change the item stock
        void updatesQuantity() {
            cart.addItem(apple, 2);
            assertEquals(8, apple.getQuantity());
            cart.updateItemQuantity(apple.getName(), 4);
            assertEquals(4, cart.getItems().get(0).getQuantity());
            assertEquals(6, apple.getQuantity());
        }

        @Test
        // assert when updating the item quantity to 0 it will be deleted from the cart
        void removesItemWhenQuantityZero() {
            cart.addItem(apple, 2);
            assertEquals(8, apple.getQuantity());
            cart.updateItemQuantity(apple.getName(), 0);
            assertTrue(cart.isEmpty());
            assertEquals(10, apple.getQuantity());
        }

        @Test
        void removesItemWhenQuantityNegative() {
            // same as above but with negative value
            cart.addItem(apple, 2);
            assertEquals(8, apple.getQuantity());
            cart.updateItemQuantity(apple.getName(), -1);
            assertEquals(10, apple.getQuantity());
            assertTrue(cart.isEmpty());
        }

        @Test
        // assert if update item quantity will throw and error if the item dont exist
        void throwsWhenItemNotInCart() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> cart.updateItemQuantity("NonExistent", 5));
            assertEquals("Item not in cart: NonExistent", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Subtotal and Totals")
    class SubtotalAndTotalsTests {
        @Test
        void subtotalWithSingleItem() {
            cart.addItem(apple, 2);
            assertEquals(200, cart.getSubtotal()); // 2 * 100
        }

        @Test
        void subtotalWithMultipleItems() {
            cart.addItem(apple, 2); // 2 * 100
            cart.addItem(banana, 3);// 3 * 50
            assertEquals(350, cart.getSubtotal()); // 2 * 100 + 3 * 50
        }

        @Test
        void subtotalEmptyCart() {
            assertEquals(0, cart.getSubtotal());
        }

        @Test
        void finalTotalWithNoDiscount() {
            cart.addItem(apple, 2); // 2 * 100
            assertEquals(200.0, cart.getFinalTotal(), 0.001);
        }

        @Test
        void finalTotalWithDiscount() {
            cart.addItem(apple, 2); // 2 * 100
            cart.setDiscountPercentage(10);
            assertEquals(180.0, cart.getFinalTotal(), 0.001); // 200 * 100 * 0.8
        }

        @Test
        void finalTotalWithFullDiscount() {
            cart.addItem(apple, 2);
            cart.setDiscountPercentage(100);
            assertEquals(0.0, cart.getFinalTotal(), 0.001);
        }
    }

    @Nested
    @DisplayName("Discount")
    class DiscountTests {
        @Test
        void setDiscountPercentageValid() {
            assertDoesNotThrow(() -> cart.setDiscountPercentage(25));
            assertEquals(25, cart.getDiscountPercentage());
        }

        @Test
        void setDiscountPercentageThrowsWhenNegative() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> cart.setDiscountPercentage(-1));
            assertEquals("Discount must be between 0 and 100", ex.getMessage());
        }

        @Test
        void setDiscountPercentageThrowsWhenOver100() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> cart.setDiscountPercentage(101));
            assertEquals("Discount must be between 0 and 100", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("clear and isEmpty")
    class ClearAndEmptyTests {
        @Test
        void clearRemovesAllItems() {
            cart.addItem(apple, 2);
            cart.addItem(banana, 1);
            cart.clear();
            assertEquals(10, apple.getQuantity());
            assertEquals(5, banana.getQuantity());
            assertTrue(cart.isEmpty());
            assertEquals(0, cart.getItems().size());
        }

        @Test
        void clearResetsDiscount() {
            cart.addItem(apple, 1);
            cart.setDiscountPercentage(20);
            cart.clear();
            assertEquals(10, apple.getQuantity());
            assertEquals(0, cart.getDiscountPercentage());
        }

        @Test
        void isEmptyInitially() {
            assertTrue(cart.isEmpty());
        }

        @Test
        void notEmptyAfterAddingItem() {
            cart.addItem(apple, 1);
            assertFalse(cart.isEmpty());
        }
    }
}
