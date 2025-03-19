package uk.ac.rhul.cs2810.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.OrderRepository;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

/**
 * Controller for handling kitchen-related operations.
 */
@RestController
@RequestMapping("/api/kitchen")
@CrossOrigin(origins = "http://localhost:5173")
public class KitchenController {

  private final OrderRepository orderRepository;
  private final WaiterRepository waiterRepository;

  public KitchenController(OrderRepository orderRepository, WaiterRepository waiterRepository) {
    this.orderRepository = orderRepository;
    this.waiterRepository = waiterRepository;
  }

  /**
   * Retrieves orders that are currently in "Preparing" status.
   *
   * @return List of orders being prepared.
   */
  @GetMapping("/preparing")
  public ResponseEntity<List<Order>> getPreparingOrders() {
    List<Order> preparingOrders = orderRepository.findByStatus(Order.STATUS_PREPARING);
    return ResponseEntity.ok(preparingOrders);
  }

  /**
   * Marks an order as "Ready" so that waiters know it's ready for delivery.
   *
   * @param orderId The ID of the order to update.
   * @return Updated order object.
   */
  @PostMapping("/{orderId}/ready")
  public ResponseEntity<Order> markOrderAsReady(@PathVariable int orderId) {
    Optional<Order> orderOpt = orderRepository.findById(orderId);
    if (!orderOpt.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    Order order = orderOpt.get();
    order.setStatus(Order.STATUS_READY); // Mark as Ready
    orderRepository.save(order);
    // Notify waiters
    notifyWaiters(order);
    return ResponseEntity.ok(order);
  }

  private void notifyWaiters(Order order) {
    // Logic to find available waiters and notify them
    List<Waiter> availableWaiters = new ArrayList<>();
    waiterRepository.findAll().forEach(availableWaiters::add);
    for (Waiter waiter : availableWaiters) {
      waiter.notifyNewOrder(order);
      waiterRepository.save(waiter);
    }
  }

}
