package uk.ac.rhul.cs2810.contoller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.ac.rhul.cs2810.model.Menu;
import uk.ac.rhul.cs2810.repository.MenuRepository;

/**
 * Tests for the 'MenuController' class.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class MenuControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MenuRepository menuRepository;

  Menu menu;

  @BeforeEach
  void beforeEach() {
    menu = new Menu();
    menu.setName("Main Menu");
    menu = menuRepository.save(menu);
  }

  @Test
  void getMenuTest() throws JsonProcessingException, Exception {

    MvcResult action = mockMvc
        .perform(MockMvcRequestBuilders.get("/menu/getMenu")
            .param("menu_id", String.valueOf(menu.getId()))
            .accept(MediaType.APPLICATION_JSON))
        .andReturn();
    assertEquals(HttpStatus.OK.value(), action.getResponse().getStatus());

    Menu menu = objectMapper.readValue(action.getResponse().getContentAsString(), Menu.class);

    assertNotNull(menu);
    assertEquals(menu.getId(), menu.getId());
    assertEquals(menu.getName(), menu.getName());
  }

}
