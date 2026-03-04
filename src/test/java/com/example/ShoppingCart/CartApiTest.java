package com.example.ShoppingCart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@DisplayName("Cart API Tests")
class CartApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private String cartId;
    private String itemName;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        itemName = "CartItem-" + System.nanoTime();
        String itemJson = """
                {"name":"%s","quantity":100,"price":5}
                """.formatted(itemName).trim();
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(itemJson));

        String cartJson = """
                {"discountPercentage":10.0}
                """.trim();
        MvcResult cartResult = mockMvc.perform(post("/api/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cartJson))
                .andExpect(status().isCreated())
                .andReturn();
        cartId = com.jayway.jsonpath.JsonPath.read(
                cartResult.getResponse().getContentAsString(), "$.cartId");
    }

    @Test
    // POST /api/carts creates cart
    void createCart_returnsCreated() throws Exception {
        String json = """
                {"discountPercentage":5.0}
                """.trim();

        mockMvc.perform(post("/api/carts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartId").exists())
                .andExpect(jsonPath("$.discountPercentage").value(5.0))
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    // GET /api/carts/{id} cart exists 
    void getCart_whenExists_returnsCart() throws Exception {
        mockMvc.perform(get("/api/carts/" + cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(cartId));
    }

    @Test
    // GET /api/carts/{id} cart doesnt exist
    void getCart_whenNotExists_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/carts/non-existent-cart-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    // POST /api/carts/{id}/items adds item
    void addItemToCart_returnsCreated() throws Exception {
        String json = """
                {"itemName":"%s","quantity":3}
                """.formatted(itemName).trim();

        mockMvc.perform(post("/api/carts/" + cartId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.subtotal").value(15));
    }

    @Test
    // POST /api/carts/{id}/items item does not exist
    void addItemToCart_itemNotExists_returnsNotFound() throws Exception {
        String json = """
                {"itemName":"NonExistentItem","quantity":1}
                """.trim();

        mockMvc.perform(post("/api/carts/" + cartId + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    // PUT /api/carts/{id}/items/{name} updates quantity
    void updateCartItem_returnsOk() throws Exception {
        String addJson = """
                {"itemName":"%s","quantity":5}
                """.formatted(itemName).trim();
        mockMvc.perform(post("/api/carts/" + cartId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson));

        String updateJson = """
                {"quantity":2}
                """.trim();
        mockMvc.perform(put("/api/carts/" + cartId + "/items/" + itemName)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(10));
    }

    @Test
    // DELETE /api/carts/{id}/items/{name} removes item
    void removeItemFromCart_returnsOk() throws Exception {
        String addJson = """
                {"itemName":"%s","quantity":5}
                """.formatted(itemName).trim();
        mockMvc.perform(post("/api/carts/" + cartId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson));

        mockMvc.perform(delete("/api/carts/" + cartId + "/items/" + itemName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }

    @Test
    // DELETE /api/carts/{id} clears cart and returns 200
    void clearCart_returnsOk() throws Exception {
        String addJson = """
                {"itemName":"%s","quantity":5}
                """.formatted(itemName).trim();
        mockMvc.perform(post("/api/carts/" + cartId + "/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addJson));

        mockMvc.perform(delete("/api/carts/" + cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isEmpty());
    }
}
