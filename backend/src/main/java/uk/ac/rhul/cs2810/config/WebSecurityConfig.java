package uk.ac.rhul.cs2810.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import uk.ac.rhul.cs2810.repository.UserRepository;

/**
 * Configuration class for Web security.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

  /**
   * Configures the security filter chain, CSRF and CORS.
   *
   * @param http the object to configure
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs when configuring
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf().disable()
        .authorizeHttpRequests(
            (requests) -> requests
                // Public endpoints
                .requestMatchers("/", "/home", "/api/auth/**").permitAll()
                // Waiter CRUD - GET authenticated, Write requires MANAGER
                .requestMatchers(HttpMethod.GET, "/api/waiters/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/waiters").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/waiters/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/waiters/**").hasRole("MANAGER")
                // Existing role-specific paths
                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/api/waiter/**").hasRole("WAITER")
                .requestMatchers("/api/kitchen/**").hasRole("KITCHEN")
                // Fallback: Any other request needs authentication
                .anyRequest().authenticated()
        )
        .formLogin((form) -> form.loginPage("/login").permitAll())
        .logout((logout) -> logout.permitAll());

    return http.build();
  }

  /**
   * Provides user details for authentication.
   *
   * @param userRepository userRepository
   * @return username
   */
  @Bean
  public UserDetailsService userDetailsService(UserRepository userRepository) {
    return username -> userRepository.findByUsername(username)
        .map(user -> org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername()).password(user.getPassword()).roles(user.getRole().name())
            .build())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  /**
   * Encodes password using BCrypt.
   *
   * @return encoded password
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

