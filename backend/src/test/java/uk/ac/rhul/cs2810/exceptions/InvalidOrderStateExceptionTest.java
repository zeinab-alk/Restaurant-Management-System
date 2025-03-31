package uk.ac.rhul.cs2810.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for the InvalidOrderStateException class.
 */
public class InvalidOrderStateExceptionTest {

  /**
   * Test that the exception properly stores the message.
   */
  @Test
  public void testExceptionMessage() {
    String errorMessage = "Cannot transition from PENDING to DELIVERED";
    InvalidOrderStateException exception = new InvalidOrderStateException(errorMessage);
    
    assertEquals(errorMessage, exception.getMessage());
  }
} 