package uk.ac.rhul.cs2810.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.OrderStatusUpdateRequest;
import uk.ac.rhul.cs2810.repository.OrderRepository;

/**
 * Controller for handling orders.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

  private final OrderRepository orderRepository;
  private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

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
  public ResponseEntity<Order> placeOrder(@RequestBody Order order) {
    try {
      Order savedOrder = orderRepository.save(order);
      return ResponseEntity.ok(savedOrder);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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
    try {
      long startTime = System.currentTimeMillis();
      logger.info("Fetching all orders...");

      List<Order> orders = orderRepository.findAllWithMenuItems();

      long endTime = System.currentTimeMillis();
      logger.info("Orders fetched successfully in {} ms. Found {} orders.", (endTime - startTime),
          orders.size());

      return ResponseEntity.ok(orders);
    } catch (Exception e) {
      logger.error("Error fetching orders: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Retrieves the most recent order.
   *
   * @return ResponseEntity containing the latest Order if found, or a 204 No Content response
   */
  @GetMapping("/latest")
  public ResponseEntity<Order> getLatestOrder() {
    try {
      long startTime = System.currentTimeMillis();
      logger.info("Fetching latest order...");

      // First try with optimized query
      Optional<Order> latestOrder = orderRepository.findLatestOrderWithMenuItems();

      // Fallback to simpler query if the optimized one doesn't work
      if (latestOrder.isEmpty()) {
        latestOrder = orderRepository.findTopByOrderByOrderTimeDesc();
      }

      if (latestOrder.isPresent()) {
        Order order = latestOrder.get();
        logger.info("Latest Order Found: {}", order.getOrderNumber());

        // Only log detailed info at debug level to reduce console spam
        if (logger.isDebugEnabled()) {
          logger.debug("Menu Items in Order: {}", order.getMenuItems());
        }

        long endTime = System.currentTimeMillis();
        logger.info("Latest order fetched in {} ms", (endTime - startTime));

        // Add cache control headers to reduce load on frequent polling
        return ResponseEntity.ok().header("Cache-Control", "max-age=1") // Cache for 1 second
            .body(order);
      } else {
        // Return an empty response with 204 No Content instead of 404
        logger.info("No orders found in the system");
        return ResponseEntity.noContent().build();
      }
    } catch (Exception e) {
      logger.error("Error fetching latest order: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Updates the status of an existing order.
   *
   * @param id The ID of the order to update.
   * @param request The request body containing the new order status.
   * @return ResponseEntity with the updated order.
   */
  @PutMapping("/{id}/status")
  public ResponseEntity<Order> updateOrderStatus(@PathVariable int id,
      @RequestBody OrderStatusUpdateRequest request) {
    Optional<Order> optionalOrder = orderRepository.findById(id);
    if (optionalOrder.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Order order = optionalOrder.get();
    order.setStatus(request.getStatus());
    // @PreUpdate in Order entity will automatically update orderUpdatedTime
    if (request.getStatus() == OrderStatus.IN_PROGRESS) {
      order.setEstimatedCompletionTime(LocalDateTime.now().plusMinutes(order.getWaitingTime()));
    }
    orderRepository.save(order);

    return ResponseEntity.ok(order);
  }
}


