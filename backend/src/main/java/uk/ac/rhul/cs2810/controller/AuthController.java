package uk.ac.rhul.cs2810.controller;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.LoginRequest;
import uk.ac.rhul.cs2810.model.LoginResponse;
import uk.ac.rhul.cs2810.model.User;
import uk.ac.rhul.cs2810.model.UserRole;
import uk.ac.rhul.cs2810.repository.UserRepository;

/**
 * Controller for handling authentication-related requests.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

  private final UserRepository userRepository;

  /**
  * Constructor for AuthController.
  *
  * @param userRepository The repository for user operations
  */
  
  public AuthController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
  * Handles user login requests.
  *
  * @param request The login request containing the PIN
  * @return ResponseEntity containing the login response
  */
  
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    return userRepository.findByPin(request.getPin())
      .map(user -> ResponseEntity.ok(new LoginResponse(user.getRole().name())))
      .orElse(ResponseEntity.badRequest().body(new LoginResponse("INVALID")));
  }
} 