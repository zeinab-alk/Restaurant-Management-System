package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;

@WebMvcTest(MenuItemController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MenuItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MenuItemRepository menuItemRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Test `getAllMenuItems()`.
   */
  @Test
  public void testGetAllMenuItems() throws Exception {
    when(menuItemRepository.findAll())
        .thenReturn(Arrays.asList(new MenuItem(1, "Burger", 5.99), new MenuItem(2, "Pizza", 8.99)));

    mockMvc.perform(get("/api/menuItems")).andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  /**
   * Test `addMenuItem()`.
   */
  @Test
  public void testAddMenuItemSuccess() throws Exception {
    MenuItem item = new MenuItem(1, "Burger", 5.99);
    when(menuItemRepository.save(any(MenuItem.class))).thenReturn(item);

    mockMvc
        .perform(post("/api/menuItems").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(item)))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Burger"));
  }

  @Test
  public void testAddMenuItemFailure() throws Exception {
    when(menuItemRepository.save(any(MenuItem.class)))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc
        .perform(post("/api/menuItems").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new MenuItem(1, "Burger", 5.99))))
        .andDo(print()).andExpect(status().isInternalServerError())
        .andExpect(content().string("Error saving menu item: Database error"));
  }

  /**
   * Test `updateMenuItem()`.
   */
  @Test
  public void testUpdateMenuItemSuccess() throws Exception {
    MenuItem existingItem = new MenuItem(1, "Burger", 5.99);
    MenuItem updatedItem = new MenuItem(1, "Cheese Burger", 6.99);

    when(menuItemRepository.findById(1)).thenReturn(Optional.of(existingItem));
    when(menuItemRepository.save(any(MenuItem.class))).thenReturn(updatedItem);

    mockMvc
        .perform(put("/api/menuItems/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedItem)))
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Cheese Burger"));
  }

  @Test
  public void testUpdateMenuItemNotFound() throws Exception {
    when(menuItemRepository.findById(99)).thenReturn(Optional.empty());

    MenuItem updatedItem = new MenuItem(99, "Pasta", 10.99);

    mockMvc
        .perform(put("/api/menuItems/99").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedItem)))
        .andDo(print()).andExpect(status().isNotFound());
  }

  /**
   * Test `getMenuItem()`.
   */
  @Test
  public void testGetMenuItemFound() throws Exception {
    MenuItem item = new MenuItem(1, "Burger", 5.99);
    when(menuItemRepository.findById(1)).thenReturn(Optional.of(item));

    mockMvc.perform(get("/api/menuItems/1")).andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Burger"));
  }

  @Test
  public void testGetMenuItemNotFound() throws Exception {
    when(menuItemRepository.findById(999)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/menuItems/999")).andDo(print()).andExpect(status().isNotFound())
        .andExpect(content().string("Menu item not found"));
  }

  @Test
  public void testGetMenuItemThrowsException() throws Exception {
    when(menuItemRepository.findById(anyInt())).thenThrow(new RuntimeException("Database failure"));

    mockMvc.perform(get("/api/menuItems/1")).andDo(print())
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error retrieving menu item: Database failure"));
  }
}

