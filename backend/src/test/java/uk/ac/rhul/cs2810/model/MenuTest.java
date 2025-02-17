package uk.ac.rhul.cs2810.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Menu test class.
 */
public class MenuTest {

  private Menu menu;
  private MenuItem item1;
  private MenuItem item2;

  @BeforeEach
  void setUp() {
    // Initialise Menu and MenuItems before each test
    menu = new Menu();
    item1 = new MenuItem();
    item2 = new MenuItem();

    // Setting values for item1
    item1.setItemId(101);
    item1.setName("Taco Deluxe");
    item1.setPrice(9.99);
    item1.setCaloricInfo(750);

    // Setting values for item2
    item2.setItemId(102);
    item2.setName("Burger Special");
    item2.setPrice(12.99);
    item2.setCaloricInfo(950);
  }

  @Test
  void testAddMenuItem() {
    // Initially, the menu should be empty
    assertNull(menu.getItemByName("Taco Deluxe"));

    // Add item1 to the menu
    menu.addMenuItem(item1);

    // Test if item1 can be found in the menu
    assertNotNull(menu.getItemByName("Taco Deluxe"));
    assertEquals(item1, menu.getItemByName("Taco Deluxe"));
  }

  @Test
  void testGetItemByNameItemFound() {
    // Add both items to the menu
    menu.addMenuItem(item1);
    menu.addMenuItem(item2);

    // Test if the correct item is returned
    MenuItem foundItem = menu.getItemByName("Burger Special");
    assertNotNull(foundItem);
    assertEquals(item2, foundItem);
  }

  @Test
  void testGetItemByNameItemNotFound() {
    // Add item1 to the menu, but not item2
    menu.addMenuItem(item1);

    // Test if an item that is not in the menu returns null
    assertNull(menu.getItemByName("Pizza"));
  }
}
