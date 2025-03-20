package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.rhul.cs2810.exceptions.InvalidOrderStateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class OrderTest {
  private Order order;
  private Customer customer;
  private MenuItem item1;
  private MenuItem item2;

  @BeforeEach
  void setUp() {
    customer = new Customer(); // Placeholder until Customer class is implemented
    order = new Order(1, customer, null); // Pass nulls for removed fields

    List<String> dietaryRestrictions = new ArrayList<>();
    List<String> allergenInformation = new ArrayList<>();

    item1 = new MenuItem();
    item1.setItemId(1);
    item1.setName("Burger");
    item1.setPrice(5.99);
    item1.setCaloricInfo(500);
    item1.setDietaryRestrictions(dietaryRestrictions);
    item1.setAllergenInformation(allergenInformation);
    item1.setPopularity(10);

    item2 = new MenuItem();
    item2.setItemId(2);
    item2.setName("Fries");
    item2.setPrice(2.99);
    item2.setCaloricInfo(300);
    item2.setDietaryRestrictions(dietaryRestrictions);
    item2.setAllergenInformation(allergenInformation);
    item2.setPopularity(8);
  }

  @Test
  void testOrderConstructor() {
    assertEquals(1, order.getOrderNumber());
    assertEquals(customer, order.getCustomer());
    assertEquals(OrderStatus.PENDING, order.getStatus());
    assertTrue(order.getItems().isEmpty());
  }

  @Test
  void testSetAndGetOrderNumber() {
    order.setOrderNumber(2);
    assertEquals(2, order.getOrderNumber());
  }

  @Test
  void testSetAndGetCustomer() {
    Customer newCustomer = new Customer(); // Placeholder
    order.setCustomer(newCustomer);
    assertEquals(newCustomer, order.getCustomer());
  }

  @Test
  void testSetAndGetStatus() {
    order.setStatus(OrderStatus.IN_PROGRESS);
    assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
  }

  @Test
  void testAddItem() {
    order.addItem(item1);
    assertTrue(order.getItems().contains(item1));
    assertEquals(1, order.getItems().size());
  }

  @Test
  void testRemoveItem() {
    order.addItem(item1);
    order.addItem(item2);
    order.removeItem(item1);
    assertFalse(order.getItems().contains(item1));
    assertEquals(1, order.getItems().size());
  }

  @Test
  void testRemoveNonExistentItem() {
    order.addItem(item1);
    order.removeItem(item2); // item2 was never added
    assertEquals(1, order.getItems().size());
  }

  @Test
  void testGetOrderSum() {
    order.addItem(item1);
    order.addItem(item2);
    assertEquals(8.98, order.getOrderSum(), 0.01);
  }

  @Test
  void testGetOrderSumEmpty() {
    assertEquals(0.00, order.getOrderSum(), 0.01);
  }

  @Test
  void testGetOrderSumWithDuplicates() {
    order.addItem(item1);
    order.addItem(item1);
    assertEquals(11.98, order.getOrderSum(), 0.01);
  }

  @Test
  void testUpdateStatus() {
    order.updateOrderStatus(OrderStatus.COMPLETED);
    assertEquals(OrderStatus.COMPLETED, order.getStatus());
  }

  @Test
  void testMarkAsReadyForDelivery_ValidTransition() {
    order.setStatus(OrderStatus.IN_PROGRESS);
    order.markAsReadyForDelivery();
    assertEquals(OrderStatus.READY_FOR_DELIVERY, order.getStatus());
  }

  @Test
  void testMarkAsReadyForDelivery_InvalidTransition_ShouldThrowException() {
    order.setStatus(OrderStatus.PENDING);

    Exception exception = assertThrows(InvalidOrderStateException.class, () -> {
      order.markAsReadyForDelivery();
    });

    assertEquals(
        "Order must be IN_PROGRESS or PROCESSING before it can be marked as READY_FOR_DELIVERY.",
        exception.getMessage());
  }

  @Test
  void testUpdateStatus_ValidTransition() {
    order.setStatus(OrderStatus.PENDING);
    order.updateOrderStatus(OrderStatus.IN_PROGRESS);
    assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
  }

  @Test
  void testUpdateStatus_InvalidTransition_ShouldThrowException() {
    order.setStatus(OrderStatus.PENDING);

    Exception exception = assertThrows(InvalidOrderStateException.class, () -> {
      order.updateOrderStatus(OrderStatus.DELIVERED);
    });

    assertEquals("Order cannot skip directly from PENDING to DELIVERED.", exception.getMessage());
  }

  @Test
  void testOrderPlacedTimeIsSetOnCreation() {
    assertNotNull(order.getOrderPlacedTime(), "Order placed time should be set on creation.");
    assertNotNull(order.getOrderUpdatedTime(), "Order updated time should be set on creation.");
  }

  @Test
  void testOrderUpdatedTimeChangesOnUpdate() {
    LocalDateTime initialUpdateTime = order.getOrderUpdatedTime();

    // Ensure some time passes before updating
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      fail("Test interrupted unexpectedly");
    } // Small delay to ensure timestamp changes

    order.setStatus(OrderStatus.IN_PROGRESS);
    order.updateOrderStatus(OrderStatus.COMPLETED);

    assertTrue(order.getOrderUpdatedTime().isAfter(initialUpdateTime),
        "Order updated time should change when order is updated.");
  }

  @Test
  void testEstimatedCompletionTimeSetOnInProgress() {
    order.setWaitingTime(30);
    order.updateOrderStatus(OrderStatus.IN_PROGRESS);

    assertNotNull(order.getEstimatedCompletionTime(),
        "Estimated completion time should be set when order is IN_PROGRESS.");
    assertEquals(order.getOrderUpdatedTime().plusMinutes(30), order.getEstimatedCompletionTime(),
        "Estimated completion time should be calculated correctly.");
  }


}
