package uk.ac.rhul.cs2810.model;

public class OrderStatusUpdateRequest {
  private OrderStatus status;

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }
}

