package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents an alert request from a waiter.
 */
@Entity
public class AlertRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private int tableNumber;
  private String alertType;
  private boolean resolved;
  private String resolvedBy;

  @ManyToOne
  @JoinColumn(name = "waiter_id")
  private Waiter waiter;

  /**
   * Default constructor.
   */
  public AlertRequest() {}

  /**
   * Constructs an AlertRequest.
   *
   * @param tableNumber the table number needing assistance.
   * @param alertType type of alert (e.g., "urgent", "general").
   * @param waiter the assigned waiter.
   */
  public AlertRequest(int tableNumber, String alertType, Waiter waiter) {
    this.tableNumber = tableNumber;
    this.alertType = alertType;
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

  public String getAlertType() {
    return alertType;
  }

  public void setAlertType(String alertType) {
    this.alertType = alertType;
  }

  public boolean isResolved() {
    return resolved;
  }

  public void setResolved(boolean resolved) {
    this.resolved = resolved;
  }

  public String getResolvedBy() {
    return resolvedBy;
  }

  public void setResolvedBy(String resolvedBy) {
    this.resolvedBy = resolvedBy;
  }

  public Waiter getWaiter() {
    return waiter;
  }

  public void setWaiter(Waiter waiter) {
    this.waiter = waiter;
  }
}
