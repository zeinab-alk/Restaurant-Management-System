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
  void menuTest() {
    //Test 1

    menu.setId(001);
    menu.setName("Main Menu");

    assertEquals(001, menu.getId());
    assertEquals("Main Menu", menu.getName());
  }

}
