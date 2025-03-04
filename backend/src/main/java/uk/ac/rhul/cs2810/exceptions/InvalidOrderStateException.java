package uk.ac.rhul.cs2810.exceptions;

/**
 * Exception thrown when an order status transition is invalid.
 * This exception is used when attempting to change an order to an invalid state
 * or when trying to perform operations not permitted in the current state.
 */
public class InvalidOrderStateException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public InvalidOrderStateException(String message) {
    super(message);
  }
}

