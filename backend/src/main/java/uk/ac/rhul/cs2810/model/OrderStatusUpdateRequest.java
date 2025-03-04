package uk.ac.rhul.cs2810.model;

/**
 * Request class for updating an order's status.
 */
public class OrderStatusUpdateRequest {
  private OrderStatus status;

  /**
   * Default constructor needed for JSON deserialization.
   */
  public OrderStatusUpdateRequest() {
  }

  /**
   * Constructor with status parameter.
   *
   * @param status The order status to set.
   */
  public OrderStatusUpdateRequest(OrderStatus status) {
    this.status = status;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }
}

