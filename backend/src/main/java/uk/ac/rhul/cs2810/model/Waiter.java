package uk.ac.rhul.cs2810.model;

import java.util.List;

public class Waiter {
  // Attributes - Currently not all are going to be in use
    private int id;
    private String name;
    @SuppressWarnings("unused") // Currently suppressed as they are not used
    private List<Integer> assignedTables; // Assuming a list of table IDs
    private String status;
    @SuppressWarnings("unused") // Currently suppressed as they are not used
    private List<String> currentOrders;  // Assuming orders are represented as Strings
    
    // We will need menu class to be completed before this runs
    // private Menu menu;
    
    // Constructor - This is also currently not in use until Menu is created separately
    /*
     * public Waiter(int id, String name, String status, Menu menu) { this.id = id; this.name =
     * name; this.status = status; this.menu = menu; }
     */

    // Methods - Below is an example we can use once the menu class is implemented correctly
    public void changeMenu(String itemName, boolean availability) {
      // if (menu != null) {
      // menu.updateItemAvailability(itemName, availability);
      // System.out.println("Menu updated: " + itemName + " is now " + (availability ? "available" :
      // "unavailable"));
      // } else {
      // System.out.println("No menu is associated with this waiter.");
      // }
      // }
      // Apologies for the formatting, have fun fixing it!
    }

    public void confirmOrder() {
        // Method implementation goes here
    }

    public void trackOrder() {
        // Method implementation goes here
    }

    public void updateOrderStatus() {
        // Method implementation goes here
    }

    public void cancelOrder() {
        // Method implementation goes here
    }

    public void notifyTeam() {
        // Method implementation goes here
    }

    public int getId() {
      return id;
    }

    // Temporary getters and setters created, they may be used later.
    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getStatus() {
      return status;
    }

    public void setStatus(String status) {
      this.status = status;
    }
}
