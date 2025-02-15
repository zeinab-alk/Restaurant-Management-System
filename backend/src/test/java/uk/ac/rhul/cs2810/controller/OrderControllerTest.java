package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.repository.OrderRepository;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filters for test
public class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private OrderRepository orderRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testPlaceOrder() throws Exception {
    Order order = new Order(1, null, "2025-02-14 12:00", 15, "Pending");

    when(orderRepository.save(any(Order.class))).thenReturn(order); // Ensure this returns an order

    mockMvc
        .perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(order)))
        .andDo(print()) // Debugging: prints the actual response JSON
        .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("Pending")); // Ensure
                                                                                      // status
                                                                                      // exists in
                                                                                      // response
  }


}


