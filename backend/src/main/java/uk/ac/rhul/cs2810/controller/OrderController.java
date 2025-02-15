package uk.ac.rhul.cs2810.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.repository.OrderRepository;

/**
 * Controller for handling orders.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Places a new order.
     * 
     * @param order The order to be placed.
     * @return ResponseEntity with the saved order or an error message.
     */
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Order order) {
        try {
            Order savedOrder = orderRepository.save(order);
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("error", "Error placing order: " + e.getMessage()));
        }
    }

    /**
     * Retrieves an order by its ID.
     * 
     * @param orderId The ID of the order.
     * @return ResponseEntity with the order if found, otherwise a 404 error.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable int orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
      return order.map(ResponseEntity::ok)
          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Retrieves all orders.
     * 
     * @return ResponseEntity with a list of all orders.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
      List<Order> orders = orderRepository.findAll();
      return ResponseEntity.ok(orders);
    }
}
