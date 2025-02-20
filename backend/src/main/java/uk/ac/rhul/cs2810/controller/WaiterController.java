package uk.ac.rhul.cs2810.controller;

import java.util.List;
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
import uk.ac.rhul.cs2810.model.MenuItem;
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
        orderDetails.setLength(orderDetails.length() - 2); // Remove last comma
      } else {
        orderDetails.append("No items found.");
      }

      return ResponseEntity
          .ok("Waiter " + waiter.getName() + " has been notified.\n" + orderDetails);
    } else {
      return ResponseEntity.status(404).body("Waiter with ID " + waiterId + " not found.");
      }
  }

}
