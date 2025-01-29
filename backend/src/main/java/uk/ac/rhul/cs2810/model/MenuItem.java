package uk.ac.rhul.cs2810.model;

import java.util.List;

public class MenuItem {
  private int itemId;
  private String name;
  private double price;
  private int caloricInfo;
  private List<String> dietaryRestrictions; // Changed to List
  private List<String> allergenInformation; // Changed to List
  private int popularity;

  // Constructor
  public MenuItem(int itemId, String name, double price, int caloricInfo,
      List<String> dietaryRestrictions, List<String> allergenInformation, int popularity) {
    this.itemId = itemId;
    this.name = name;
    this.price = price;
    this.caloricInfo = caloricInfo;
    this.dietaryRestrictions = dietaryRestrictions;
    this.allergenInformation = allergenInformation;
    this.popularity = popularity;
  }

  // Getters and Setters
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
