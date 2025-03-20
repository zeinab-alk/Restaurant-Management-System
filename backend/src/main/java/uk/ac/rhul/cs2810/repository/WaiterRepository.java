package uk.ac.rhul.cs2810.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uk.ac.rhul.cs2810.model.Waiter;

/**
 * Repository to manage Waiters.
 */
public interface WaiterRepository extends CrudRepository<Waiter, Integer> {

  /**
   * Find waiters by name.
   *
   * @param name of waiter
   * @return the waiter
   */
  Waiter findByName(String name);

  /**
   * Find by busy false.
   *
   * @return Optional of waiter
   */
  Optional<Waiter> findByBusyFalse();

  /**
   * Find by busy false.
   *
   * @return List of waiters
   */
  List<Waiter> findAllByBusyFalse();

  /**
   * Finds the first Waiter by status (e.g. 'Available'), ordered by ID ascending.
   *
   * <p>Example usage: waiterRepository.findFirstByStatusOrderByIdAsc("Available");
   *
   * @param status the status of the waiter.
   * @return an optional Waiter object.
   */
  Optional<Waiter> findFirstByStatusOrderByIdAsc(String status);

  /**
   * Finds the first Waiter who is not busy, ordered by ID ascending.
   *
   * <p>Example usage: waiterRepository.findFirstByBusyFalseOrderByIdAsc();
   *
   * @return an optional Waiter object.
   */
  Optional<Waiter> findFirstByBusyFalseOrderByIdAsc();

  /**
   * Returns one available Waiter (status='Available' AND busy=false), picking the first by ID.
   *
   * <p>Uses a native SQL query for LIMIT 1.
   *
   * @return an optional Waiter object.
   */
  @Query(value = "SELECT * FROM waiters w WHERE w.status='Available' AND w.busy=false "
      + "ORDER BY w.id ASC LIMIT 1", nativeQuery = true)
  Optional<Waiter> findAvailableWaiter();

  /**
   * Finds all waiters assigned to a specific table.
   *
   * @param tableNumber The table number.
   * @return List of waiters assigned to the given table.
   */
  @Query("SELECT w FROM Waiter w JOIN w.assignedTables ta WHERE ta.tableNumber = :tableNumber")
  List<Waiter> findWaitersByAssignedTable(@Param("tableNumber") int tableNumber);

}
