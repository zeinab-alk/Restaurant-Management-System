package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single item on the restaurant's menu.
 * This class holds essential information such as ID, name, price, calorie count, dietary
 * restrictions, allergen details, and popularity.
 */
@Entity  // Marks this class as a JPA entity
public class MenuItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer itemId;

  private String name;
  private double price;
  private int caloricInfo;

  @ElementCollection  // For collections of simple types
  private List<String> dietaryRestrictions;

  @ElementCollection  // For collections of simple types
  private List<String> allergenInformation;

  private int popularity;
  private boolean availability;

  @Column(name = "image_url", nullable = true) // Allows NULL values and ensures correct mapping
  private String imageUrl;

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

  /**
   * Constructor for creating a MenuItem with basic details.
   *
   * @param itemId The unique ID of the menu item.
   * @param name The name of the menu item.
   * @param price The price of the menu item.
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

  /**
   * This is a validated setURL to make sure if setURL is utilised, only valid URLS can be used.
   *
   * @param imageUrl is the URL we will be using.
   */
  public void setImageUrl(String imageUrl) {
    if (isValidUrl(imageUrl)) {
      this.imageUrl = imageUrl;
    } else {
      throw new IllegalArgumentException("Invalid image URL");
    }
  }

  private boolean isValidUrl(String url) {
    try {
      URI uri = new URI(url); // Create URI instance
      uri.toURL(); // Convert URI to URL (no longer using the deprecated URL constructor)
      return true;
    } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
      return false;
    }
  }
}


