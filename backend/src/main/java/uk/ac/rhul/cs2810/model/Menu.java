package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Column;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a restaurant menu.
 */
@Entity
public class Menu {

  @Id  // Marks this field as the primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY)  // Configures auto-generation of IDs
  @Column(name = "menu_id")
  private Integer id;

  private String name;

  @OneToMany
  private List<MenuItem> menuItems;

  /**
   * Default constructor that initialises the menu items list.
   */
  public Menu() {
    this.menuItems = new ArrayList<>();
  }

  /**
   * Constructs a menu with a specified ID and name.
   *
   * @param menuId the menu ID.
   * @param name the menu name.
   */
  public Menu(int menuId, String name) {
    this.menuId = menuId;
    this.name = name;
    this.menuItems = new ArrayList<>();
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
   * Gets the menu name.
   *
   * @return The menu name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the menu name.
   *
   * @param name The name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the list of menu items.
   *
   * @return List of menu items
   */
  public List<MenuItem> getMenuItems() {
    return menuItems;
  }

  public void setMenuItems(List<MenuItem> menuItems) {
    this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
  }

  /**
   * Retrieves a menu item by its name.
   *
   * @param itemName the name of the menu item.
   * @return the MenuItem if found, otherwise null.
   */
  public MenuItem getItemByName(String name) {
    if (name == null || menuItems == null) {
      return null;
    }
    
    for (MenuItem item : menuItems) {
      if (item != null && name.equalsIgnoreCase(item.getName())) {
        return item;
      }
    }
    return null;
  }

  /**
   * Adds a new item to the menu.
   *
   * @param item the menu item to add.
   */
  public void addMenuItem(MenuItem item) {
    if (item != null) {
      if (menuItems == null) {
        menuItems = new ArrayList<>();
      }
      menuItems.add(item);
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Menu menu = (Menu) obj;
    return Objects.equals(id, menu.id);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}

