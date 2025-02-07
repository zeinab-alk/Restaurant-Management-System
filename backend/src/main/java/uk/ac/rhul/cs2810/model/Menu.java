package uk.ac.rhul.cs2810.model;

import java.util.ArrayList;
import java.util.List;

public class Menu {
  private List<MenuItem> menuItems;

  public Menu() {
    this.menuItems = new ArrayList<>();
  }

  // Add method to get a menu item by name
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
