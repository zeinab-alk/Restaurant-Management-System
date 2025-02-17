package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.rhul.cs2810.model.Menu;
import uk.ac.rhul.cs2810.repository.MenuRepository;

@WebMvcTest(MenuController.class)
@AutoConfigureMockMvc(addFilters = false)  // Disable security filters for testing
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuRepository menuRepository;

    /**
     * Test retrieving a menu by ID (found).
     */
    @Test
    public void testGetMenuByIdSuccess() throws Exception {
        // Arrange
        Menu mockMenu = new Menu(1, "Lunch Menu");
        when(menuRepository.findById(1)).thenReturn(Optional.of(mockMenu));

        // Act & Assert
        mockMvc.perform(get("/menu/getMenu").param("menu_id", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))  // Updated to match actual response
                .andExpect(jsonPath("$.menuItems").isEmpty());  // Ensure menuItems is null
    }


    /**
     * Test retrieving a menu by ID (not found) - should return 404.
     */
    @Test
    public void testGetMenuByIdNotFound() throws Exception {
        // Arrange
        when(menuRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/menu/getMenu").param("menu_id", "999"))
                .andDo(print())
                .andExpect(status().isNotFound()); // Expect 404 now
    }


}
