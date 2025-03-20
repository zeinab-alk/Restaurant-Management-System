package uk.ac.rhul.cs2810.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.rhul.cs2810.exceptions.InvalidOrderStateException;

/**
 * Represents an order placed by a customer at a restaurant. Contains details about the order,
 * including customer, items, order time, and status.
 */
@Entity
@Table(name = "orders")
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

  @Enumerated(EnumType.STRING)
  @JsonProperty("status")
  private OrderStatus status;

  @Column(name = "waiting_time")
  private int waitingTime;

  @Column(name = "order_placed_time", nullable = false)
  private LocalDateTime orderPlacedTime;

  @Column(name = "order_updated_time")
  private LocalDateTime orderUpdatedTime;

  @Column(name = "estimated_completion_time")
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

  // Constructors
  public Order() {
    this.menuItems = new ArrayList<>();
  }

  public Order(int orderNumber, Customer customer, OrderStatus status) {
    this.orderNumber = orderNumber;
    this.customer = customer;
    this.status = status;
    this.menuItems = new ArrayList<>();
    this.orderPlacedTime = LocalDateTime.now();
    this.orderUpdatedTime = LocalDateTime.now();
  }

  // Getters and setters
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

  public List<MenuItem> getMenuItems() {
    return menuItems != null ? menuItems : new ArrayList<>();
  }

  public void setItems(List<MenuItem> items) {
    this.menuItems = (items != null) ? items : new ArrayList<>();
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public int getWaitingTime() {
    return waitingTime;
  }

  public void setWaitingTime(int waitingTime) {
    this.waitingTime = waitingTime;
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

  // Order logic
  @JsonProperty("menuItems")
  public List<MenuItem> getItems() {
    return getMenuItems();
  }

  public void addItem(MenuItem menuItem) {
    if (this.menuItems == null) {
      this.menuItems = new ArrayList<>();
    }
    this.menuItems.add(menuItem);
  }

  public void removeItem(MenuItem menuItem) {
    if (this.menuItems != null) {
      this.menuItems.remove(menuItem);
    }
  }

  public double getOrderSum() {
    return menuItems.stream().mapToDouble(MenuItem::getPrice).sum();
  }

  public void markAsReadyForDelivery() {
    if (this.status != OrderStatus.IN_PROGRESS && this.status != OrderStatus.PROCESSING) {
      throw new InvalidOrderStateException(
          "Order must be IN_PROGRESS or PROCESSING before it can be marked as READY_FOR_DELIVERY.");
    }
    logger.info("Order {} status changed from {} to READY_FOR_DELIVERY.", this.orderNumber,
        this.status);
    this.status = OrderStatus.READY_FOR_DELIVERY;
  }

  public void updateOrderStatus(OrderStatus newStatus) {
    if (this.status == OrderStatus.PENDING && newStatus == OrderStatus.DELIVERED) {
      throw new InvalidOrderStateException("Order cannot skip directly from PENDING to DELIVERED.");
    }

    this.status = newStatus;
    this.orderUpdatedTime = LocalDateTime.now();

    if (newStatus == OrderStatus.IN_PROGRESS) {
      this.estimatedCompletionTime = this.orderUpdatedTime.plusMinutes(15);
    }

    logger.info("Order {} status changed to {}", this.orderNumber, newStatus);
  }

}
