package uk.ac.rhul.cs2810.model;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents a single item on the restaurant's menu. This class holds essential information such as
 * ID, name, price, calorie count, dietary restrictions, allergen details, and popularity.
 */

public class MenuItem {
  private int itemId;
  private String name;
  private double price;
  private int caloricInfo;
  private List<String> dietaryRestrictions; // Stores any dietary restrictions (e.g., vegetarian,
                                            // vegan, etc.)
  private List<String> allergenInformation; // Stores known allergens (e.g., nuts, gluten, etc.)
  private int popularity; // Tracks how popular the item is among customers

  /**
   * Default constructor that initialises dietaryRestrictions and allergenInformation as empty
   * lists.
   */
  public MenuItem() {
    this.dietaryRestrictions = new ArrayList<>();
    this.allergenInformation = new ArrayList<>();
  }

  // Getters and Setters

  /**
   * Gets the unique ID of this menu item.
   * 
   * @return the itemId
   */
  public int getItemId() {
    return itemId;
  }

  /**
   * Sets the unique ID for this menu item.
   * 
   * @param itemId the ID to assign
   */
  public void setItemId(int itemId) {
    this.itemId = itemId;
  }

  /**
   * Gets the name of this menu item.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this menu item.
   * 
   * @param name the name to assign
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the price of this menu item.
   * 
   * @return the price
   */
  public double getPrice() {
    return price;
  }

  /**
   * Sets the price of this menu item.
   * 
   * @param price the price to assign
   */
  public void setPrice(double price) {
    this.price = price;
  }

  /**
   * Gets the calorie count of this menu item.
   * 
   * @return the caloricInfo
   */
  public int getCaloricInfo() {
    return caloricInfo;
  }

  /**
   * Sets the calorie count of this menu item.
   * 
   * @param caloricInfo the calorie count to assign
   */
  public void setCaloricInfo(int caloricInfo) {
    this.caloricInfo = caloricInfo;
  }

  /**
   * Gets a list of dietary restrictions for this menu item.
   * 
   * @return a list of dietary restrictions
   */
  public List<String> getDietaryRestrictions() {
    return dietaryRestrictions;
  }

  /**
   * Sets the dietary restrictions for this menu item.
   * 
   * @param dietaryRestrictions the list of restrictions to assign
   */
  public void setDietaryRestrictions(List<String> dietaryRestrictions) {
    this.dietaryRestrictions = dietaryRestrictions;
  }

  /**
   * Gets a list of allergen information for this menu item.
   * 
   * @return a list of known allergens
   */
  public List<String> getAllergenInformation() {
    return allergenInformation;
  }

  /**
   * Sets the allergen information for this menu item.
   * 
   * @param allergenInformation the list of allergens to assign
   */
  public void setAllergenInformation(List<String> allergenInformation) {
    this.allergenInformation = allergenInformation;
  }

  /**
   * Gets the popularity rating of this menu item.
   * 
   * @return the popularity value
   */
  public int getPopularity() {
    return popularity;
  }

  /**
   * Sets the popularity rating of this menu item.
   * 
   * @param popularity the popularity value to assign
   */
  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }
}

