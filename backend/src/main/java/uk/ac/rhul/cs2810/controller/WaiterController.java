package uk.ac.rhul.cs2810.controller;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}

