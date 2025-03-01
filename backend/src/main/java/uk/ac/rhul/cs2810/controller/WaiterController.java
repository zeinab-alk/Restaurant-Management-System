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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.OrderStatus;
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

  private static final String CONTACT_NUMBER = "+44 123 456 789";

  @Autowired
  private WaiterRepository waiterRepository;

  @Autowired
  private OrderRepository orderRepository; // Inject order repo

  /**
   * Calls a waiter for a specific table.
   *
   * @param tableNumber The table number requesting a waiter.
   * @return ResponseEntity with the status message.
   */
  @PostMapping("/call-waiter")
  public ResponseEntity<String> callWaiter(@RequestParam int tableNumber) {
    Optional<Waiter> availableWaiter = waiterRepository.findAvailableWaiter();

    if (availableWaiter.isPresent()) {
      Waiter waiter = availableWaiter.get();
      waiter.setBusy(true);
      waiterRepository.save(waiter);
      return ResponseEntity.ok("A waiter is on the way to table " + tableNumber + "!");
    }
    return ResponseEntity
        .ok("No waiters available. Please call " + CONTACT_NUMBER + " for assistance.");
  }

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
      if (order.getStatus() == null) {
        return ResponseEntity.status(400).body("Order status is invalid.");
      }
      if (order.getStatus() == OrderStatus.DELIVERED) {
        return ResponseEntity.ok("Order " + order.getOrderNumber() + " is already delivered.");
      }

      order.setStatus(OrderStatus.DELIVERED);
      orderRepository.save(order); // Save the updated order

      return ResponseEntity
          .ok("Order " + order.getOrderNumber() + " has been marked as Delivered.");
    } else {
      return ResponseEntity.status(404).body("Order with ID " + orderId + " not found.");
    }
  }

}
