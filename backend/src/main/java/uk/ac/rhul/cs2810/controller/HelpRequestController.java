package uk.ac.rhul.cs2810.controller;

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
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.HelpRequest;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.HelpRequestRepository;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

/**
 * Controller for handling Help Requests.
 */
@RestController
@RequestMapping("/api/help-requests")
@CrossOrigin(origins = "http://localhost:5173")
public class HelpRequestController {

  private static final Logger logger = LoggerFactory.getLogger(HelpRequestController.class);

  @Autowired
  private HelpRequestRepository helpRequestRepository;

  @Autowired
  private WaiterRepository waiterRepository;

  /**
   * Creates a new HelpRequest.
   */
  @PostMapping
  public ResponseEntity<?> createHelpRequest(@RequestBody HelpRequest request) {
    final long startTime = System.currentTimeMillis();
    logger.info("Received HelpRequest: Table {}", request.getTableNumber());

    if (request.getTableNumber() <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid table number.");
    }

    // Assign a random available waiter if none provided
    List<Waiter> availableWaiters = (List<Waiter>) waiterRepository.findAll();
    if (availableWaiters.isEmpty()) {
      logger.error("ERROR: No waiters available.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available waiters.");
    }

    // Assign the first available waiter
    request.setWaiter(availableWaiters.get(0));
    request.setResolved(false); // Ensure new requests are unresolved

    // Save to DB
    helpRequestRepository.save(request);
    long endTime = System.currentTimeMillis();
    logger.info("✅ Help request successfully saved for Table {} in {} ms", 
        request.getTableNumber(), (endTime - startTime));

    return new ResponseEntity<>(request, HttpStatus.CREATED);
  }

  /**
   * Retrieves all unresolved HelpRequests.
   *
   * @return ResponseEntity containing a list of unresolved help requests
   */
  @GetMapping
  public ResponseEntity<List<HelpRequest>> getActiveHelpRequests() {
    List<HelpRequest> activeRequests = helpRequestRepository.findByResolvedFalse();
    return ResponseEntity.ok(activeRequests);
  }

  /**
   * Retrieves a specific HelpRequest by ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<HelpRequest> getHelpRequestById(@PathVariable Long id) {
    Optional<HelpRequest> helpRequest = helpRequestRepository.findById(id);

    return helpRequest.map(ResponseEntity::ok) // If found, return HTTP 200 with the help request
        .orElseGet(() -> ResponseEntity.notFound().build()); // If not found, return HTTP 404
  }

  /**
   * Resolves a HelpRequest by ID.
   *
   * @param id the ID of the help request to resolve
   * @return ResponseEntity containing the updated help request if found, or a 404 response
   */
  @PutMapping("/{id}/resolve")
  public ResponseEntity<?> resolveHelpRequest(@PathVariable Long id) {
    Optional<HelpRequest> optionalRequest = helpRequestRepository.findById(id);
    if (optionalRequest.isPresent()) {
      HelpRequest helpRequest = optionalRequest.get();
      helpRequest.setResolved(true);
      helpRequestRepository.save(helpRequest);
      logger.info("HelpRequest {} marked as resolved.", id);
      return ResponseEntity.ok(helpRequest);
    }
    logger.error("ERROR: HelpRequest {} not found.", id);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Help request not found");
  }
}




