package uk.ac.rhul.cs2810.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Waiter entity class.
 */
@Entity
@Table(name = "waiters")
public class Waiter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "waiter_id")
  private int id;

  @Column(name = "waiter_name")
  private String name;

  @Column(name = "status")
  private String status;

  @ElementCollection
  private List<Integer> assignedTablesLegacy; // Renamed to keep old data

  @ElementCollection
  private List<String> currentOrders;

  @ManyToOne
  @JoinColumn(name = "menu_id")
  private Menu menu;

  @Column(name = "is_busy")
  private boolean busy = false;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "waiter_id")
  private List<TableAssignment> assignedTables = new ArrayList<>();

  public void assignTable(int tableNumber) {
    assignedTables.add(new TableAssignment(tableNumber));
  }

  /**
   * Default constructor required by JPA.
   */
  public Waiter() {}

  /**
   * Waiter constructor.
   *
   * @param id the waiter ID.
   * @param name the waiter name.
   * @param status the status of the waiter.
   * @param menu the assigned menu.
   */
  public Waiter(int id, String name, String status, Menu menu) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.menu = menu;
    this.busy = false;
  }


  /**
   * Overloaded method for confirming order without parameters (stub method).
   */
  public void confirmOrder() {
    System.out.println("Confirming order...");
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

  /**
   * Changes menu availability.
   *
   * @param itemName as item name.
   * @param availability as availability status.
   */
  public void changeMenu(String itemName, boolean availability) {
    if (itemName == null) {
      System.out.println("Item name cannot be null.");
      return;
    }
    
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

      String orderId = String.valueOf(order.getOrderNumber());
      if (!this.currentOrders.contains(orderId)) {
        this.currentOrders.add(orderId);
      }

      String notificationMessage =
          "Waiter " + this.name + " has been notified of new order: " + orderId;
      System.out.println(notificationMessage);

      List<MenuItem> orderedItems = order.getMenuItems();
      StringBuilder orderDetails = new StringBuilder("Order " + order.getOrderNumber() + ": ");

      for (MenuItem item : orderedItems) {
        orderDetails.append(item.getName()).append(", ");
      }

      if (!orderedItems.isEmpty()) {
        orderDetails.setLength(orderDetails.length() - 2);
      }

      this.currentOrders.add(orderDetails.toString());

      System.out
          .println("Waiter " + this.name + " has been notified of new order: " + orderDetails);
    }
  }

  public List<Integer> getUnpaidTables(List<Order> allOrders) {
    List<Integer> unpaidTables = new ArrayList<>();

    if (assignedTables == null || assignedTables.isEmpty()) {
      System.out.println("No tables assigned to waiter: " + this.name);
      return unpaidTables;
    }

    for (Order order : allOrders) {
      Customer customer = order.getCustomer();
      if (customer != null) {
        int tableNumber = (int) customer.getId();
        if (getAssignedTableNumbers().contains(tableNumber)
            && order.getStatus() == OrderStatus.DELIVERED) {
          unpaidTables.add(tableNumber);
        }
      }
    }

    if (unpaidTables.isEmpty()) {
      System.out.println("All delivered orders have been paid.");
    } else {
      System.out
          .println("Waiter " + this.name + " needs to check payment for tables: " + unpaidTables);
    }

    return unpaidTables;
  }

  public void trackOrder() {
    System.out.println("Tracking order...");
  }

  public void updateOrderStatus() {
    System.out.println("Updating order status...");
  }

  public void cancelOrder() {
    System.out.println("Cancelling order...");
  }

  public void notifyTeam() {
    System.out.println("Notifying the team...");
  }

  // =============== GETTERS & SETTERS ===============

  public boolean isBusy() {
    return busy;
  }

  public void setBusy(boolean busy) {
    this.busy = busy;
  }

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

  public List<Integer> getAssignedTableNumbers() {
    List<Integer> tableNumbers = new ArrayList<>();
    for (TableAssignment ta : assignedTables) {
      tableNumbers.add(ta.getTableNumber());
    }
    return tableNumbers;
  }

  public List<TableAssignment> getAssignedTables() {
    return assignedTables;
  }

  public void setAssignedTables(List<TableAssignment> assignedTables) {
    this.assignedTables = assignedTables;
  }

  public List<String> getCurrentOrders() {
    return currentOrders;
  }

  public void setCurrentOrders(List<String> currentOrders) {
    this.currentOrders = currentOrders;
  }
}

