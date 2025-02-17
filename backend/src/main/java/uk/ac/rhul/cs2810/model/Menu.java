package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple menu class that represents a restaurant menu.
 * This class is mapped to a database table using JPA annotations.
 */
@Entity  // Marks this class as a JPA entity to be managed by the persistence provider
public class Menu {

  @Id  // Marks this field as the primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY)  // Configures auto-generation of IDs
  private Integer id;
  private Integer menuId;

  /**
   * List of menu items associated with this menu.
   * Uses a one-to-many relationship as one menu can have multiple items.
   */
  @OneToMany  // Establishes one-to-many relationship with MenuItem entities
  private List<MenuItem> menuItems;

  private String name;

  /**
   * Default constructor that initializes an empty list of menu items.
   */
  public Menu() {
    this.menuItems = new ArrayList<>();
  }

  /**
   * Constructor for creating a menu with an ID and name.
   *
   * @param menuId The unique ID of the menu.
   * @param name The name of the menu.
   */
  public Menu(int menuId, String name) {
    this.id = menuId;
    this.name = name;
  }

  /**
   * Gets the menu's unique identifier.
   *
   * @return The menu's ID
   */
  public Integer getId() {
    return id;
  }

  /**
   * Sets the menu's unique identifier.
   *
   * @param id The ID to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Gets the list of menu items.
   *
   * @return List of menu items
   */
  public List<MenuItem> getMenuItems() {
    return menuItems;
  }

  /**
   * Sets the list of menu items.
   *
   * @param menuItems The list of menu items to set
   */
  public void setMenuItems(List<MenuItem> menuItems) {
    this.menuItems = menuItems;
  }

  /**
   * Finding the item by its name.
   *
   * @param name as the item name
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

  /**
   * Adds a new menu item to the menu.
   *
   * @param item The menu item to add
   */
  public void addMenuItem(MenuItem item) {
    menuItems.add(item);
  }
}
