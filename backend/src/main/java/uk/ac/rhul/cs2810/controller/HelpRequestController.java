package uk.ac.rhul.cs2810.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import uk.ac.rhul.cs2810.model.HelpRequest;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.HelpRequestRepository;

import java.util.List;
import java.util.Optional;

/**
 * Controller for handling requests related to Help Requests.
 */
@RestController
@RequestMapping("/api/help-requests")
public class HelpRequestController {

  @Autowired
  private HelpRequestRepository helpRequestRepository;

  /**
   * Creates a new HelpRequest for a specific table and waiter.
   * 
   * @param tableNumber the table number where assistance is requested
   * @param waiterId the ID of the waiter assigned to the request
   * @return a ResponseEntity containing the created HelpRequest
   */
  @PostMapping
  public ResponseEntity<HelpRequest> createHelpRequest(@RequestParam int tableNumber,
      @RequestParam Long waiterId) {
    HelpRequest newRequest = new HelpRequest(tableNumber, new Waiter());

    helpRequestRepository.save(newRequest);
    return new ResponseEntity<>(newRequest, HttpStatus.CREATED);
  }

  /**
   * Retrieves all help requests.
   * 
   * @return a list of all HelpRequests
   */
  @GetMapping
  public ResponseEntity<List<HelpRequest>> getAllHelpRequests() {
    List<HelpRequest> requests = helpRequestRepository.findAll();
    return new ResponseEntity<>(requests, HttpStatus.OK);
  }

  /**
   * Retrieves a specific HelpRequest by its ID.
   * 
   * @param id the ID of the HelpRequest
   * @return a ResponseEntity containing the requested HelpRequest
   */
  @GetMapping("/{id}")
  public ResponseEntity<HelpRequest> getHelpRequestById(@PathVariable Long id) {
    Optional<HelpRequest> request = helpRequestRepository.findById(id);
    return request.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Marks a specific HelpRequest as resolved.
   * 
   * @param id the ID of the HelpRequest to resolve
   * @return a ResponseEntity containing the updated HelpRequest
   */
  @PutMapping("/{id}/resolve")
  public ResponseEntity<HelpRequest> resolveHelpRequest(@PathVariable Long id) {
    Optional<HelpRequest> optionalRequest = helpRequestRepository.findById(id);
    if (optionalRequest.isPresent()) {
      HelpRequest request = optionalRequest.get();
      request.setResolved(true);
      helpRequestRepository.save(request);
      return new ResponseEntity<>(request, HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

}
