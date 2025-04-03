package uk.ac.rhul.cs2810.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.OrderStatusUpdateRequest;
import uk.ac.rhul.cs2810.model.PayRequest;
import uk.ac.rhul.cs2810.repository.OrderRepository;
import uk.ac.rhul.cs2810.service.OrderService;

/**
 * Combined REST Controller for order operations.
 * Exposes endpoints for placing orders, retrieving orders, modifying orders (e.g. adding items),
 * updating order status, and processing payments.
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

  private final OrderRepository orderRepository;
  private final OrderService orderService;
  private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

  /**
   * Constructs the OrderController with required dependencies.
   *
   * @param orderRepository the repository for Order entities.
   * @param orderService the service handling business logic for orders.
   */
  @Autowired
  public OrderController(OrderRepository orderRepository, OrderService orderService) {
    this.orderRepository = orderRepository;
    this.orderService = orderService;
  }

  /**
   * Adds a menu item to an existing order.
   *
   * @param orderId the ID of the order to update.
   * @param menuItemId the ID of the menu item to add.
   * @return the updated order.
   */
  @PostMapping("/{orderId}/addItem")
  public Order addItemToOrder(@PathVariable Integer orderId,
                              @RequestParam Integer menuItemId) {
    return orderService.addItemToOrder(orderId, menuItemId);
  }

  /**
   * Places a new order.
   *
   * @param order the order to be placed.
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
   * @param orderId the ID of the order.
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
   * @return ResponseEntity containing the latest order if found, or a 204 No Content response.
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

        if (logger.isDebugEnabled()) {
          logger.debug("Menu Items in Order: {}", order.getMenuItems());
        }

        long endTime = System.currentTimeMillis();
        logger.info("Latest order fetched in {} ms", (endTime - startTime));

        return ResponseEntity.ok().header("Cache-Control", "max-age=1").body(order);
      } else {
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
   * @param id the ID of the order to update.
   * @param request the request body containing the new order status.
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
    if (request.getStatus() == OrderStatus.IN_PROGRESS) {
      order.setEstimatedCompletionTime(LocalDateTime.now().plusMinutes(order.getWaitingTime()));
    }
    orderRepository.save(order);

    return ResponseEntity.ok(order);
  }

  /**
   * Retrieves an order by its order number including its menu items.
   *
   * @param orderNumber the order number to search for.
   * @return ResponseEntity containing the order if found, or a 404 error.
   */
  @GetMapping("/number/{orderNumber}")
  public ResponseEntity<Order> getOrderByOrderNumber(@PathVariable int orderNumber) {
    Optional<Order> optionalOrder = orderRepository.findByOrderNumberWithMenuItems(orderNumber);
    return optionalOrder.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }

  /**
   * Processes a payment request for a given order.
   * Validates card details and updates the order status to PAID if valid.
   *
   * @param request the PayRequest containing order number and card details.
   * @return ResponseEntity with a success or error message.
   */
  @PostMapping("/pay")
  public ResponseEntity<String> payForOrder(@RequestBody PayRequest request) {
    Optional<Order> optionalOrder = orderRepository.findByOrderNumber(request.getOrderNumber());
    if (optionalOrder.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
    }

    if (!request.getCardNumber().matches("\\d{16}")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid card number.");
    }
    if (!request.getExpiryDate().matches("(0[1-9]|1[0-2])/\\d{2}")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid expiry date format.");
    }
    if (request.getCardHolderName() == null || request.getCardHolderName().isBlank()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cardholder name required.");
    }
    if (!request.getCvv().matches("\\d{3}")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid CVV.");
    }

    Order order = optionalOrder.get();
    order.setStatus(OrderStatus.PAID);
    orderRepository.save(order);

    return ResponseEntity.ok("Payment successful. Order marked as PAID.");
  }
}