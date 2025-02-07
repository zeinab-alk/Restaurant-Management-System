package uk.ac.rhul.cs2810.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple menu class.
 */
public class Menu {
  private List<MenuItem> menuItems;

  public Menu() {
    this.menuItems = new ArrayList<>();
  }

  // Add method to get a menu item by name
  /**
   * Finding the item by its name.
   *
   * @param name as the itemname
   * @return the menu item
   */
  public MenuItem getItemByName(String name) {
    for (MenuItem item : menuItems) {
      if (item.getName().equalsIgnoreCase(name)) {
        return item;
      }
    }
    return null;
  }

  // Optionally, a method to add menu items
  public void addMenuItem(MenuItem item) {
    menuItems.add(item);
  }
}