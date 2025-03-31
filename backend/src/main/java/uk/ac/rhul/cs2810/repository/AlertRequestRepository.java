package uk.ac.rhul.cs2810.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2810.model.AlertRequest;

/**
 * Repository interface for managing AlertRequest entities.
 */
public interface AlertRequestRepository extends JpaRepository<AlertRequest, Long> {

  /**
   * Retrieves a list of unresolved alerts.
   *
   * @return a list of AlertRequest objects that are not resolved
   */
  List<AlertRequest> findByResolvedFalse();
}

