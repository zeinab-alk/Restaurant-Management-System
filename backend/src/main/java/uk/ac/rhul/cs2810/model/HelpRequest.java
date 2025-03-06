package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a help request from a customer.
 */
@Entity
public class HelpRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private int tableNumber;
  private boolean resolved;

  @ManyToOne
  @JoinColumn(name = "waiter_id")
  private Waiter waiter;

  /**
   * Default constructor.
   */
  public HelpRequest() {}

  /**
   * Constructs a HelpRequest.
   *
   * @param tableNumber the table number requesting help.
   * @param waiter the assigned waiter.
   */
  public HelpRequest(int tableNumber, Waiter waiter) {
    this.tableNumber = tableNumber;
    this.waiter = waiter;
    this.resolved = false;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public int getTableNumber() {
    return tableNumber;
  }

  public void setTableNumber(int tableNumber) {
    this.tableNumber = tableNumber;
  }

  /**
   * Marks the request as resolved.
   */
  public boolean isResolved() {
    return resolved;
  }

  public void setResolved(boolean resolved) {
    this.resolved = resolved;
  }

  public Waiter getWaiter() {
    return waiter;
  }

  public void setWaiter(Waiter waiter) {
    this.waiter = waiter;
  }
}

