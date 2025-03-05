package uk.ac.rhul.cs2810.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order placed by a customer at a restaurant. Contains details about the order,
 * including the customer, items, order time, waiting time, and status.
 */
@Entity
@Table(name = "orders") // Ensures the table name is properly created
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("orderNumber")
  private int orderNumber;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "order_menu_item",
      joinColumns = @JoinColumn(name = "order_id"),
      inverseJoinColumns = @JoinColumn(name = "item_id")
  )
  private List<MenuItem> menuItems;

  @JsonProperty("menuItems")
  public List<MenuItem> getItems() {
    return (this.menuItems != null) ? this.menuItems : new ArrayList<>();
  }


  private String orderTime;
  private int waitingTime;

  @JsonProperty("status")
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
    this.menuItems = new ArrayList<>();
  }

  /**
   * Default constructor (needed for JPA and JSON serialization).
   */
  public Order() {
    this.menuItems = new ArrayList<>();
  }

  public int getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(int orderNumber) {
    this.orderNumber = orderNumber;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public void setItems(List<MenuItem> items) {
    this.menuItems = (items != null) ? items : new ArrayList<>();
  }

  public String getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(String orderTime) {
    this.orderTime = orderTime;
  }

  public int getWaitingTime() {
    return waitingTime;
  }

  public void setWaitingTime(int waitingTime) {
    this.waitingTime = waitingTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * Adds a menu item to the order.
   *
   * @param menuItem The menu item to add.
   */
  public void addItem(MenuItem menuItem) {
    if (this.menuItems == null) {
      this.menuItems = new ArrayList<>();
    }
    menuItems.add(menuItem);
  }

  /**
   * Removes a menu item from the order.
   *
   * @param menuItem The menu item to remove.
   */
  public void removeItem(MenuItem menuItem) {
    if (this.menuItems != null) {
      menuItems.remove(menuItem);
    }
  }

  /**
   * Calculates the total price of all items in the order.
   *
   * @return The total price of the order.
   */
  public double getOrderSum() {
    return menuItems.stream().mapToDouble(MenuItem::getPrice).sum();
  }

  /**
   * Updates the status of the order.
   *
   * @param newStatus The new status to set.
   */
  public void updateStatus(String newStatus) {
    this.status = newStatus;
  }

  public List<MenuItem> getMenuItems() {
    return this.getItems(); // Ensure menuItems is a field in Order
  }


  /*
   * public String getOrderDetails() { // Some optional debug output }
   */

  /*
   * public String getOrderDetails() { StringBuilder details = new StringBuilder();
   * details.append("Order Number: ").append(orderNumber).append("\n");
   * //details.append("Customer: ").append(customer.getName()).append("\n");
   * details.append("Items:\n");
   *
   * for (MenuItem item : menuItems) {
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
