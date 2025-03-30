package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PayRequestTest {

  @Test
  public void testConstructorAndGetters() {
    PayRequest request = new PayRequest(1, "1234678912345678", "11/25", "Zeynep Sener", "123");

    assertEquals(1, request.getOrderNumber());
    assertEquals("1234678912345678", request.getCardNumber());
    assertEquals("11/25", request.getExpiryDate());
    assertEquals("Zeynep Sener", request.getCardHolderName());
    assertEquals("123", request.getCvv());
  }

  @Test
  public void testSetters() {
    PayRequest request = new PayRequest(0, "", "", "", "");

    request.setOrderNumber(2);
    request.setCardNumber("1234678912345678");
    request.setExpiryDate("11/25");
    request.setCardHolderName("Zeynep Sener");
    request.setCvv("123");

    assertEquals(2, request.getOrderNumber());
    assertEquals("1234678912345678", request.getCardNumber());
    assertEquals("11/25", request.getExpiryDate());
    assertEquals("Zeynep Sener", request.getCardHolderName());
    assertEquals("123", request.getCvv());
  }
}
