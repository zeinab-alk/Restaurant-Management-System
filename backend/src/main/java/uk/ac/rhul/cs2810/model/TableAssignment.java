package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a table assigned to a waiter.
 */
@Entity
@Table(name = "waiter_tables")
public class TableAssignment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "table_number", nullable = false)
  private int tableNumber;

  public TableAssignment() {}

  public TableAssignment(int tableNumber) {
    this.tableNumber = tableNumber;
  }

  public int getTableNumber() {
    return tableNumber;
  }

  public void setTableNumber(int tableNumber) {
    this.tableNumber = tableNumber;
  }
}
