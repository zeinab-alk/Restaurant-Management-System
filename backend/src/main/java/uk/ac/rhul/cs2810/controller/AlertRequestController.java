package uk.ac.rhul.cs2810.controller;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.AlertRequest;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.AlertRequestRepository;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

/**
 * Controller for handling Alert Requests.
 */
@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:5173")
public class AlertRequestController {

  private static final Logger logger = LoggerFactory.getLogger(AlertRequestController.class);

  @Autowired
  private AlertRequestRepository alertRequestRepository;

  @Autowired
  private WaiterRepository waiterRepository;

  @Autowired
  private SimpMessagingTemplate messagingTemplate;

  /**
   * Creates a new Alert Request.
   *
   * @param request the Alert Request object to be created.
   * @return ResponseEntity containing the created Alert Request or an error message.
   */
  @PostMapping
  public ResponseEntity<?> createAlertRequest(@RequestBody AlertRequest request) {
    logger.info("Received AlertRequest: Table {} - Type: {}", request.getTableNumber(),
        request.getAlertType());

    if (request.getTableNumber() <= 0 || request.getAlertType() == null
        || request.getAlertType().isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Invalid table number or alert type.");
    }

    List<Waiter> availableWaiters = (List<Waiter>) waiterRepository.findAll();
    if (availableWaiters.isEmpty()) {
      logger.error("ERROR: No waiters available.");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No available waiters.");
    }

    request.setWaiter(availableWaiters.get(0));
    request.setResolved(false);
    alertRequestRepository.save(request);

    messagingTemplate.convertAndSend("/topic/alerts", request);

    logger.info("✅ Alert request successfully saved for Table {}", request.getTableNumber());
    return new ResponseEntity<>(request, HttpStatus.CREATED);
  }

  /**
   * Retrieves all unresolved Alert Requests.
   *
   * @return ResponseEntity containing a list of unresolved AlertRequests.
   */
  @GetMapping
  public ResponseEntity<List<AlertRequest>> getActiveAlertRequests() {
    List<AlertRequest> activeAlerts = alertRequestRepository.findByResolvedFalse();
    return ResponseEntity.ok(activeAlerts);
  }

  /**
   * Resolves an Alert Request.
   *
   * @param id the ID of the alert request to resolve.
   * @return Response Entity indicating success or failure.
   */
  @PutMapping("/{id}/resolve")
  public ResponseEntity<?> resolveAlertRequest(@PathVariable Long id) {
    Optional<AlertRequest> optionalAlert = alertRequestRepository.findById(id);
    if (optionalAlert.isPresent()) {
      AlertRequest alertRequest = optionalAlert.get();
      alertRequest.setResolved(true);
      alertRequestRepository.save(alertRequest);

      messagingTemplate.convertAndSend("/topic/alerts", alertRequest);
      logger.info("✅ AlertRequest {} marked as resolved.", id);
      return ResponseEntity.ok(alertRequest);
    }
    logger.error("❌ ERROR: AlertRequest {} not found.", id);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alert request not found");
  }
}


