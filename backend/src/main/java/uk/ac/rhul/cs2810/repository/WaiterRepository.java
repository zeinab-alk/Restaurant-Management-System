package uk.ac.rhul.cs2810.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.Waiter;

/**
 * Repository to manage Waiters.
 */
public interface WaiterRepository extends CrudRepository<Waiter, Integer> {

  @Query("SELECT w FROM Waiter w WHERE w.status = 'Available' ORDER BY w.id ASC LIMIT 1")
  Optional<Waiter> findFirstAvailableWaiter();

  @Query("SELECT w FROM Waiter w WHERE w.busy = false")
  Optional<Waiter> findAvailableWaiter();
}