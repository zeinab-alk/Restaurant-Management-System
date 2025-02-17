package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTest {
  private Order order;
  private Customer customer;
  private MenuItem item1;
  private MenuItem item2;

  @BeforeEach
  void setUp() {
    customer = new Customer(); // Placeholder until Customer class is implemented
    order = new Order(1, customer, "12:30 PM", 15, "Pending");

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
    assertEquals("12:30 PM", order.getOrderTime());
    assertEquals(15, order.getWaitingTime());
    assertEquals("Pending", order.getStatus());
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
  void testSetAndGetOrderTime() {
    order.setOrderTime("1:00 PM");
    assertEquals("1:00 PM", order.getOrderTime());
  }

  @Test
  void testSetAndGetWaitingTime() {
    order.setWaitingTime(20);
    assertEquals(20, order.getWaitingTime());
  }

  @Test
  void testSetAndGetStatus() {
    order.setStatus("In Progress");
    assertEquals("In Progress", order.getStatus());
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
    order.updateStatus("Completed");
    assertEquals("Completed", order.getStatus());
  }
}
