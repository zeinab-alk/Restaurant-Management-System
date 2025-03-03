package uk.ac.rhul.cs2810.exceptions;

public class InvalidOrderStateException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public InvalidOrderStateException(String message) {
    super(message);
  }
}

