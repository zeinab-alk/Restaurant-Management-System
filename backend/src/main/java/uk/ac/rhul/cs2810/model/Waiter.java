package uk.ac.rhul.cs2810.model;

import java.util.List;

/**
 * Waiter class.
 */
public class Waiter {
  // Attributes - Currently not all are going to be in use
  private int id;
  private String name;
  @SuppressWarnings("unused") // Currently suppressed as they are not used
  private List<Integer> assignedTables; // Assuming a list of table IDs
  private String status;
  @SuppressWarnings("unused") // Currently suppressed as they are not used
  private List<String> currentOrders; // Assuming orders are represented as Strings

  private Menu menu;
  Order order;

  /**
   * Waiter constructor.
   *
   * @param id as id.
   * @param name as name
   * @param status as status
   * @param menu as the menu
   */
  public Waiter(int id, String name, String status, Menu menu) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.menu = menu;
  }


  /**
   * Change menu as in Sprint1.
   *
   * @param itemName as itemname
   * @param availability as availability
   */
  public void changeMenu(String itemName, boolean availability) {
    // Check if the waiter has a menu associated
    if (menu != null) {
      // Find the menu item by name
      MenuItem item = menu.getItemByName(itemName);

      if (item != null) {
        // Update the availability of the menu item
        item.setAvailable(availability);
        System.out.println("Menu updated: " + itemName + " is now "
            + (availability ? "available" : "unavailable"));
      } else {
        System.out.println("Item " + itemName + " not found in the menu.");
      }
    } else {
      System.out.println("No menu is associated with this waiter.");
    }
  }

  /**
   * Updates the status of the given order to "Delivered" if it hasn't been delivered yet.
   *
   * @param order The order to update.
   */
  public void markOrderAsDelivered(Order order) {
    if (order == null) {
      System.out.println("Order is null."); // Matches expected test output
      return;
    }

    String orderStatus = order.getStatus();

    if (orderStatus == null) { // Separate check for null status
      System.out.println("Order status is null.");
      return;
    }

    if (orderStatus.trim().isEmpty() || "Delivered".equalsIgnoreCase(orderStatus)) {
      System.out.println("Order is already delivered or invalid.");
    } else {
      order.setStatus("Delivered");
      System.out.println("Order " + order.getOrderNumber() + " has been marked as Delivered.");
    }
  }

  /**
   * Allows waiter to confirm order placed by customer.
   */
  public boolean confirmOrder() {
    if (order == null) {
      return false;
    }

    order.setStatus("Confirmed");
    return true;
  }

  /**
   * Allows waiter to track order placed by customer.
   */
  public void trackOrder() {
    // Method implementation goes here
  }

  /**
   * Allows waiter to update order status.
   */
  public void updateOrderStatus() {
    // Method implementation goes here
  }

  /**
   * Allows waiter to cancel order placed by customer.
   */
  public void cancelOrder() {
    // Method implementation goes here
  }

  /**
   * Allows waiter to notify team about order placed by customer.
   */
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
