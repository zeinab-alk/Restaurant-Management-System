package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the 'Menu' class.
 */
public class MenuTest {

  Menu menu;

  @BeforeEach
  void beforeEach() {
    menu = new Menu();
  }

  @Test
  void customerTest() {
    //Test 1

    menu.setName("Main Menu);

    assertEquals("Main Menu", menu.getName());
  }

}
