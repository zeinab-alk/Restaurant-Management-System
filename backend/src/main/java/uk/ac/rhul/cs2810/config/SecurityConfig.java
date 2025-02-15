package uk.ac.rhul.cs2810.config;

import static org.springframework.security.config.Customizer.withDefaults;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuration class for security.
 */
@Configuration
public class SecurityConfig {

  /**
   * Configures the security filter chain, CSRF and CORS.
   *
   * @param http the object to configure
   * @return the configured SecurityFilterChain
   * @throws Exception if an error occurs when configuring
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()) // Disable CSRF (use with caution)
        .cors(withDefaults()) // Enable CORS with default settings
        .authorizeHttpRequests(auth -> auth.requestMatchers("/api/**").permitAll() // Allow all API
                                                                                   // requests
            .anyRequest().authenticated()); // Secure other endpoints

    return http.build();
  }

  /**
   * Configures CORS settings for the application.
   *
   * @return the configured CorsConfigurationSource
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(Arrays.asList("*")); // Allow all origins
    config.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Explicitly
                                                                                        // allow
                                                                                        // these
                                                                                        // HTTP
                                                                                        // methods
    config.setAllowCredentials(true); // Allow credentials (for authentication)

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }
}

