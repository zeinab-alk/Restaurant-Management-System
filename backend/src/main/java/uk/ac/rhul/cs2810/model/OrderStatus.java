package uk.ac.rhul.cs2810.model;

/**
 * Enumeration representing the various states an order can be in during its lifecycle.
 *
 * <p>
 * The possible states are:
 * <ul>
 * <li>PENDING - Order has been placed but not yet processed</li>
 * <li>IN_PROGRESS - Order is being prepared in the kitchen</li>
 * <li>READY_FOR_DELIVERY - Order is prepared and waiting for a waiter to deliver</li>
 * <li>COMPLETED - Order has been served and completed</li>
 * <li>DELIVERED - Order has been delivered to the customer</li>
 * <li>PROCESSING - Order is being processed by the system</li>
 * <li>CANCELLED - Order has been cancelled</li>
 * <li>PAID - Order has been paid</li>
 * </ul>
 */
public enum OrderStatus {
  PENDING, IN_PROGRESS, READY_FOR_DELIVERY, COMPLETED, DELIVERED, PROCESSING, CANCELLED, PAID;
}
