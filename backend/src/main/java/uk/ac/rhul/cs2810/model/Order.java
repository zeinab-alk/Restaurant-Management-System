package uk.ac.rhul.cs2810.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.rhul.cs2810.exceptions.InvalidOrderStateException;



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

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "order_menu_item", joinColumns = @JoinColumn(name = "order_id"),
      inverseJoinColumns = @JoinColumn(name = "item_id"))
  private List<MenuItem> menuItems;

  private String orderTime;
  private int waitingTime;

  @Enumerated(EnumType.STRING)
  @JsonProperty("status")
  private OrderStatus status;

  @Column(nullable = false)
  private LocalDateTime orderPlacedTime;

  private LocalDateTime orderUpdatedTime;
  private LocalDateTime estimatedCompletionTime;

  @PrePersist
  protected void onCreate() {
    this.orderPlacedTime = LocalDateTime.now();
    this.orderUpdatedTime = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.orderUpdatedTime = LocalDateTime.now();
  }

  public LocalDateTime getOrderPlacedTime() {
    return orderPlacedTime;
  }

  public void setOrderPlacedTime(LocalDateTime orderPlacedTime) {
    this.orderPlacedTime = orderPlacedTime;
  }

  public LocalDateTime getOrderUpdatedTime() {
    return orderUpdatedTime;
  }

  public void setOrderUpdatedTime(LocalDateTime orderUpdatedTime) {
    this.orderUpdatedTime = orderUpdatedTime;
  }

  public LocalDateTime getEstimatedCompletionTime() {
    return estimatedCompletionTime;
  }

  public void setEstimatedCompletionTime(LocalDateTime estimatedCompletionTime) {
    this.estimatedCompletionTime = estimatedCompletionTime;
  }


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
    this.menuItems = new ArrayList<>();
    this.orderPlacedTime = LocalDateTime.now();
    this.orderUpdatedTime = LocalDateTime.now();
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
    return (this.menuItems != null) ? this.menuItems : new ArrayList<>();
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
    // Validate status transitions
    if (this.status == null) {
      // Allow any status if current status is null (new order)
      logger.info("Order {} status set to {} (initial status).", this.orderNumber, newStatus);
      this.status = newStatus;
      this.orderUpdatedTime = LocalDateTime.now();
      return;
    }

    // Define valid transitions
    boolean isValidTransition = switch (this.status) {
      case PENDING -> newStatus == OrderStatus.IN_PROGRESS 
                     || newStatus == OrderStatus.CANCELLED 
                     || newStatus == OrderStatus.COMPLETED;  // Allow direct completion
      case IN_PROGRESS -> newStatus == OrderStatus.READY_FOR_DELIVERY 
                         || newStatus == OrderStatus.CANCELLED 
                         || newStatus == OrderStatus.COMPLETED;  // Allow direct completion
      case READY_FOR_DELIVERY -> newStatus == OrderStatus.DELIVERED 
                                || newStatus == OrderStatus.CANCELLED 
                                || newStatus == OrderStatus.COMPLETED;  // Allow direct completion
      case DELIVERED -> newStatus == OrderStatus.COMPLETED 
                       || newStatus == OrderStatus.PAID;
      case COMPLETED -> newStatus == OrderStatus.PAID;
      case PAID -> false; // Terminal state
      case CANCELLED -> false; // Terminal state
      case PROCESSING -> newStatus == OrderStatus.READY_FOR_DELIVERY 
                        || newStatus == OrderStatus.CANCELLED 
                        || newStatus == OrderStatus.COMPLETED;  // Allow direct completion
    };

    if (!isValidTransition) {
      String errorMessage = String.format(
          "Order cannot skip directly from %s to %s.", 
          this.status, 
          newStatus
      );
      logger.error(errorMessage);
      throw new InvalidOrderStateException(errorMessage);
    }

    logger.info("Order {} status changed from {} to {}.", this.orderNumber, this.status, newStatus);
    this.status = newStatus;
    this.orderUpdatedTime = LocalDateTime.now();

    // If order is now in progress, calculate estimated completion time
    if (newStatus == OrderStatus.IN_PROGRESS) {
      this.estimatedCompletionTime = this.orderUpdatedTime.plusMinutes(waitingTime);
    }
  }

  /**
   * Retrieves the list of menu items in the order.
   */
  public List<MenuItem> getMenuItems() {
    return getItems();
  }

  /*
   * public String getOrderDetails() { // Some optional debug output }
   */
}

