package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;  // Make sure to import
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
        MenuItem menuItem = new MenuItem();
        menuItem.setItemId(1);
        menuItem.setName("Test Item");
        menuItem.setPrice(10.99);
        menuItem.setCaloricInfo(500);
        menuItem.setPopularity(4);
        menuItem.setAvailable(true);

        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(menuItem);

        mockMvc.perform(post("/api/menuItems")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(menuItem)))
                .andExpect(status().isOk());
    }

    @Test
    void directCoverageTest() {
      // Mock outside of Spring's context:
      MenuItemRepository repo = mock(MenuItemRepository.class);
      MenuItemController controller = new MenuItemController(repo);

      MenuItem item = new MenuItem();
      item.setItemId(99);
      item.setName("Direct Coverage Item");
      item.setPrice(5.99);
      item.setCaloricInfo(300);
      item.setPopularity(1);
      item.setAvailable(true);

      when(repo.save(any(MenuItem.class)))
          .thenReturn(item)
          .thenThrow(new RuntimeException("DB error"));

      // First call: success
      ResponseEntity<?> response1 = controller.addMenuItem(item);
      assert response1.getStatusCode().is2xxSuccessful();
      // Cast body to MenuItem
      MenuItem returnedItem = (MenuItem) response1.getBody();
      assert returnedItem != null;
      assert "Direct Coverage Item".equals(returnedItem.getName());

      // Second call: exception
      ResponseEntity<?> response2 = controller.addMenuItem(item);
      assert response2.getStatusCode().is5xxServerError();
    }
    


}
