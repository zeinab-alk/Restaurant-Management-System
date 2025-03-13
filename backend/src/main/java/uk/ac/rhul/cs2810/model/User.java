package uk.ac.rhul.cs2810.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class User {
  
  @Id
  @GeneratedValue
  private Long id;
  
  private String username;
  private String password;
  private UserRole role;

}
