package uk.ac.rhul.cs2810.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.Customer;
import uk.ac.rhul.cs2810.model.Menu;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.OrderStatus;
import uk.ac.rhul.cs2810.model.OrderStatusUpdateRequest;
import uk.ac.rhul.cs2810.model.TableAssignment;
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
  private OrderRepository orderRepository; // Inject order repo

  /**
   * Notifies a waiter of a new order.
   *
   * @param waiterId The ID of the waiter to notify.
   * @param order The new order details.
   * @return ResponseEntity with the status message.
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
   * Updates the status of an order.
   *
   * @param orderId The ID of the order to update.
   * @param request The request containing the new status.
   * @return ResponseEntity with success or error message.
   */
  @PostMapping("/orders/{orderId}/update-status")
  public ResponseEntity<String> updateOrderStatus(@PathVariable int orderId,
      @RequestBody OrderStatusUpdateRequest request) {

    System.out.println("Received request to update order " + orderId + " to status: "
        + (request != null ? request.getStatus() : "null"));

    // Check if request is null
    if (request == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Request body cannot be null");
    }

    // Check if status is null
    if (request.getStatus() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order status cannot be null");
    }

    try {
      // First try with findByOrderNumber
      Optional<Order> optionalOrder = orderRepository.findByOrderNumber(orderId);

      if (optionalOrder.isEmpty()) {
        // If not found, try with findById as fallback
        optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body("Order with number " + orderId + " not found.");
        }
      }

      Order order = optionalOrder.get();
      OrderStatus newStatus = request.getStatus();

      // Prevent setting the same status again
      if (order.getStatus() == newStatus) {
        return ResponseEntity.ok("Order status is already " + newStatus);
      }

      // Update the status directly to avoid potential validation issues
      order.setStatus(newStatus);
      orderRepository.save(order);
      return ResponseEntity.ok("Order status updated to " + newStatus);

    } catch (Exception e) {
      System.err.println("Error updating order status: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to update order status: " + e.getMessage());
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
    // Since orderNumber is the primary key in Order entity, we use the orderId directly
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
      return ResponseEntity.status(404).body("Order with number " + orderId + " not found.");
    }
  }

  /**
   * Confirms a customer order by checking if all items are available.
   *
   * @param waiterId The ID of the waiter handling the order.
   * @param orderId The ID of the order to confirm.
   * @return ResponseEntity with confirmation message.
   */
  @PostMapping("/{waiterId}/confirm-order/{orderId}")
  public ResponseEntity<String> confirmOrder(@PathVariable int waiterId,
      @PathVariable int orderId) {
    Optional<Waiter> optionalWaiter = waiterRepository.findById(waiterId);
    Optional<Order> optionalOrder = orderRepository.findById(orderId);

    if (optionalWaiter.isEmpty() || optionalOrder.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Waiter or order not found.");
    }

    Waiter waiter = optionalWaiter.get();
    Order order = optionalOrder.get();
    Menu menu = waiter.getMenu();

    if (menu == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("No menu associated with this waiter.");
    }

    List<MenuItem> orderedItems = order.getMenuItems();
    if (orderedItems == null || orderedItems.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order is empty or invalid.");
    }

    for (MenuItem item : orderedItems) {
      MenuItem menuItem = menu.getItemByName(item.getName());
      if (menuItem == null || !menuItem.isAvailable()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Item unavailable: " + item.getName());
      }
    }

    return ResponseEntity.ok("Order " + order.getOrderNumber() + " confirmed.");
  }

  /**
   * Retrieves a list of orders that are ready for delivery.
   *
   * @return ResponseEntity containing a list of orders with status READY_FOR_DELIVERY. Returns a
   *         404 status if no ready orders are found.
   */

  @GetMapping("/ready-orders")
  public ResponseEntity<List<Order>> getReadyOrders() {
    List<Order> readyOrders = orderRepository.findByStatus(OrderStatus.READY_FOR_DELIVERY);
    if (readyOrders.isEmpty()) {
      return ResponseEntity.status(404).body(null);
    }
    return ResponseEntity.ok(readyOrders);
  }



  /**
   * Assigns a table to a waiter.
   *
   * @param waiterId The ID of the waiter.
   * @param tableNumber The table number to assign.
   * @return ResponseEntity with success or error message.
   */
  @PostMapping("/{waiterId}/assign-table/{tableNumber}")

  public ResponseEntity<String> assignTableToWaiter(@PathVariable int waiterId,
      @PathVariable int tableNumber) {
    Optional<Waiter> optionalTargetWaiter = waiterRepository.findById(waiterId);

    if (optionalTargetWaiter.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Waiter with ID " + waiterId + " not found.");
    }

    // Check if the table is already assigned to another waiter
    List<Waiter> allWaiters = new ArrayList<>();
    waiterRepository.findAll().forEach(allWaiters::add);

    for (Waiter otherWaiter : allWaiters) {
      // Check if the table is already assigned to another waiter
      if (otherWaiter.getId() != waiterId // Don't check the target waiter themselves
          // Use getAssignedTableNumbers() for the check
          && otherWaiter.getAssignedTableNumbers().contains(tableNumber)) {
        // Table already assigned to another waiter
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            "Table " + tableNumber + " is already assigned to waiter " + otherWaiter.getId() + ".");
      }
    }

    // Assign the table if not already assigned to the target waiter
    Waiter targetWaiter = optionalTargetWaiter.get();
    // Check using getAssignedTableNumbers()
    if (targetWaiter.getAssignedTableNumbers().contains(tableNumber)) {
      // Table already assigned to this waiter
      return ResponseEntity.ok("Table " + tableNumber + " is already assigned to this waiter.");
    }

    // Use the assignTable method in the Waiter entity
    targetWaiter.assignTable(tableNumber);
    waiterRepository.save(targetWaiter);

    // Use waiter's name in the response message to match test expectation
    return ResponseEntity
        .ok("Table " + tableNumber + " assigned to waiter " + targetWaiter.getName());
  }

  /**
   * Retrieves all waiters along with their assigned tables.
   *
   * @return List of waiters with assigned tables.
   */
  @GetMapping
  public ResponseEntity<List<Waiter>> getAllWaiters() {
    List<Waiter> waiters = (List<Waiter>) waiterRepository.findAll();
    return ResponseEntity.ok(waiters);
  }


  /**
   * Retrieves a list of tables that have received their orders but have not yet paid.
   *
   * @param waiterId The ID of the waiter responsible for the tables.
   * @return ResponseEntity containing a list of unpaid tables or a message indicating all orders
   *         have been paid.
   */
  @GetMapping("/{waiterId}/unpaid-tables")

  public ResponseEntity<String> getUnpaidTables(@PathVariable int waiterId) {
    Optional<Waiter> optionalWaiter = waiterRepository.findById(waiterId);

    if (optionalWaiter.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Waiter with ID " + waiterId + " not found.");
    }

    Waiter waiter = optionalWaiter.get();
    List<Order> allOrders = orderRepository.findAll();
    List<Integer> unpaidTables = new ArrayList<>();

    if (waiter.getAssignedTables().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("No tables assigned to waiter " + waiter.getName() + ".");
    }

    // Check every orders to find unpaid tables
    for (Order order : allOrders) {
      Customer customer = order.getCustomer();
      if (customer != null) {
        int tableNumber = (int) customer.getId();
        if (waiter.getAssignedTables().contains(tableNumber)
            && order.getStatus() == OrderStatus.DELIVERED) {
          unpaidTables.add(tableNumber);
        }
      }
    }



    // Message will be outputtted based on if tables have or have not paid
    if (unpaidTables.isEmpty()) {
      return ResponseEntity
          .ok("All delivered orders for waiter " + waiter.getName() + " have been paid.");
    } else {
      return ResponseEntity.ok("Waiter " + waiter.getName()
          + " needs to check payment for the following tables: " + unpaidTables);
    }
  }

  // === NEW CRUD Endpoints for Waiters ===

  /**
   * Creates a new waiter. Requires ROLE_MANAGER (as configured in WebSecurityConfig).
   *
   * @param waiter Contains the details (e.g., name) for the new waiter.
   * @return ResponseEntity containing the created waiter and HTTP status 201, or an error.
   */
  @PostMapping
  public ResponseEntity<?> createWaiter(@RequestBody Waiter waiter) {
    try {
      if (waiter.getName() == null || waiter.getName().trim().isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Waiter name cannot be empty.");
      }
      // Ensure assignedTables is initialized if not provided
      if (waiter.getAssignedTables() == null) {
        waiter.setAssignedTables(new ArrayList<>());
      }
      // ID should be generated by the database, so we don't set it here.
      Waiter savedWaiter = waiterRepository.save(waiter);
      return ResponseEntity.status(HttpStatus.CREATED).body(savedWaiter);
    } catch (Exception e) {
      // Log the exception details
      System.err.println("Error creating waiter: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error creating waiter: " + e.getMessage());
    }
  }

  /**
   * Updates an existing waiter's details (e.g., name). Requires ROLE_MANAGER (as configured in
   * WebSecurityConfig).
   *
   * @param id The ID of the waiter to update.
   * @param waiterDetails Contains the updated details (e.g., name).
   * @return ResponseEntity containing the updated waiter or an error status.
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> updateWaiter(@PathVariable int id, @RequestBody Waiter waiterDetails) {
    return waiterRepository.findById(id).map(waiter -> {
      try {
        if (waiterDetails.getName() != null && !waiterDetails.getName().trim().isEmpty()) {
          waiter.setName(waiterDetails.getName());
        }
        // Add other fields to update if necessary
        Waiter updatedWaiter = waiterRepository.save(waiter);
        return ResponseEntity.ok(updatedWaiter);
      } catch (Exception e) {
        System.err.println("Error updating waiter with ID " + id + ": " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error updating waiter: " + e.getMessage());
      }
    }).orElse(
        ResponseEntity.status(HttpStatus.NOT_FOUND).body("Waiter with ID " + id + " not found."));
  }

  /**
   * Deletes a waiter. Requires ROLE_MANAGER (as configured in WebSecurityConfig).
   *
   * @param id The ID of the waiter to delete.
   * @return ResponseEntity with no content (204) or an error status.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteWaiter(@PathVariable int id) {
    return waiterRepository.findById(id).map(waiter -> {
      try {
        waiterRepository.delete(waiter);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
      } catch (Exception e) {
        System.err.println("Error deleting waiter with ID " + id + ": " + e.getMessage());
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error deleting waiter: " + e.getMessage());
      }
    }).orElse(
        ResponseEntity.status(HttpStatus.NOT_FOUND).body("Waiter with ID " + id + " not found."));
  }

  /**
   * Removes a specific table assignment from a waiter.
   *
   * @param waiterId The ID of the waiter.
   * @param tableNumber The table number to remove.
   * @return ResponseEntity indicating success or failure.
   */
  @DeleteMapping("/{waiterId}/assign-table/{tableNumber}")
  public ResponseEntity<Void> removeTableAssignment(@PathVariable int waiterId,
      @PathVariable int tableNumber) {
    Optional<Waiter> optionalWaiter = waiterRepository.findById(waiterId);

    if (optionalWaiter.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Waiter waiter = optionalWaiter.get();
    // Get the actual list of TableAssignment objects
    List<TableAssignment> assignments = waiter.getAssignments();

    // Find the specific TableAssignment object to remove
    Optional<TableAssignment> assignmentToRemove =
        assignments.stream().filter(ta -> ta.getTableNumber() == tableNumber).findFirst();

    if (assignmentToRemove.isPresent()) {
      // Remove the object directly from the list
      assignments.remove(assignmentToRemove.get());
      waiterRepository.save(waiter); // Save the waiter to trigger cascade/orphan removal
      return ResponseEntity.noContent().build(); // Success, no content to return
    } else {
      // Table wasn't assigned to this waiter in the first place
      // Or potentially return OK/noContent as it's idempotent
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Cancels an order if it is able to.
   *
   * @param orderId The ID of the order to cancel.
   * @return ResponseEntity with success or failure message.
   */
  @PostMapping("/orders/{orderId}/cancel")
  public ResponseEntity<String> cancelOrder(@PathVariable int orderId) {
    Optional<Order> optionalOrder = orderRepository.findById(orderId);

    if (optionalOrder.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Order with ID " + orderId + " not found.");
    }

    Order order = optionalOrder.get();

    if (order.getStatus() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order status is invalid.");
    }

    if (order.getStatus() == OrderStatus.CANCELLED) {
      return ResponseEntity.ok("Order " + order.getOrderNumber() + " has already been cancelled.");
    }

    if (order.getStatus() == OrderStatus.DELIVERED) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot cancel order "
          + order.getOrderNumber() + " has already been delivered so cant be cancelled.");
    }

    order.setStatus(OrderStatus.CANCELLED);
    orderRepository.save(order);

    return ResponseEntity
        .ok("Order " + order.getOrderNumber() + " has been successfully cancelled.");
  }


}


