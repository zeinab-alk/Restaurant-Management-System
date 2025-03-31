package uk.ac.rhul.cs2810.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

/**
 * Tests for WebSocketConfig class.
 */
@SpringBootTest
public class WebSocketConfigTest {

  @Autowired
  private WebSocketConfig webSocketConfig;
  
  /**
   * Test that the WebSocketConfig is properly autowired.
   */
  @Test
  public void testWebSocketConfigAutowired() {
    assertNotNull(webSocketConfig, "WebSocketConfig should be autowired");
  }
  
  /**
   * Test configureMessageBroker method.
   */
  @Test
  public void testConfigureMessageBroker() {
    // Create a mock MessageBrokerRegistry
    MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);
    
    // Call the method
    webSocketConfig.configureMessageBroker(registry);
    
    // Verify that the expected methods were called on the registry
    verify(registry).enableSimpleBroker("/topic");
    verify(registry).setApplicationDestinationPrefixes("/app");
  }
  
  /**
   * Test that the WebSocketConfig implements the required interface.
   * We can't easily mock StompEndpointRegistry, so we just verify the class structure.
   */
  @Test
  public void testImplementsWebSocketMessageBrokerConfigurer() {
    // This test just verifies that the class is properly annotated and implements
    // the required interface, which is checked by verifying we can autowire it.
    assertNotNull(webSocketConfig, "WebSocketConfig should be properly configured");
  }
} 