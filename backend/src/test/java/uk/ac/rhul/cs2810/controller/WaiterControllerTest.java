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

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

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
  public void notifyWaiterNewOrder_WhenWaiterExists_ShouldReturnOrderDetails() {
    // Arrange
    int waiterId = 1;
    Waiter waiter = new Waiter();
    waiter.setId(waiterId);
    waiter.setName("John");

    MenuItem item1 = new MenuItem();
    item1.setName("Tacos");

    MenuItem item2 = new MenuItem();
    item2.setName("Burrito");

    Order order = new Order();
    order.setOrderNumber(101);
    order.setItems(Arrays.asList(item1, item2));

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));

    // Act
    ResponseEntity<String> response = waiterController.notifyWaiterNewOrder(waiterId, order);

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody().contains("Waiter John has been notified"));
    assertTrue(response.getBody().contains("Tacos"));
    assertTrue(response.getBody().contains("Burrito"));

    verify(waiterRepository, times(1)).save(waiter);
  }

  @Test
  public void notifyWaiterNewOrder_WhenWaiterDoesNotExist_ShouldReturnNotFound() {
    // Arrange
    int waiterId = 99;
    Order order = new Order();
    order.setOrderNumber(101);

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<String> response = waiterController.notifyWaiterNewOrder(waiterId, order);

    // Assert
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
    assertEquals("Waiter with ID 99 not found.", response.getBody());

    verify(waiterRepository, never()).save(any(Waiter.class));
  }
}

