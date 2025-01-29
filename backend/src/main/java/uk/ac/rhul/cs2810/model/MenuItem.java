package uk.ac.rhul.cs2810.model;

public class MenuItem {
  private int itemId; // Unique ID for the menu item
  private String name; // Name of the menu item
  private double price; // Price of the menu item
  private int caloricInfo; // Calorie count
  private String dietaryRestrictions; // Dietary restrictions (e.g., vegetarian, vegan)
  private String allergenInformation; // Allergen information
  private int popularity; // Popularity rating (e.g., number of orders)

  // Constructor
  public MenuItem(int itemId, String name, double price, int caloricInfo,
      String dietaryRestrictions, String allergenInformation, int popularity) {
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

  public String getDietaryRestrictions() {
    return dietaryRestrictions;
  }

  public void setDietaryRestrictions(String dietaryRestrictions) {
    this.dietaryRestrictions = dietaryRestrictions;
  }

  public String getAllergenInformation() {
    return allergenInformation;
  }

  public void setAllergenInformation(String allergenInformation) {
    this.allergenInformation = allergenInformation;
  }

  public int getPopularity() {
    return popularity;
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }

  @Override
  public String toString() {
    return "MenuItem{" + "itemId=" + itemId + ", name='" + name + '\'' + ", price=" + price
        + ", caloricInfo=" + caloricInfo + ", dietaryRestrictions='" + dietaryRestrictions + '\''
        + ", allergenInformation='" + allergenInformation + '\'' + ", popularity=" + popularity
        + '}';
  }
}
