package uk.ac.rhul.cs2810.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody; // Move this above RestController
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.Order; // Move this above Waiter
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

/**
 * Controller for handling waiter-related operations.
 */
@RestController
@RequestMapping("/api/waiters")
@CrossOrigin(origins = "http://localhost:5173") // Allow frontend access
public class WaiterController {

  @Autowired
  private WaiterRepository waiterRepository;

  private static final String CONTACT_NUMBER = "+44 123 456 789";

  /**
   * Calls a waiter for a specific table.
   *
   * @param tableNumber The table number requesting a waiter.
   * @return Response message indicating waiter status.
   */
  @PostMapping("/call-waiter")
  public ResponseEntity<String> callWaiter(@RequestParam int tableNumber) {
    Optional<Waiter> availableWaiter = waiterRepository.findAvailableWaiter();

    if (availableWaiter.isPresent()) {
      Waiter waiter = availableWaiter.get();
      waiter.setBusy(true);
      waiterRepository.save(waiter);

      return ResponseEntity.ok("A waiter is on the way to table " + tableNumber + "!");
    } else {
      return ResponseEntity
          .ok("No waiters available. Please call " + CONTACT_NUMBER + " for assistance.");
    }
  }

  /**
   * Notifies the waiter of a new order.
   *
   * @param waiterId The ID of the waiter to notify.
   * @param order The order that has been placed.
   * @return Response message indicating the notification status.
   */
  @PostMapping("/{waiterId}/notify-new-order")
  public ResponseEntity<String> notifyWaiterNewOrder(@PathVariable int waiterId,
      @RequestBody Order order) {
    Optional<Waiter> optionalWaiter = waiterRepository.findById(waiterId);

    if (optionalWaiter.isPresent()) {
      Waiter waiter = optionalWaiter.get();
      waiter.notifyNewOrder(order); // Notify the waiter of the new order
      waiterRepository.save(waiter); // Save the updated waiter state if needed

      return ResponseEntity.ok("Waiter " + waiter.getName() + " has been notified of new order: "
          + order.getOrderNumber());
    } else {
      return ResponseEntity.status(404).body("Waiter with ID " + waiterId + " not found.");
    }
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
