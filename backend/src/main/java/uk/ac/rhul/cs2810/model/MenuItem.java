package uk.ac.rhul.cs2810.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item on the restaurant menu.
 */
@Entity
public class MenuItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer itemId;

  private String name;
  private double price;
  private int caloricInfo;

  @ElementCollection
  private List<String> dietaryRestrictions;

  @ElementCollection
  private List<String> allergenInformation;

  private int popularity;
  private boolean availability;
  
  private String imageUrl;

  /**
   * Checks if the item is available.
   *
   * @return true if available, false otherwise.
   */
  public boolean isAvailable() {
    return availability;
  }

  /**
   * Sets the availability of the menu item.
   *
   * @param availability the new availability status.
   */
  public void setAvailable(boolean availability) {
    this.availability = availability;
  }

  /**
   * Default constructor initializing empty lists.
   */
  public MenuItem() {
    this.dietaryRestrictions = new ArrayList<>();
    this.allergenInformation = new ArrayList<>();
  }

  /**
   * Constructs a MenuItem with basic details.
   *
   * @param itemId the item ID.
   * @param name the item name.
   * @param price the item price.
   */
  public MenuItem(int itemId, String name, double price) {
    this();
    this.itemId = itemId;
    this.name = name;
    this.price = price;
  }

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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}


