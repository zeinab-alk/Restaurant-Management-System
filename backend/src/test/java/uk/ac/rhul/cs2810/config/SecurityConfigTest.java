package uk.ac.rhul.cs2810.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Tests for SecurityConfig class.
 */
@SpringBootTest
public class SecurityConfigTest {

  @Autowired
  private SecurityConfig securityConfig;
  
  @Autowired
  private SecurityFilterChain filterChain;
  
  @Autowired
  private CorsConfigurationSource corsConfigurationSource;
  
  /**
   * Test that the security config beans are properly created.
   */
  @Test
  public void testSecurityConfigBeans() {
    assertNotNull(securityConfig, "SecurityConfig should be autowired");
    assertNotNull(filterChain, "SecurityFilterChain bean should be created");
    assertNotNull(corsConfigurationSource, "CorsConfigurationSource bean should be created");
  }
} 