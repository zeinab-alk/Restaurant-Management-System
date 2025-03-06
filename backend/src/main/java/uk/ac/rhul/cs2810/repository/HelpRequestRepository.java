package uk.ac.rhul.cs2810.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2810.model.HelpRequest;

/**
 * Repository interface for managing HelpRequest entities.
 * Provides methods to interact with the database.
 */
public interface HelpRequestRepository extends JpaRepository<HelpRequest, Long> {

  /**
   * Retrieves a list of unresolved help requests.
   *
   * @return a list of HelpRequest objects that are not resolved
   */
  List<HelpRequest> findByResolvedFalse();
}