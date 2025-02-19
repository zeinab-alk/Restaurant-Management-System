/**
 * 
 */
package uk.ac.rhul.cs2810.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class WaiterControllerTest {
  @Mock
  private WaiterRepository waiterRepository;

  @InjectMocks
  private WaiterController waiterController;

  @BeforeEach
  public void setUp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  public void callWaiter_WhenWaiterAvailable_ShouldReturnSuccessMessage() {
    // Arrange
    int tableNumber = 5;
    Waiter availableWaiter = new Waiter();
    availableWaiter.setBusy(false);

    when(waiterRepository.findAvailableWaiter()).thenReturn(Optional.of(availableWaiter));

    // Act
    ResponseEntity<String> response = waiterController.callWaiter(tableNumber);

    // Assert
    assertEquals("A waiter is on the way to table " + tableNumber + "!", response.getBody());
    assertTrue(availableWaiter.isBusy());
    verify(waiterRepository, times(1)).save(availableWaiter);
  }

  @Test
  public void callWaiter_WhenNoWaiterAvailable_ShouldReturnErrorMessage() {
    // Arrange
    int tableNumber = 5;
    when(waiterRepository.findAvailableWaiter()).thenReturn(Optional.empty());

    // Act
    ResponseEntity<String> response = waiterController.callWaiter(tableNumber);

    // Assert
    assertEquals("No waiters available. Please call +44 123 456 789 for assistance.",
        response.getBody());
    verify(waiterRepository, never()).save(any(Waiter.class));
  }

  @Test
  public void notifyWaiterNewOrder_WhenWaiterExists_ShouldNotifyWaiter() {
    // Arrange
    int waiterId = 1;
    Order order = new Order(); // Initialise your Order object
    order.setOrderNumber(123); // Set necessary properties for the order
    Waiter waiter = new Waiter();
    waiter.setId(waiterId);
    waiter.setName("John Doe");

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));

    // Act
    ResponseEntity<String> response = waiterController.notifyWaiterNewOrder(waiterId, order);

    // Assert
    assertEquals("Waiter John Doe has been notified of new order: 123", response.getBody());
    assertTrue(waiter.getCurrentOrders().contains("123"));
    verify(waiterRepository, times(1)).save(waiter); // Verify save is called
  }

  @Test
  public void notifyWaiterNewOrder_WhenWaiterNotFound_ShouldReturnNotFound() {
    // Arrange
    int waiterId = 1;
    Order order = new Order(); // Initialise your Order object
    when(waiterRepository.findById(waiterId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<String> response = waiterController.notifyWaiterNewOrder(waiterId, order);

    // Assert
    assertEquals("Waiter with ID 1 not found.", response.getBody());
    verify(waiterRepository, never()).save(any(Waiter.class)); // Ensure save is not called
  }



}
