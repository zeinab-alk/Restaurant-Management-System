package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Waiter entity class.
 */
@Entity
@Table(name = "waiters")
public class Waiter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private String name;
  private String status;

  @ElementCollection
  private List<Integer> assignedTables;

  @ElementCollection
  private List<String> currentOrders;

  @ManyToOne
  @JoinColumn(name = "menu_id") // Foreign key reference to the Menu table
  private Menu menu;

  @Column(name = "is_busy")
  private boolean busy = false;

  /**
   * Default constructor required by JPA.
   */
  public Waiter() {}

  /**
   * Waiter constructor.
   *
   * @param id as id.
   * @param name as name.
   * @param status as status.
   * @param menu as the menu.
   */
  public Waiter(int id, String name, String status, Menu menu) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.menu = menu;
    this.busy = false;
  }

  /**
   * Changes menu availability.
   *
   * @param itemName as item name.
   * @param availability as availability status.
   */
  public void changeMenu(String itemName, boolean availability) {
    if (menu != null) {
      MenuItem item = menu.getItemByName(itemName);
      if (item != null) {
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
   * Marks order as delivered if not already.
   *
   * @param order The order to update.
   */
  public void markOrderAsDelivered(Order order) {
    if (order == null) {
      System.out.println("Order is null.");
      return;
    }
    OrderStatus orderStatus = order.getStatus();
    if (orderStatus == null || orderStatus == OrderStatus.DELIVERED) {
      System.out.println("Order is already delivered or invalid.");
    } else {
      order.setStatus(OrderStatus.DELIVERED);
      System.out.println("Order " + order.getOrderNumber() + " has been marked as Delivered.");
    }
  }


  /**
   * Notifies the waiter when a new order is placed with item details.
   *
   * @param order The order that has been placed.
   */
  public void notifyNewOrder(Order order) {
    if (order != null) {
      if (this.currentOrders == null) {
        this.currentOrders = new ArrayList<>();
      }

      // Add the order number
      String orderId = String.valueOf(order.getOrderNumber());
      if (!this.currentOrders.contains(orderId)) {
        this.currentOrders.add(orderId);
      }

      // Print exactly what the test expects
      String notificationMessage =
          "Waiter " + this.name + " has been notified of new order: " + orderId;
      System.out.println(notificationMessage);


      // Extracting ordered items
      List<MenuItem> orderedItems = order.getMenuItems(); // Ensure Order has getMenuItems()
      StringBuilder orderDetails = new StringBuilder("Order " + order.getOrderNumber() + ": ");

      for (MenuItem item : orderedItems) {
        orderDetails.append(item.getName()).append(", ");
      }

      // Remove trailing comma
      if (!orderedItems.isEmpty()) {
        orderDetails.setLength(orderDetails.length() - 2);
      }

      // Add the formatted order details to currentOrders
      this.currentOrders.add(orderDetails.toString());

      System.out
          .println("Waiter " + this.name + " has been notified of new order: " + orderDetails);
    }
  }

  /**
   * Confirms a customer order by checking if all items are available.
   *
   * @param order The order to confirm.
   * @return true if all items are available, false otherwise.
   */
  public boolean confirmOrder(Order order) {
    if (order == null || menu == null) {
      System.out.println("Order or menu is not available.");
      return false;
    }

    List<MenuItem> orderedItems = order.getMenuItems();

    if (orderedItems == null || orderedItems.isEmpty()) {
      System.out.println("Order or menu is not available.");
      return false;
    }

    for (MenuItem item : orderedItems) {
      MenuItem menuItem = menu.getItemByName(item.getName());
      if (menuItem == null || !menuItem.isAvailable()) {
        System.out.println("Item unavailable: " + item.getName());
        return false;
      }
    }

    System.out.println("Order " + order.getOrderNumber() + " confirmed.");
    return true;
  }

  // Overloaded confirmOrder without parameters
  public void confirmOrder() {}

  /**
   * Checks if the waiter is busy.
   *
   * @return true if busy, false otherwise.
   */
  public boolean isBusy() {
    return busy;
  }

  /**
   * Sets the busy status of the waiter.
   *
   * @param busy as busy status.
   */
  public void setBusy(boolean busy) {
    this.busy = busy;
  }

  public void trackOrder() {}

  public void updateOrderStatus() {}

  public void cancelOrder() {}

  public void notifyTeam() {}

  // Getters and Setters

  public int getId() {
    return id;
  }

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

  public Menu getMenu() {
    return menu;
  }

  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  public List<Integer> getAssignedTables() {
    return assignedTables;
  }

  public void setAssignedTables(List<Integer> assignedTables) {
    this.assignedTables = assignedTables;
  }

  public List<String> getCurrentOrders() {
    return currentOrders;
  }

  public void setCurrentOrders(List<String> currentOrders) {
    this.currentOrders = currentOrders;
  }
}
