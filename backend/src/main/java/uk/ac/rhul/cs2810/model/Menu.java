package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a restaurant menu.
 */
@Entity
public class Menu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "menu_id")
  private Integer menuId;

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

  public Integer getMenuId() {
    return menuId;
  }

  public void setMenuId(Integer menuId) {
    this.menuId = menuId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<MenuItem> getMenuItems() {
    return menuItems;
  }

  public void setMenuItems(List<MenuItem> menuItems) {
    this.menuItems = menuItems;
  }

  /**
   * Retrieves a menu item by its name.
   *
   * @param itemName the name of the menu item.
   * @return the MenuItem if found, otherwise null.
   */
  public MenuItem getItemByName(String itemName) {
    for (MenuItem item : menuItems) {
      if (item.getName().equalsIgnoreCase(itemName)) {
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
    this.menuItems.add(item);
  }
}

