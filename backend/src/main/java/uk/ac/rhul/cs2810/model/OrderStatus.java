package uk.ac.rhul.cs2810.model;

/**
 * Represents order status.
 */
public enum OrderStatus {
  
  PENDING("Pending"), IN_PROGRESS("In Progress"), COMPLETED("Completed"), CANCELLED("Cancelled");
  
  final String label;
  
  OrderStatus(String label) {
    this.label = label;
  }
}