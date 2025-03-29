package uk.ac.rhul.cs2810.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.rhul.cs2810.model.*;
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

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(waiterController).build();
  }

  @Test
  public void notifyWaiterNewOrder_WhenWaiterExists_ShouldReturnOrderDetails() {
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

    ResponseEntity<String> response = waiterController.notifyWaiterNewOrder(waiterId, order);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody().contains("Waiter John has been notified"));
    verify(waiterRepository, times(1)).save(waiter);
  }

  @Test
  public void notifyWaiterNewOrder_WhenWaiterDoesNotExist_ShouldReturnNotFound() {
    int waiterId = 99;
    Order order = new Order();
    order.setOrderNumber(101);

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.empty());

    ResponseEntity<String> response = waiterController.notifyWaiterNewOrder(waiterId, order);

    assertEquals(404, response.getStatusCodeValue());
    assertEquals("Waiter with ID 99 not found.", response.getBody());
    verify(waiterRepository, never()).save(any());
  }

  @Test
  public void markOrderAsDelivered_WhenOrderExists_ShouldMarkAsDelivered() {
    int orderId = 1;
    Order order = new Order();
    order.setOrderNumber(123);
    order.setStatus(OrderStatus.PENDING);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    ResponseEntity<String> response = waiterController.markOrderAsDelivered(orderId);

    assertEquals("Order 123 has been marked as Delivered.", response.getBody());
    assertEquals(OrderStatus.DELIVERED, order.getStatus());
    verify(orderRepository).save(order);
  }

  @Test
  public void markOrderAsDelivered_WhenAlreadyDelivered_ShouldReturnMessage() {
    int orderId = 1;
    Order order = new Order();
    order.setOrderNumber(123);
    order.setStatus(OrderStatus.DELIVERED);

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    ResponseEntity<String> response = waiterController.markOrderAsDelivered(orderId);

    assertEquals("Order 123 is already delivered.", response.getBody());
    verify(orderRepository, never()).save(any());
  }

  @Test
  public void markOrderAsDelivered_WhenOrderNotFound_ShouldReturnNotFound() {
    when(orderRepository.findById(1)).thenReturn(Optional.empty());

    ResponseEntity<String> response = waiterController.markOrderAsDelivered(1);

    assertEquals("Order with number 1 not found.", response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void markOrderAsDelivered_WhenStatusIsNull_ShouldReturnError() {
    Order order = new Order();
    order.setOrderNumber(123);
    order.setStatus(null);

    when(orderRepository.findById(1)).thenReturn(Optional.of(order));

    ResponseEntity<String> response = waiterController.markOrderAsDelivered(1);

    assertEquals("Order status is invalid.", response.getBody());
    verify(orderRepository, never()).save(any());
  }

  @Test
  public void assignTableToWaiter_WhenWaiterExists_ShouldAssignTable() {
    int waiterId = 1, tableNumber = 5;
    Waiter waiter = new Waiter();
    waiter.setId(waiterId);
    waiter.setName("John");

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));

    ResponseEntity<String> response = waiterController.assignTableToWaiter(waiterId, tableNumber);

    assertEquals(200, response.getStatusCodeValue());
    assertTrue(response.getBody().contains("assigned to waiter John"));
    verify(waiterRepository).save(waiter);
  }

  @Test
  public void assignTableToWaiter_WhenWaiterDoesNotExist_ShouldReturnNotFound() {
    when(waiterRepository.findById(99)).thenReturn(Optional.empty());

    ResponseEntity<String> response = waiterController.assignTableToWaiter(99, 5);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Waiter with ID 99 not found.", response.getBody());
  }

  @Test
  public void getUnpaidTables_WhenNoneFound_ShouldReturnPaidMessage() {
    int waiterId = 1;
    Waiter waiter = new Waiter();
    waiter.setId(waiterId);
    waiter.setName("John");
    waiter.setAssignedTables(Arrays.asList(10, 20));

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));
    when(orderRepository.findAll()).thenReturn(Collections.emptyList());

    ResponseEntity<String> response = waiterController.getUnpaidTables(waiterId);

    assertEquals("All delivered orders for waiter John have been paid.", response.getBody());
  }

  @Test
  public void getUnpaidTables_WhenWaiterDoesNotExist_ShouldReturnNotFound() {
    when(waiterRepository.findById(80)).thenReturn(Optional.empty());

    ResponseEntity<String> response = waiterController.getUnpaidTables(80);

    assertEquals("Waiter with ID 80 not found.", response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getReadyOrders_ShouldReturnReadyOrders() throws Exception {
    Order order1 = new Order(1, null, "2025-02-14 12:00", 15, OrderStatus.READY_FOR_DELIVERY);
    Order order2 = new Order(2, null, "2025-02-14 13:00", 10, OrderStatus.READY_FOR_DELIVERY);

    when(orderRepository.findByStatus(OrderStatus.READY_FOR_DELIVERY))
        .thenReturn(Arrays.asList(order1, order2));

    mockMvc.perform(get("/api/waiters/ready-orders")).andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].orderNumber").value(1))
        .andExpect(jsonPath("$[1].orderNumber").value(2));
  }
}
