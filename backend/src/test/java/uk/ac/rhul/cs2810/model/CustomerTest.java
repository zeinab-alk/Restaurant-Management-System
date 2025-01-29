package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the 'Customer' class.
 */
public class CustomerTest {

  Customer customer;
  
  @BeforeEach
  void beforeEach() {
    customer = new Customer();
  }

  @Test
  void customerTest() {
    //Test 1

    customer.setId(1234567890);

    assertEquals(1234567890, customer.getId());
  }

}
