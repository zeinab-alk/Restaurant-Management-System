package uk.ac.rhul.cs2810.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * This represents a user who is associated with Oaxaca restaurant.
 */

@Entity
@Table(name = "users")
public class User {
  
  @Id
  @GeneratedValue
  private Long id;
  
  private String pin; // 8-digit PIN
  
  @Enumerated(EnumType.STRING)
  private UserRole role;
  
  public Long getId() {
    return id;
  }
  
  public String getPin() {
    return pin;
  }
  
  public void setPin(String pin) {
    this.pin = pin;
  }
  
  public UserRole getRole() {
    return role;
  }
  
  public void setRole(UserRole role) {
    this.role = role;
  }
}
