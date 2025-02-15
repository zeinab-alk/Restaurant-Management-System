package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Added
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;

@WebMvcTest(MenuItemController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
public class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuItemRepository menuItemRepository;

    @Test
    void testAddMenuItem() throws Exception {
      // Create a MenuItem with a non-null itemId
        MenuItem menuItem = new MenuItem();
      menuItem.setItemId(1); // Fixes the "itemId is null" error
        menuItem.setName("Test Item");
        menuItem.setPrice(10.99);
        menuItem.setCaloricInfo(500);
        menuItem.setPopularity(4);
        menuItem.setAvailable(true);

        // Mock repository behavior
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        // Perform the POST request, expect 200 OK
        mockMvc.perform(post("/api/menuItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(menuItem)))
                .andExpect(status().isOk());
    }
  }
