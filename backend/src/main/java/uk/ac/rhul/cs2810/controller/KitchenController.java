package uk.ac.rhul.cs2810.controller;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.Kitchen;
import uk.ac.rhul.cs2810.repository.KitchenRepository;

/**
 * Controller that deals with Kitchen records, which we use to notify waiters about a ready order.
 */
@RestController
@RequestMapping("/api/kitchen")
@CrossOrigin(origins = "http://localhost:5173")
public class KitchenController {
  @Autowired
  private KitchenRepository kitchenRepository;

  private String lastReadyOrderMessage = "No orders are ready at the moment.";
  private final List<String> readyOrderMessages = new CopyOnWriteArrayList<>();

  /**
   * Lists all kitchen records.
   */
  @GetMapping
  public ResponseEntity<List<Kitchen>> getAllKitchenRecords() {
    List<Kitchen> all = kitchenRepository.findAll();
    if (all.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(all);
  }

  /**
   * Creates a new "kitchen" record to notify waiters about an order.
   */
  @PostMapping
  public ResponseEntity<Kitchen> createKitchenRecord(@RequestParam int orderNumber,
      @RequestParam String message) {
    Kitchen kitchen = new Kitchen();
    kitchen.setOrderNumber(orderNumber);
    kitchen.setMessage(message);
    kitchenRepository.save(kitchen);

    return ResponseEntity.ok(kitchen);
  }

  /**
   * Deletes kitchen record after button has been pressed.
   *
   * @param id The ID of the kitchen record to be deleted.
   * @return A response indicating success or failure.
   *
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteKitchenRecord(@PathVariable Long id) {
    if (!kitchenRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    kitchenRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Marks an order as ready (updates only the message, not the status).
   */
  @PostMapping("/mark-ready/{orderId}")
  public ResponseEntity<String> markOrderAsReady(@PathVariable int orderId) {
    lastReadyOrderMessage = "Order #" + orderId + " is ready for delivery!";

    // Find the order and remove it from the database
    kitchenRepository.deleteById((long) orderId);
    return ResponseEntity.ok("Message updated successfully");
  }

  /**
   * Retrieves all ready orders.
   */
  @GetMapping("/ready-orders")
  public ResponseEntity<List<String>> getReadyOrders() {
    return ResponseEntity.ok(readyOrderMessages);
  }

  /**
   * Retrieves the last "ready order" message for display.
   */
  @GetMapping("/last-ready")
  public ResponseEntity<String> getLastReadyOrder() {
    return ResponseEntity.ok(lastReadyOrderMessage);
  }

  /**
   * Clears a ready order message after it's acknowledged (optional).
   */
  @DeleteMapping("/clear-ready/{orderId}")
  public ResponseEntity<Void> clearReadyOrder(@PathVariable int orderId) {
    readyOrderMessages.removeIf(msg -> msg.contains("Order #" + orderId));
    return ResponseEntity.noContent().build();
  }


}
