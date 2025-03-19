package uk.ac.rhul.cs2810.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.OrderRepository;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

@ExtendWith(MockitoExtension.class)
public class KitchenControllerTest {


  @Mock
  private OrderRepository orderRepository;

  @Mock
  private WaiterRepository waiterRepository;

  @InjectMocks
  private KitchenController kitchenController;

  @Test
  void testGetPreparingOrders_ReturnsPreparingOrders() {
    // Arrange
    Order o1 = new Order();
    o1.setOrderNumber(1);
    o1.setStatus(Order.STATUS_PREPARING);

    Order o2 = new Order();
    o2.setOrderNumber(2);
    o2.setStatus(Order.STATUS_PREPARING);

    List<Order> preparingOrders = Arrays.asList(o1, o2);

    when(orderRepository.findByStatus(Order.STATUS_PREPARING)).thenReturn(preparingOrders);

    // Act
    ResponseEntity<List<Order>> response = kitchenController.getPreparingOrders();

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    verify(orderRepository).findByStatus(Order.STATUS_PREPARING);
  }

  @Test
  void testMarkOrderAsReady_OrderExists_MarksAsReadyAndNotifiesWaiters() {
    // Arrange
    Order existingOrder = new Order();
    existingOrder.setOrderNumber(101);
    existingOrder.setStatus(Order.STATUS_PREPARING);

    Waiter w1 = new Waiter();
    w1.setId(1);
    w1.setName("Alice");

    Waiter w2 = new Waiter();
    w2.setId(2);
    w2.setName("Bob");

    when(orderRepository.findById(101)).thenReturn(Optional.of(existingOrder));
    when(waiterRepository.findAll()).thenReturn(Arrays.asList(w1, w2));

    // Act
    ResponseEntity<Order> response = kitchenController.markOrderAsReady(101);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    Order updatedOrder = response.getBody();
    assertNotNull(updatedOrder);
    assertEquals(Order.STATUS_READY, updatedOrder.getStatus());

    // Verify order was saved and waiters were notified
    verify(orderRepository).findById(101);
    verify(orderRepository).save(existingOrder);
    verify(waiterRepository).findAll();
    // Check that each waiter was notified
    verify(waiterRepository, times(2)).save(any(Waiter.class));
  }

  @Test
  void testMarkOrderAsReady_OrderNotFound_ReturnsNotFound() {
    // Arrange
    when(orderRepository.findById(999)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<Order> response = kitchenController.markOrderAsReady(999);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
    verify(orderRepository).findById(999);
    verify(orderRepository, never()).save(any(Order.class));
    verify(waiterRepository, never()).findAll();
  }

}
