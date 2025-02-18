package uk.ac.rhul.cs2810.model;

import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
  private List<Integer> assignedTables; // Assuming a list of table IDs

  @ElementCollection
  private List<String> currentOrders; // Assuming orders are represented as Strings

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
    this.busy = false; // Waiters start as not busy
  }

  /**
   * Change menu as in Sprint1.
   *
   * @param itemName as itemname
   * @param availability as availability.
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
   * Updates the status of the given order to "Delivered" if it hasn't been delivered yet.
   *
   * @param order The order to update.
   */
  public void markOrderAsDelivered(Order order) {
    if (order == null) {
      System.out.println("Order is null.");
      return;
    }
    String orderStatus = order.getStatus();
    if (orderStatus == null || orderStatus.trim().isEmpty()
        || "Delivered".equalsIgnoreCase(orderStatus)) {
      System.out.println("Order is already delivered or invalid.");
    } else {
      order.setStatus("Delivered");
      System.out.println("Order " + order.getOrderNumber() + " has been marked as Delivered.");
    }
  }
  public boolean isBusy() {
    return busy;
  }

  public void setBusy(boolean busy) {
    this.busy = busy;
  }

  public void confirmOrder() {}

  public void trackOrder() {}

  public void updateOrderStatus() {}

  public void cancelOrder() {}

  public void notifyTeam() {}

  // Getters and Setters
  public int getId() { return id; }
  public void setId(int id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public Menu getMenu() { return menu; }
  public void setMenu(Menu menu) { this.menu = menu; }

  public List<Integer> getAssignedTables() { return assignedTables; }
  public void setAssignedTables(List<Integer> assignedTables) { this.assignedTables = assignedTables; }

  public List<String> getCurrentOrders() { return currentOrders; }
  public void setCurrentOrders(List<String> currentOrders) { this.currentOrders = currentOrders; }
}

