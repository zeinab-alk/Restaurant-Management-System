package uk.ac.rhul.cs2810.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Optional;
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
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.OrderRepository;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

@ExtendWith(MockitoExtension.class)
public class WaiterControllerTest {

  @Mock
  private WaiterRepository waiterRepository;

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private WaiterController waiterController;

  @BeforeEach
  public void setUp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
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

  // --- markOrderAsDelivered tests ---

  @Test
  public void markOrderAsDelivered_WhenOrderExists_ShouldMarkAsDelivered() {
    // Arrange
    int orderId = 1;
    Order order = new Order();
    order.setOrderNumber(123);
    order.setStatus("Pending"); // Initial status is not "Delivered"

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.markOrderAsDelivered(orderId);

    // Assert
    assertEquals("Order 123 has been marked as Delivered.", response.getBody());
    assertEquals("Delivered", order.getStatus()); // Ensure the status was updated
    verify(orderRepository, times(1)).save(order); // Verify save was called
  }

  @Test
  public void markOrderAsDelivered_WhenOrderAlreadyDelivered_ShouldReturnAlreadyDeliveredMessage() {
    // Arrange
    int orderId = 1;
    Order order = new Order();
    order.setOrderNumber(123);
    order.setStatus("Delivered"); // Status is already "Delivered"

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.markOrderAsDelivered(orderId);

    // Assert
    assertEquals("Order 123 is already delivered.", response.getBody());
    verify(orderRepository, never()).save(any(Order.class)); // Save should not be called
  }

  @Test
  public void markOrderAsDelivered_WhenOrderNotFound_ShouldReturnNotFoundMessage() {
    // Arrange
    int orderId = 1;
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<String> response = waiterController.markOrderAsDelivered(orderId);

    // Assert
    assertEquals("Order with ID 1 not found.", response.getBody());
    verify(orderRepository, never()).save(any(Order.class)); // Save should not be called
  }

  @Test
  public void markOrderAsDelivered_WhenOrderStatusIsNull_ShouldReturnErrorMessage() {
    // Arrange
    int orderId = 1;
    Order order = new Order();
    order.setOrderNumber(123);
    order.setStatus(null); // Simulating a null status

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.markOrderAsDelivered(orderId);

    // Assert
    assertEquals("Order status is invalid.", response.getBody());
    verify(orderRepository, never()).save(any(Order.class)); // Ensure order is NOT saved
  }

  @Test
  public void markOrderAsDelivered_WhenOrderStatusIsEmpty_ShouldReturnErrorMessage() {
    // Arrange
    int orderId = 1;
    Order order = new Order();
    order.setOrderNumber(123);
    order.setStatus(""); // Simulating an empty string

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.markOrderAsDelivered(orderId);

    // Assert
    assertEquals("Order status is invalid.", response.getBody());
    verify(orderRepository, never()).save(any(Order.class)); // Ensure order is NOT saved
  }

  @Test
  public void notifyWaiterNewOrder_WhenCurrentOrdersIsNull_ShouldHandleGracefully() {
    // Arrange
    int waiterId = 1;
    Order order = new Order();
    order.setOrderNumber(123);

    Waiter waiter = new Waiter();
    waiter.setId(waiterId);
    waiter.setName("Lana Shaheen");
    waiter.setCurrentOrders(null); // Explicitly setting currentOrders to null

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));

    // Act
    ResponseEntity<String> response = waiterController.notifyWaiterNewOrder(waiterId, order);

    // Assert
    String expected =
        "Waiter Lana Shaheen has been notified.\nOrder #123 contains: No items found.";
    assertEquals(expected, response.getBody());
    verify(waiterRepository, times(1)).save(waiter);
  }
}

