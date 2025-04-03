package uk.ac.rhul.cs2810.model;

/**
 * Response model for login operations.
 */
public class LoginResponse {
  private String role;

  /**
   * Constructor for LoginResponse.
   *
   * @param role The role of the user
   */
  public LoginResponse(String role) {
    this.role = role;
  }

  /**
   * Gets the role.
   *
   * @return The role
   */
  public String getRole() {
    return role;
  }
} 