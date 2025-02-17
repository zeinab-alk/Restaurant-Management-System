package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.repository.OrderRepository;

/**
 * Test for OrderController class.
 */
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for test
public class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private OrderRepository orderRepository;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * Test placing an order successfully.
   */
  @Test
  public void testPlaceOrderSuccess() throws Exception {
    // Arrange
    Order mockOrder = new Order(1, null, "2025-02-14 12:00", 15, "Pending");
    when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

    // Act & Assert
    mockMvc
        .perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mockOrder)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderNumber").value(1))
        .andExpect(jsonPath("$.status").value("Pending"));
  }

  /**
   * Test placing an order when an exception occurs (to cover the catch block).
   */
  @Test
  public void testPlaceOrderException() throws Exception {
    // Arrange: simulate an exception thrown by the repository
    when(orderRepository.save(any(Order.class))).thenThrow(new RuntimeException("DB error"));

    Order requestOrder = new Order(999, null, "2025-02-15 10:00", 10, "Pending");

    // Act & Assert: verify we get INTERNAL_SERVER_ERROR
    mockMvc
        .perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestOrder)))
        .andDo(print()).andExpect(status().isInternalServerError());
  }

  /**
   * Test getting an order by ID (found).
   */
  @Test
  public void testGetOrderFound() throws Exception {
    // Arrange
    Order mockOrder = new Order(1, null, "2025-02-14 12:00", 15, "Pending");
    when(orderRepository.findById(1)).thenReturn(Optional.of(mockOrder));

    // Act & Assert
    mockMvc
        .perform(get("/api/orders/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderNumber").value(1))
        .andExpect(jsonPath("$.status").value("Pending"));
  }

  /**
   * Test getting an order by ID (not found).
   */
  @Test
  public void testGetOrderNotFound() throws Exception {
    // Arrange
    when(orderRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    mockMvc
        .perform(get("/api/orders/999"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  /**
   * Test retrieving all orders.
   */
  @Test
  public void testGetAllOrders() throws Exception {
    // Arrange
    Order order1 = new Order(1, null, "2025-02-14 12:00", 15, "Pending");
    Order order2 = new Order(2, null, "2025-02-14 13:00", 10, "Completed");
    List<Order> mockOrders = Arrays.asList(order1, order2);

    when(orderRepository.findAll()).thenReturn(mockOrders);

    // Act & Assert
    mockMvc
        .perform(get("/api/orders"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].orderNumber").value(1))
        .andExpect(jsonPath("$[0].status").value("Pending"))
        .andExpect(jsonPath("$[1].orderNumber").value(2))
        .andExpect(jsonPath("$[1].status").value("Completed"));
  }
}




