package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents a menu given at an Oaxaca restaurant.
 */

@Entity
public class Menu {

  @Id
  @GeneratedValue
  private Integer id;

  private String name;

  @OneToMany
  private List<MenuItem> items;

  /**
   * Constructor to initialise menu items list.
   */
  public Menu() {
    this.items = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public List<MenuItem> getItems() {
    return items;
  }
}
