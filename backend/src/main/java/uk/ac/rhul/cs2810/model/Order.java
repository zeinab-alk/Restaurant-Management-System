package uk.ac.rhul.cs2810.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Represents an order placed by a customer at a restaurant. This class contains details about the
 * order, including the customer, items, order time, waiting time, and status.
 */
@Entity
@Table(name = "orders") // Ensures the table name is properly created
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int orderNumber;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToMany
  @JoinTable(name = "order_menu_items", joinColumns = @JoinColumn(name = "order_id"),
      inverseJoinColumns = @JoinColumn(name = "menu_item_id"))
  private List<MenuItem> items = new ArrayList<>();

  private String orderTime;
  private int waitingTime;
  private String status;

  /**
   * Constructs an Order with the given parameters.
   *
   * @param orderNumber The unique order number.
   * @param customer The customer who placed the order.
   * @param orderTime The time the order was placed.
   * @param waitingTime The estimated waiting time for the order.
   * @param status The current status of the order (e.g., "Pending", "Completed").
   */
  public Order(int orderNumber, Customer customer, String orderTime, int waitingTime,
      String status) {
    this.orderNumber = orderNumber;
    this.customer = customer;
    this.orderTime = orderTime;
    this.waitingTime = waitingTime;
    this.status = status;
  }

  // Default constructor (needed for JPA)
  public Order() {
    this.items = new ArrayList<>();
  }

  // Getter and Setter methods

  /**
   * Gets the order number.
   *
   * @return The order number.
   */
  public int getOrderNumber() {
    return orderNumber;
  }

  /**
   * Sets the order number.
   *
   * @param orderNumber The order number to set.
   */
  public void setOrderNumber(int orderNumber) {
    this.orderNumber = orderNumber;
  }

  /**
   * Gets the customer who placed the order.
   *
   * @return The customer.
   */
  public Customer getCustomer() {
    return customer;
  }

  /**
   * Sets the customer for the order.
   *
   * @param customer The customer to set.
   */
  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  /**
   * Gets the list of items in the order.
   *
   * @return A list of menu items.
   */
  public List<MenuItem> getItems() {
    return items;
  }

  /**
   * Sets the list of items in the order.
   *
   * @param items The list of menu items to set.
   */
  public void setItems(List<MenuItem> items) {
    this.items = items;
  }

  /**
   * Gets the time the order was placed.
   *
   * @return The order time as a string.
   */
  public String getOrderTime() {
    return orderTime;
  }

  /**
   * Sets the order time.
   *
   * @param orderTime The order time to set.
   */
  public void setOrderTime(String orderTime) {
    this.orderTime = orderTime;
  }

  /**
   * Gets the estimated waiting time for the order.
   *
   * @return The waiting time in minutes.
   */
  public int getWaitingTime() {
    return waitingTime;
  }

  /**
   * Sets the estimated waiting time for the order.
   *
   * @param waitingTime The waiting time in minutes.
   */
  public void setWaitingTime(int waitingTime) {
    this.waitingTime = waitingTime;
  }

  /**
   * Gets the current status of the order.
   *
   * @return The order status.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the order status.
   *
   * @param status The status to set.
   */
  public void setStatus(String status) {
    this.status = status;
  }

  // Methods

  /**
   * Adds a menu item to the order.
   *
   * @param menuItem The menu item to add.
   */
  public void addItem(MenuItem menuItem) {
    items.add(menuItem);
  }

  /**
   * Removes a menu item from the order.
   *
   * @param menuItem The menu item to remove.
   */
  public void removeItem(MenuItem menuItem) {
    items.remove(menuItem);
  }

  /**
   * Calculates the total price of all items in the order.
   *
   * @return The total price of the order.
   */
  public double getOrderSum() {
    double total = 0;
    for (MenuItem item : items) {
      total += item.getPrice();
    }
    return total;
  }

  /**
   * Updates the status of the order.
   *
   * @param newStatus The new status to set.
   */
  public void updateStatus(String newStatus) {
    this.status = newStatus;
  }

  /*
   * public String getOrderDetails() { StringBuilder details = new StringBuilder();
   * details.append("Order Number: ").append(orderNumber).append("\n");
   * //details.append("Customer: ").append(customer.getName()).append("\n");
   * details.append("Items:\n");
   * 
   * for (MenuItem item : items) {
   * details.append("- ").append(item.getName()).append(": $").append(item.getPrice())
   * .append("\n"); }
   * 
   * details.append("Total Price: $").append(getOrderSum()).append("\n");
   * details.append("Order Time: ").append(orderTime).append("\n");
   * details.append("Waiting Time: ").append(waitingTime).append(" minutes\n");
   * details.append("Status: ").append(status).append("\n");
   * 
   * return details.toString(); }
   */

}
