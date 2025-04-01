package uk.ac.rhul.cs2810.model;

/**
 * Request model for login operations.
 */
public class LoginRequest {
  private String pin;

  /**
   * Gets the PIN.
   *
   * @return The PIN
   */
  public String getPin() {
    return pin;
  }

  /**
   * Sets the PIN.
   *
   * @param pin The PIN to set
   */
  public void setPin(String pin) {
    this.pin = pin;
  }
} 