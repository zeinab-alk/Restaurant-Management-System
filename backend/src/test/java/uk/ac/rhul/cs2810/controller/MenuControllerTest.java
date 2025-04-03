package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.rhul.cs2810.model.Menu;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import uk.ac.rhul.cs2810.repository.MenuRepository;

/**
 * This runs tests on both MenuController and MenuItemController.
 */
@WebMvcTest({MenuController.class, MenuItemController.class})
@AutoConfigureMockMvc(addFilters = false)  // Disable security filters for testing
public class MenuControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private MenuRepository menuRepository;

  @MockBean
  private MenuItemRepository menuItemRepository;

  /**
   * Test retrieving a menu by ID (found).
   */
  @Test
  public void testGetMenuByIdSuccess() throws Exception {
    // Arrange
    Menu mockMenu = new Menu(1, "Lunch Menu");
    when(menuRepository.findById(1)).thenReturn(Optional.of(mockMenu));

    // Act & Assert
    mockMvc.perform(get("/api/menu/getMenu").param("menu_id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))  // Updated to match actual response
                .andExpect(jsonPath("$.menuItems").isEmpty());  // Ensure menuItems is null
  }


  /**
   * Test retrieving a menu item that does not exist.
   */
  @Test
  public void testGetMenuItemNotFound() throws Exception {
    when(menuItemRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc.perform(get("/api/menu/getMenu").param("menu_id", "999"))
                .andDo(print())
                .andExpect(status().isNotFound()); // Expect 404 now
  }

  /**
   * Test adding a menu item successfully.
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

  /**
   * Test updating a menu item successfully.
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

  /**
   * Test updating a menu item that does not exist.
   */
  @Test
  public void testUpdateMenuItemNotFound() throws Exception {
    when(menuItemRepository.findById(99)).thenReturn(Optional.empty());

    MenuItem updatedItem = new MenuItem(99, "Pasta", 10.99);

    mockMvc.perform(put("/api/menuItems/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedItem)))
            .andDo(print())
            .andExpect(status().isNotFound());
  }

  /**
   * Test retrieving a menu item successfully.
   */
  @Test
  public void testGetMenuItemFound() throws Exception {
    MenuItem item = new MenuItem(1, "Burger", 5.99);
    when(menuItemRepository.findById(1)).thenReturn(Optional.of(item));

    mockMvc.perform(get("/api/menuItems/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Burger"));
  }

}
