package uk.ac.rhul.cs2810.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import uk.ac.rhul.cs2810.exceptions.InvalidOrderStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an order placed by a customer at a restaurant. Contains details about the order,
 * including customer, items, order time, waiting time, and status.
 */
@Entity
@Table(name = "orders") // Ensures the table name is properly created
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

  private static final Logger logger = LoggerFactory.getLogger(Order.class);

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty("orderNumber")
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

  @Enumerated(EnumType.STRING)
  @JsonProperty("status")
  private OrderStatus status;

  /**
   * Constructs an Order with the given parameters.
   *
   * @param orderNumber The unique order number.
   * @param customer The customer who placed the order.
   * @param orderTime The time the order was placed.
   * @param waitingTime The estimated waiting time for the order.
   * @param status The current status of the order.
   */
  public Order(int orderNumber, Customer customer, String orderTime, int waitingTime,
      OrderStatus status) {
    this.orderNumber = orderNumber;
    this.customer = customer;
    this.orderTime = orderTime;
    this.waitingTime = waitingTime;
    this.status = status;
    this.items = new ArrayList<>();
  }

  /**
   * Default constructor (needed for JPA and JSON serialization).
   */
  public Order() {
    this.items = new ArrayList<>();
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

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  /**
   * Ensures items list is always initialized before returning.
   */
  @JsonProperty("menuItems")
  public List<MenuItem> getItems() {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    return this.items;
  }

  public void setItems(List<MenuItem> items) {
    this.items = (items != null) ? items : new ArrayList<>();
  }

  /**
   * Adds a menu item to the order.
   *
   * @param menuItem The menu item to add.
   */
  public void addItem(MenuItem menuItem) {
    getItems().add(menuItem);
  }

  /**
   * Removes a menu item from the order.
   *
   * @param menuItem The menu item to remove.
   */
  public void removeItem(MenuItem menuItem) {
    getItems().remove(menuItem);
  }

  /**
   * Calculates the total price of all items in the order.
   *
   * @return The total price of the order.
   */
  public double getOrderSum() {
    return getItems().stream().mapToDouble(MenuItem::getPrice).sum();
  }

  /**
   * Marks an order as READY_FOR_DELIVERY, ensuring a valid transition.
   */
  public void markAsReadyForDelivery() {
    if (this.status != OrderStatus.IN_PROGRESS && this.status != OrderStatus.PROCESSING) {
      throw new InvalidOrderStateException(
          "Order must be IN_PROGRESS or PROCESSING before it can be marked as READY_FOR_DELIVERY.");
    }
    logger.info("Order {} status changed from {} to READY_FOR_DELIVERY.", this.orderNumber,
        this.status);
    this.status = OrderStatus.READY_FOR_DELIVERY;
  }

  /**
   * Updates the status of an order while ensuring valid transitions.
   *
   * @param newStatus The new status to set.
   */
  public void updateStatus(OrderStatus newStatus) {
    // Prevent skipping key order statuses
    if (this.status == OrderStatus.PENDING && newStatus == OrderStatus.DELIVERED) {
      throw new InvalidOrderStateException("Order cannot skip directly from PENDING to DELIVERED.");
    }
    logger.info("Order {} status changed from {} to {}", this.orderNumber, this.status, newStatus);
    this.status = newStatus;
  }

  /**
   * Retrieves the list of menu items in the order.
   */
  public List<MenuItem> getMenuItems() {
    return getItems();
  }
}

