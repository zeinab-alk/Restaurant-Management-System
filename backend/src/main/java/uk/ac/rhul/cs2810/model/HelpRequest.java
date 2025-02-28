package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Represents a request for assistance from a client to a waiter.
 */
@Entity
@Table(name = "help_requests")
public class HelpRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private int tableNumber;
  private boolean resolved = false;
  private LocalDateTime requestTime;

  @ManyToOne
  @JoinColumn(name = "waiter_id")
  private Waiter assignedWaiter;

  /**
   * Default constructor.
   * Initializes the requestTime to the current time.
   */
  public HelpRequest() {
    this.requestTime = LocalDateTime.now();
  }

  /**
   * Constructs a new HelpRequest for a specific table and assigned waiter.
   *
   * @param tableNumber    the number of the table
   * @param assignedWaiter the waiter assigned to this request
   */
  public HelpRequest(int tableNumber, Waiter assignedWaiter) {
    this.tableNumber = tableNumber;
    this.assignedWaiter = assignedWaiter;
    this.requestTime = LocalDateTime.now();
  }

  /**
   * Returns the unique identifier of the help request.
   *
   * @return the request ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Returns the table number associated with the help request.
   *
   * @return the table number
   */
  public int getTableNumber() {
    return tableNumber;
  }

  /**
   * Sets whether the help request is resolved.
   *
   * @param resolved true if resolved, false otherwise
   */
  public void setResolved(boolean resolved) {
    this.resolved = resolved;
  }

  /**
   * Checks if the help request is resolved.
   *
   * @return true if resolved, false otherwise
   */
  public boolean isResolved() {
    return resolved;
  }

  /**
   * Returns the time when the help request was made.
   *
   * @return the request time
   */
  public LocalDateTime getRequestTime() {
    return requestTime;
  }

  /**
   * Returns the assigned waiter for this help request.
   *
   * @return the assigned waiter
   */
  public Waiter getAssignedWaiter() {
    return assignedWaiter;
  }
}
