package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete; // if you have a delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// We only load beans relevant to the controller layer
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.OrderStatusUpdateRequest;
import uk.ac.rhul.cs2810.model.PayRequest;
import uk.ac.rhul.cs2810.repository.OrderRepository;
import uk.ac.rhul.cs2810.service.OrderService;

/**
 * Tests the OrderController endpoints using a slice-based approach (@WebMvcTest).
 * We mock dependencies so we only test the controller logic and HTTP behavior.
 */
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters if you have Spring Security
public class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  // Mock the dependencies that OrderController needs
  @MockBean
  private OrderRepository orderRepository;

  @MockBean
  private OrderService orderService;

  @Autowired
  private ObjectMapper objectMapper;

  // --------------------------------------------------------------------------
  // Example endpoint: POST /api/orders (placeOrder)
  // --------------------------------------------------------------------------
  @Test
  void testPlaceOrder_Success() throws Exception {
    // Suppose this is the "order" we want to place
    Order mockOrder = new Order();
    mockOrder.setOrderNumber(123);
    mockOrder.setStatus(OrderStatus.PENDING);

    // If the controller calls orderRepository.save(...) -> returns mockOrder
    when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

    // Convert mockOrder to JSON for the request body
    String orderJson = objectMapper.writeValueAsString(mockOrder);

    // Perform POST /api/orders
    mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(orderJson))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderNumber").value(123))
      .andExpect(jsonPath("$.status").value(OrderStatus.PENDING.toString()));
  }

  @Test
  void testPlaceOrder_Exception() throws Exception {
    // If the repository throws an exception, we get HTTP 500
    when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("DB error"));

    Order requestOrder = new Order();
    requestOrder.setOrderNumber(999);

    String json = objectMapper.writeValueAsString(requestOrder);

    mockMvc.perform(post("/api/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
      .andDo(print())
      .andExpect(status().isInternalServerError());
  }

  // --------------------------------------------------------------------------
  // GET /api/orders/{orderId}
  // --------------------------------------------------------------------------
  @Test
  void testGetOrderFound() throws Exception {
    Order mockOrder = new Order();
    mockOrder.setOrderNumber(10);

    when(orderRepository.findById(10)).thenReturn(Optional.of(mockOrder));

    mockMvc.perform(get("/api/orders/10"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderNumber").value(10));
  }

  @Test
  void testGetOrder_NotFound() throws Exception {
    when(orderRepository.findById(999)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/orders/999"))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  // --------------------------------------------------------------------------
  // GET /api/orders (getAllOrders)
  // --------------------------------------------------------------------------
  @Test
  void testGetAllOrders() throws Exception {
    Order o1 = new Order();
    o1.setOrderNumber(1);
    Order o2 = new Order();
    o2.setOrderNumber(2);

    List<Order> mockOrders = Arrays.asList(o1, o2);
    when(orderRepository.findAllWithMenuItems()).thenReturn(mockOrders);

    mockMvc.perform(get("/api/orders"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].orderNumber").value(1))
      .andExpect(jsonPath("$[1].orderNumber").value(2));
  }

  // Example error path for getAllOrders if your code can throw an exception
  @Test
  void testGetAllOrders_Exception() throws Exception {
    when(orderRepository.findAllWithMenuItems()).thenThrow(new RuntimeException("DB error"));

    mockMvc.perform(get("/api/orders"))
      .andDo(print())
      .andExpect(status().isInternalServerError());
  }

  // --------------------------------------------------------------------------
  // GET /api/orders/latest
  // --------------------------------------------------------------------------
  @Test
  void testGetLatestOrder_Success() throws Exception {
    Order latest = new Order();
    latest.setOrderNumber(99);
    when(orderRepository.findLatestOrderWithMenuItems()).thenReturn(Optional.of(latest));
    when(orderRepository.findTopByOrderByOrderTimeDesc()).thenReturn(Optional.of(latest));

    mockMvc.perform(get("/api/orders/latest"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderNumber").value(99));
  }

  @Test
  void testGetLatestOrder_NoContent() throws Exception {
    when(orderRepository.findLatestOrderWithMenuItems()).thenReturn(Optional.empty());
    when(orderRepository.findTopByOrderByOrderTimeDesc()).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/orders/latest"))
      .andDo(print())
      .andExpect(status().isNoContent());
  }

  // --------------------------------------------------------------------------
  // POST /api/orders/{orderId}/addItem?menuItemId=...
  // --------------------------------------------------------------------------
  @Test
  void testAddItemToOrder() throws Exception {
    // If orderService.addItemToOrder(10, 555) returns an updated order
    Order updated = new Order();
    updated.setOrderNumber(10);

    when(orderService.addItemToOrder(10, 555)).thenReturn(updated);

    mockMvc.perform(post("/api/orders/10/addItem")
        .param("menuItemId", "555"))
      .andDo(print())
      .andExpect(status().isOk());
  }

  // --------------------------------------------------------------------------
  // PUT /api/orders/{id}/status (updateOrderStatus)
  // --------------------------------------------------------------------------
  @Test
  void testUpdateOrderStatus_Success() throws Exception {
    Order existing = new Order();
    existing.setOrderNumber(11);
    existing.setStatus(OrderStatus.PENDING);

    when(orderRepository.findById(11)).thenReturn(Optional.of(existing));

    OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    mockMvc.perform(put("/api/orders/11/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  void testUpdateOrderStatus_NotFound() throws Exception {
    when(orderRepository.findById(999)).thenReturn(Optional.empty());

    OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
    request.setStatus(OrderStatus.IN_PROGRESS);

    mockMvc.perform(put("/api/orders/999/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  // --------------------------------------------------------------------------
  // GET /api/orders/number/{orderNumber}
  // --------------------------------------------------------------------------
  @Test
  void testGetOrderByOrderNumber_Success() throws Exception {
    Order o = new Order();
    o.setOrderNumber(123);

    when(orderRepository.findByOrderNumberWithMenuItems(123))
        .thenReturn(Optional.of(o));

    mockMvc.perform(get("/api/orders/number/123"))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderNumber").value(123));
  }

  @Test
  void testGetOrderByOrderNumber_NotFound() throws Exception {
    when(orderRepository.findByOrderNumberWithMenuItems(999))
        .thenReturn(Optional.empty());

    mockMvc.perform(get("/api/orders/number/999"))
      .andDo(print())
      .andExpect(status().isNotFound());
  }

  // --------------------------------------------------------------------------
  // POST /api/orders/pay
  // --------------------------------------------------------------------------
  @Test
  void testPayForOrder_Success() throws Exception {
    PayRequest payRequest = new PayRequest(1, "1234567812345678", "04/25", "Test Name", "123");
    Order existingOrder = new Order();
    existingOrder.setOrderNumber(1);

    when(orderRepository.findByOrderNumber(1))
        .thenReturn(Optional.of(existingOrder));

    mockMvc.perform(post("/api/orders/pay")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payRequest)))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(content().string("Payment successful. Order marked as PAID."));
  }

  @Test
  void testPayForOrder_OrderNotFound() throws Exception {
    PayRequest payRequest = new PayRequest(999, "1234567812345678", "04/25", "Test Name", "123");

    when(orderRepository.findByOrderNumber(999)).thenReturn(Optional.empty());

    mockMvc.perform(post("/api/orders/pay")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payRequest)))
      .andDo(print())
      .andExpect(status().isNotFound())
      .andExpect(content().string("Order not found."));
  }

  @Test
  void testPayForOrder_InvalidCardNumber() throws Exception {
    PayRequest payRequest = new PayRequest(1, "123", "04/25", "Test Name", "123");
    Order existingOrder = new Order();
    existingOrder.setOrderNumber(1);

    when(orderRepository.findByOrderNumber(1)).thenReturn(Optional.of(existingOrder));

    mockMvc.perform(post("/api/orders/pay")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payRequest)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string("Invalid card number."));
  }

  @Test
  void testPayForOrder_InvalidExpiryDate() throws Exception {
    PayRequest payRequest = new PayRequest(1, "1234567812345678", "14/30", "Test Name", "123");
    Order existingOrder = new Order();
    existingOrder.setOrderNumber(1);

    when(orderRepository.findByOrderNumber(1)).thenReturn(Optional.of(existingOrder));

    mockMvc.perform(post("/api/orders/pay")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payRequest)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string("Invalid expiry date format."));
  }

  @Test
  void testPayForOrder_MissingCardholderName() throws Exception {
    PayRequest payRequest = new PayRequest(1, "1234567812345678", "04/25", "  ", "123");
    Order existingOrder = new Order();
    existingOrder.setOrderNumber(1);

    when(orderRepository.findByOrderNumber(1)).thenReturn(Optional.of(existingOrder));

    mockMvc.perform(post("/api/orders/pay")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payRequest)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string("Cardholder name required."));
  }

  @Test
  void testPayForOrder_InvalidCvv() throws Exception {
    PayRequest payRequest = new PayRequest(1, "1234567812345678", "04/25", "Test Name", "12");
    Order existingOrder = new Order();
    existingOrder.setOrderNumber(1);

    when(orderRepository.findByOrderNumber(1)).thenReturn(Optional.of(existingOrder));

    mockMvc.perform(post("/api/orders/pay")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(payRequest)))
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(content().string("Invalid CVV."));
  }
}
