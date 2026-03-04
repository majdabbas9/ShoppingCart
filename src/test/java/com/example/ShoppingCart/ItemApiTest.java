package com.example.ShoppingCart;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@DisplayName("Item API test")
@SpringBootTest
class ItemApiTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * Test the createItem endpoint
     */
    @Test
    // POST /api/items new item
    void createItem_returnsCreated() throws Exception {
        String uniqueName = "Item-" + System.nanoTime();
        String json = """
                {"name":"%s","quantity":50,"price":10}
                """.formatted(uniqueName).trim();

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(uniqueName))
                .andExpect(jsonPath("$.quantity").value(50))
                .andExpect(jsonPath("$.price").value(10));
    }

    /**
     * Test the createItem endpoint when the item name already exists
     */
    @Test
    // POST /api/items existing item
    void createItem_duplicateName_returnsConflict() throws Exception {
        String uniqueName = "Item-Dup-" + System.nanoTime();
        String json = """
                {"name":"%s","quantity":10,"price":5}
                """.formatted(uniqueName).trim();

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // testing the code below
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString("already exists")));
    }

    /**
     * Test the listItems endpoint
     */
    @Test
    // GET /api/items list items
    void listItems_returnsAllItems() throws Exception {
        String uniqueName = "Item-List-" + System.nanoTime();
        String json = """
                {"name":"%s","quantity":25,"price":3}
                """.formatted(uniqueName).trim();
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // testing the code below 
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(uniqueName)));
    }

    /**
     * Test the getItemByName endpoint when the item exists
     */
    @Test
    // GET /api/item finding item
    void getItemByName_whenExists_returnsItem() throws Exception {
        String uniqueName = "Item-Get-" + System.nanoTime();
        String json = """
                {"name":"%s","quantity":100,"price":7}
                """.formatted(uniqueName).trim();
        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // testing the code below
        mockMvc.perform(get("/api/items/" + uniqueName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(uniqueName))
                .andExpect(jsonPath("$.quantity").value(100));
    }

    /**
     * Test the getItemByName endpoint when the item does not exist
     */
    @Test
    // GET /api/items Non Existent Item
    void getItemByName_whenNotExists_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/items/NonExistentItem" + System.nanoTime()))
                .andExpect(status().isNotFound());
    }

    /**
     * Test the createItem endpoint when the item price is invalid
     */
    @Test
    // POST /api/items invaild item
    void createItem_invalidPrice_returnsError() throws Exception {
        String uniqueName = "Item-Invalid-" + System.nanoTime();
        String json = """
                {"name":"%s","quantity":10,"price":0}
                """.formatted(uniqueName).trim();

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").exists());
    }
}
