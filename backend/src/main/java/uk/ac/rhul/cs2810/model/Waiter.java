package uk.ac.rhul.cs2810.model;

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
import java.util.ArrayList;
import java.util.List;



/**
 * Represents a waiter entity in the system.
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
   * {@link TableAssignment}
   *
   * <p>Waiter constructor.
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

  /**
   * Cancels an order by checking its status and marking it as CANCELLED.
   *
   * @param order The order to be cancelled.
   * @return true if the order was successfully cancelled , false otherwise.
   */
  public boolean cancelOrder(Order order) {
    if (order == null) {
      System.out.println("Order does not exist so cant be cancelled!");
      return false;
    }

    if (order.getStatus() == OrderStatus.CANCELLED) {
      System.out.println("Order " + order.getOrderNumber() + " is already canceled.");
      return false;
    }

    if (order.getStatus() == OrderStatus.DELIVERED) {
      System.out.println("Order " + order.getOrderNumber()
          + " has already been delivered and cannot be canceled.");
      return false;
    }

    order.setStatus(OrderStatus.CANCELLED);
    System.out.println("Order " + order.getOrderNumber() + " has been canceled successfully.");
    return true;
  }


  /**
   * Gets a list of tables assigned to this waiter that have been delivered
   * but remain unpaid.
   *
   * @param allOrders the list of all orders
   * @return a list of unpaid table numbers
   */
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

  /**
   * Tracks the order status.
   */
  public void trackOrder() {
    System.out.println("Tracking order...");
  }

  /**
   * Updates the order status.
   */
  public void updateOrderStatus() {
    System.out.println("Updating order status...");
  }

  /**
   * Cancels an order.
   */
  public void cancelOrder() {
    System.out.println("Cancelling order...");
  }


  /**
   * Notifies the team about the order status.
   */
  public void notifyTeam() {
    System.out.println("Notifying the team...");
  }

  /**
   * Checks if the waiter is busy.
   *
   * @return true if busy, false otherwise
   */
  public boolean isBusy() {
    return busy;
  }

  /**
   * Sets the waiter’s busy status.
   *
   * @param busy true if the waiter is busy, false otherwise
   */
  public void setBusy(boolean busy) {
    this.busy = busy;
  }

  /**
   * Retrieves the waiter's ID.
   *
   * @return the waiter's ID.
   */
  public int getId() {
    return id;
  }

  /**
   * Sets the waiter's ID.
   *
   * @param id the new waiter ID.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Retrieves the waiter's name.
   *
   * @return the waiter's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the waiter's name.
   *
   * @param name the new name of the waiter.
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Retrieves the current status of the waiter.
   *
   * @return the status of the waiter.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the status of the waiter.
   *
   * @param status the new status of the waiter.
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Retrieves the menu assigned to the waiter.
   *
   * @return the assigned menu.
   */
  public Menu getMenu() {
    return menu;
  }

  /**
   * Sets the menu assigned to the waiter.
   *
   * @param menu the new assigned menu.
   */
  public void setMenu(Menu menu) {
    this.menu = menu;
  }

  /**
   * Retrieves a list of table numbers assigned to the waiter.
   *
   * @return a list of assigned table numbers.
   */
  public List<Integer> getAssignedTableNumbers() {
    List<Integer> tableNumbers = new ArrayList<>();
    for (TableAssignment ta : assignedTables) {
      tableNumbers.add(ta.getTableNumber());
    }
    return tableNumbers;
  }

  /**
   * Returns a list of assigned table numbers.
   *
   * @return List of assigned table numbers.
   */
  public List<Integer> getAssignedTables() {
    List<Integer> tableNumbers = new ArrayList<>();
    for (TableAssignment ta : assignedTables) {
      tableNumbers.add(ta.getTableNumber());
    }
    return tableNumbers;
  }


  /**
   * Sets assigned tables by converting List&lt;Integer&gt; to List&lt;TableAssignment&gt;.
   *
   * @param tableNumbers A list of table numbers to assign.
   */
  public void setAssignedTables(List<Integer> tableNumbers) {
    assignedTables = new ArrayList<>();
    for (int tableNumber : tableNumbers) {
      assignedTables.add(new TableAssignment(tableNumber));
    }
  }


  /**
   * Retrieves the list of current orders assigned to the waiter.
   *
   * @return A list of current order IDs.
   */
  public List<String> getCurrentOrders() {
    return currentOrders;
  }

  /**
   * Updates the list of current orders assigned to the waiter.
   *
   * @param currentOrders The new list of current order IDs.
   */
  public void setCurrentOrders(List<String> currentOrders) {
    this.currentOrders = currentOrders;
  }

}

