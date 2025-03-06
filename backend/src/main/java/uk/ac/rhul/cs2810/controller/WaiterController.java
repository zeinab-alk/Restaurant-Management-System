package uk.ac.rhul.cs2810.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.OrderRepository;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

/**
 * Controller for handling waiter-related operations.
 */
@RestController
@RequestMapping("/api/waiters")
@CrossOrigin(origins = "http://localhost:5173")
public class WaiterController {

  @Autowired
  private WaiterRepository waiterRepository;

  @Autowired
  private OrderRepository orderRepository;

  /**
   * Notifies the waiter of a new order.
   *
   * @param waiterId The ID of the waiter to notify.
   * @param order The order that has been placed.
   * @return ResponseEntity with the notification status.
   */
  @PostMapping("/{waiterId}/notify-new-order")
  public ResponseEntity<String> notifyWaiterNewOrder(@PathVariable int waiterId,
      @RequestBody Order order) {
    Optional<Waiter> optionalWaiter = waiterRepository.findById(waiterId);

    if (optionalWaiter.isPresent()) {
      Waiter waiter = optionalWaiter.get();
      waiter.notifyNewOrder(order);
      waiterRepository.save(waiter);

      // Fetch ordered items
      List<MenuItem> orderedItems = order.getMenuItems();
      StringBuilder orderDetails =
          new StringBuilder("Order #" + order.getOrderNumber() + " contains: ");

      for (MenuItem item : orderedItems) {
        orderDetails.append(item.getName()).append(", ");
      }

      if (!orderedItems.isEmpty()) {
        orderDetails.setLength(orderDetails.length() - 2);
      } else {
        orderDetails.append("No items found.");
      }

      return ResponseEntity
          .ok("Waiter " + waiter.getName() + " has been notified.\n" + orderDetails);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Waiter with ID " + waiterId + " not found.");
  }

  /**
   * Marks an order as delivered.
   *
   * @param orderId The ID of the order to update.
   * @return Response message indicating success or failure.
   */
  @PostMapping("/orders/{orderId}/mark-delivered")
  public ResponseEntity<String> markOrderAsDelivered(@PathVariable int orderId) {
    Optional<Order> optionalOrder = orderRepository.findById(orderId);

    if (optionalOrder.isPresent()) {
      Order order = optionalOrder.get();
      if (order.getStatus() == null || order.getStatus().trim().isEmpty()) {
        return ResponseEntity.status(400).body("Order status is invalid.");
      }
      if ("Delivered".equalsIgnoreCase(order.getStatus())) {
        return ResponseEntity.ok("Order " + order.getOrderNumber() + " is already delivered.");
      }

      order.setStatus("Delivered");
      orderRepository.save(order); // Save the updated order

      return ResponseEntity
          .ok("Order " + order.getOrderNumber() + " has been marked as Delivered.");
    } else {
      return ResponseEntity.status(404).body("Order with ID " + orderId + " not found.");
    }
  }
}

