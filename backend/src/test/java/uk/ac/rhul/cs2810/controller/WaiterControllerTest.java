package uk.ac.rhul.cs2810.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import java.util.Optional;
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
import uk.ac.rhul.cs2810.model.Menu;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.OrderStatusUpdateRequest;
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

  private MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(waiterController).build();
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
    order.setStatus(OrderStatus.PENDING); // Initial status is not "Delivered"

    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.markOrderAsDelivered(orderId);

    // Assert
    assertEquals("Order 123 has been marked as Delivered.", response.getBody());
    assertEquals(OrderStatus.DELIVERED, order.getStatus()); // Ensure the status was updated
    verify(orderRepository, times(1)).save(order); // Verify save was called
  }

  @Test
  public void markOrderAsDelivered_WhenOrderAlreadyDelivered_ShouldReturnAlreadyDeliveredMessage() {
    // Arrange
    int orderId = 1;
    Order order = new Order();
    order.setOrderNumber(123);
    order.setStatus(OrderStatus.DELIVERED); // Status is already "Delivered"

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
    assertEquals("Order with number 1 not found.", response.getBody());
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
    order.setStatus(null);

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


  @Test
  public void confirmOrder_WhenOrderIsEmpty_ShouldReturnBadRequest() {
    // Arrange
    int waiterId = 1;
    int orderId = 10;

    Waiter waiter = new Waiter();
    waiter.setId(waiterId);
    waiter.setName("John");

    Order order = new Order();
    order.setOrderNumber(303);
    order.setItems(null); // Order has no items

    Menu menu = new Menu();
    waiter.setMenu(menu);

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.confirmOrder(waiterId, orderId);

    // Assert
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Order is empty or invalid.", response.getBody());
  }

  @Test
  public void confirmOrder_WhenMenuItemIsNotFound_ShouldReturnBadRequest() {
    // Arrange
    int waiterId = 1;
    int orderId = 10;

    Waiter waiter = new Waiter();
    waiter.setId(waiterId);
    waiter.setName("John");

    MenuItem item1 = new MenuItem();
    item1.setName("Burger"); // Not found in menu

    Order order = new Order();
    order.setOrderNumber(505);
    order.setItems(Arrays.asList(item1));

    Menu menu = new Menu();
    menu.setMenuItems(Arrays.asList()); // Menu is empty

    waiter.setMenu(menu);

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.confirmOrder(waiterId, orderId);

    // Assert
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Item unavailable: Burger", response.getBody());
  }

  @Test
  public void confirmOrder_WhenOrderHasUnavailableItems_ShouldReturnBadRequest() {
    // Arrange
    int waiterId = 1;
    int orderId = 10;

    Waiter waiter = new Waiter();
    waiter.setId(waiterId);
    waiter.setName("John");

    MenuItem item1 = new MenuItem();
    item1.setName("Steak");
    item1.setAvailable(false); // Item is unavailable

    Order order = new Order();
    order.setOrderNumber(606);
    order.setItems(Arrays.asList(item1));

    Menu menu = new Menu();
    menu.setMenuItems(Arrays.asList(item1)); // Menu contains the item, but it's unavailable

    waiter.setMenu(menu);

    when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.confirmOrder(waiterId, orderId);

    // Assert
    assertEquals(400, response.getStatusCodeValue());
    assertEquals("Item unavailable: Steak", response.getBody());
  }

  @Test
  public void updateOrderStatus_WhenOrderExists_ShouldUpdateStatusSuccessfully() {
    // Arrange
    int orderId = 1;
    OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    Order order = new Order();
    order.setOrderNumber(orderId);
    order.setStatus(OrderStatus.PENDING);

    when(orderRepository.findByOrderNumber(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.updateOrderStatus(orderId, request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Order status updated to IN_PROGRESS", response.getBody());
    assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void updateOrderStatus_WhenOrderNotFoundByNumber_ButFoundById_ShouldUpdateStatus() {
    // Arrange
    int orderId = 1;
    OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    Order order = new Order();
    order.setOrderNumber(orderId);
    order.setStatus(OrderStatus.PENDING);

    when(orderRepository.findByOrderNumber(orderId)).thenReturn(Optional.empty());
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.updateOrderStatus(orderId, request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Order status updated to IN_PROGRESS", response.getBody());
    assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
    verify(orderRepository, times(1)).save(order);
  }

  @Test
  public void updateOrderStatus_WhenOrderNotFound_ShouldReturnNotFound() {
    // Arrange
    int orderId = 1;
    OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    when(orderRepository.findByOrderNumber(orderId)).thenReturn(Optional.empty());
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

    // Act
    ResponseEntity<String> response = waiterController.updateOrderStatus(orderId, request);

    // Assert
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("Order with number 1 not found.", response.getBody());
    verify(orderRepository, never()).save(any());
  }

  @Test
  public void updateOrderStatus_WhenStatusAlreadySet_ShouldReturnOkWithMessage() {
    // Arrange
    int orderId = 1;
    OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    Order order = new Order();
    order.setOrderNumber(orderId);
    order.setStatus(OrderStatus.IN_PROGRESS); // Same status as requested

    when(orderRepository.findByOrderNumber(orderId)).thenReturn(Optional.of(order));

    // Act
    ResponseEntity<String> response = waiterController.updateOrderStatus(orderId, request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Order status is already IN_PROGRESS", response.getBody());
    verify(orderRepository, never()).save(any());
  }

  @Test
  public void updateOrderStatus_WhenRequestIsNull_ShouldReturnBadRequest() {
    // Arrange
    int orderId = 1;

    // Act
    ResponseEntity<String> response = waiterController.updateOrderStatus(orderId, null);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Request body cannot be null", response.getBody());
    verify(orderRepository, never()).save(any());
  }

  @Test
  public void updateOrderStatus_WhenStatusIsNull_ShouldReturnBadRequest() {
    // Arrange
    int orderId = 1;
    OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
    // Status is null by default

    // Act
    ResponseEntity<String> response = waiterController.updateOrderStatus(orderId, request);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("Order status cannot be null", response.getBody());
    verify(orderRepository, never()).save(any());
  }

  @Test
  public void updateOrderStatus_WhenExceptionOccurs_ShouldReturnInternalServerError() {
    // Arrange
    int orderId = 1;
    OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    Order order = new Order();
    order.setOrderNumber(orderId);
    order.setStatus(OrderStatus.PENDING);

    when(orderRepository.findByOrderNumber(orderId)).thenReturn(Optional.of(order));
    when(orderRepository.save(any())).thenThrow(new RuntimeException("Database error"));

    // Act
    ResponseEntity<String> response = waiterController.updateOrderStatus(orderId, request);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertTrue(response.getBody().contains("Failed to update order status"));
  }


  @Test
  public void testGetReadyOrders_Success() throws Exception {
    Order readyOrder1 = new Order(1, null, "2025-03-01 12:00", 10, OrderStatus.READY_FOR_DELIVERY);
    Order readyOrder2 = new Order(2, null, "2025-03-01 12:30", 15, OrderStatus.READY_FOR_DELIVERY);

    when(orderRepository.findByStatus(OrderStatus.READY_FOR_DELIVERY))
        .thenReturn(Arrays.asList(readyOrder1, readyOrder2));

    mockMvc.perform(get("/api/waiters/ready-orders")).andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].orderNumber").value(1))
        .andExpect(jsonPath("$[1].orderNumber").value(2));
  }
  
  @Test
  public void assignTableToWaiter_WhenWaiterExists_ShouldAssignTable() {
      // Arrange
      int waiterId = 1;
      int tableNumber = 5;
      Waiter waiter = new Waiter();
      waiter.setId(waiterId);
      waiter.setName("John");

      when(waiterRepository.findById(waiterId)).thenReturn(Optional.of(waiter));

      // Act
      ResponseEntity<String> response = waiterController.assignTableToWaiter(waiterId, tableNumber);

      // Assert
      assertEquals(200, response.getStatusCodeValue());
      assertEquals("Table 5 assigned to waiter John", response.getBody());
      assertTrue(waiter.getAssignedTableNumbers().contains(tableNumber));
      verify(waiterRepository, times(1)).save(waiter);
  }

  @Test
  public void assignTableToWaiter_WhenWaiterDoesNotExist_ShouldReturnNotFound() {
      // Arrange
      int waiterId = 99;
      int tableNumber = 5;

      when(waiterRepository.findById(waiterId)).thenReturn(Optional.empty());

      // Act
      ResponseEntity<String> response = waiterController.assignTableToWaiter(waiterId, tableNumber);

      // Assert
      assertEquals(404, response.getStatusCodeValue());
      assertEquals("Waiter with ID 99 not found.", response.getBody());
      verify(waiterRepository, never()).save(any(Waiter.class));
  }




}
