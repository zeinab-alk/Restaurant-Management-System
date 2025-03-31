package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AlertRequestTest {

  @Test
  void testDefaultConstructor() {
    AlertRequest alertRequest = new AlertRequest();

    // Verify defaults
    assertNull(alertRequest.getId(), "ID should be null by default");
    assertEquals(0, alertRequest.getTableNumber(), "Table number should default to 0");
    assertNull(alertRequest.getAlertType(), "Alert type should be null by default");
    assertFalse(alertRequest.isResolved(), "Resolved should be false by default");
    assertNull(alertRequest.getResolvedBy(), "resolvedBy should be null by default");
    assertNull(alertRequest.getWaiter(), "Waiter should be null by default");
  }

  @Test
  void testParameterizedConstructor() {
    Waiter waiter = new Waiter(1, "Alice", "active", null);

    AlertRequest alertRequest = new AlertRequest(5, "urgent", waiter);

    // Verify fields from constructor
    assertNull(alertRequest.getId(), "ID should be null until saved by the database");
    assertEquals(5, alertRequest.getTableNumber(),
        "Table number should match constructor argument");
    assertEquals("urgent", alertRequest.getAlertType(),
        "Alert type should match constructor argument");
    assertFalse(alertRequest.isResolved(), "Resolved should be false by default");
    assertNull(alertRequest.getResolvedBy(), "resolvedBy should still be null");
    assertEquals(waiter, alertRequest.getWaiter(),
        "Waiter should match the one passed to constructor");
  }

  @Test
  void testSettersAndGetters() {
    AlertRequest alertRequest = new AlertRequest();
    Waiter anotherWaiter = new Waiter(2, "Zeynep", "busy", null);

    // Set each field
    alertRequest.setId(100L);
    alertRequest.setTableNumber(10);
    alertRequest.setAlertType("general");
    alertRequest.setResolved(true);
    alertRequest.setResolvedBy("Manager");
    alertRequest.setWaiter(anotherWaiter);

    // Verify each field
    assertEquals(100L, alertRequest.getId());
    assertEquals(10, alertRequest.getTableNumber());
    assertEquals("general", alertRequest.getAlertType());
    assertTrue(alertRequest.isResolved());
    assertEquals("Manager", alertRequest.getResolvedBy());
    assertEquals(anotherWaiter, alertRequest.getWaiter());
  }
}
