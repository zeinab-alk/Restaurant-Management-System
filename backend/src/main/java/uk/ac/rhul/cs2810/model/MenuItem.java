package uk.ac.rhul.cs2810.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single item on the restaurant's menu.
 * This class holds essential information such as ID, name, price, calorie count, dietary
 * restrictions, allergen details, and popularity.
 */
public class MenuItem {

  /**
   * The unique ID of this menu item.
   */
  private int itemId;

  /**
   * The name of this menu item.
   */
  private String name;

  /**
   * The price of this menu item.
   */
  private double price;

  /**
   * The number of calories in this menu item.
   */
  private int caloricInfo;

  /**
   * List of dietary restrictions (e.g., vegetarian, vegan).
   */
  private List<String> dietaryRestrictions;

  /**
   * List of allergens (e.g., nuts, gluten).
   */
  private List<String> allergenInformation;

  /**
   * Tracks how popular the item is among customers.
   */
  private int popularity;

  // Added by Ben for Waiter class specifically.
  private boolean availability;

  // getter and setter for availability
  public boolean isAvailable() {
    return availability;
  }

  public void setAvailable(boolean availability) {
    this.availability = availability;
  }


  /**
   * Default constructor that initializes dietaryRestrictions and allergenInformation as empty
   * lists.
   */
  public MenuItem() {
    this.dietaryRestrictions = new ArrayList<>();
    this.allergenInformation = new ArrayList<>();
  }

  // -- getters and setters below --

  public int getItemId() {
    return itemId;
  }

  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getCaloricInfo() {
    return caloricInfo;
  }

  public void setCaloricInfo(int caloricInfo) {
    this.caloricInfo = caloricInfo;
  }

  public List<String> getDietaryRestrictions() {
    return dietaryRestrictions;
  }

  public void setDietaryRestrictions(List<String> dietaryRestrictions) {
    this.dietaryRestrictions = dietaryRestrictions;
  }

  public List<String> getAllergenInformation() {
    return allergenInformation;
  }

  public void setAllergenInformation(List<String> allergenInformation) {
    this.allergenInformation = allergenInformation;
  }

  public int getPopularity() {
    return popularity;
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }
}

