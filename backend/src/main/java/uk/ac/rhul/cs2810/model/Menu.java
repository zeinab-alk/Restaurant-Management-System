package uk.ac.rhul.cs2810.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents a menu given at an Oaxaca restaurant.
 */
public class Menu {

  private int id;

  private String name;

  private List<MenuItem> items;

  public Menu() {
    this.items = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId(int i) {
    this.id = i;
  }

  public List<MenuItem> getItems() {
    return items;
  }
}
