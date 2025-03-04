package uk.ac.rhul.cs2810.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2810.model.Order;

/**
 * Repository interface for managing orders in the database.
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {

  Optional<Order> findTopByOrderByOrderTimeDesc();
  
  /**
   * Find an order by its order number.
   * 
   * @param orderNumber The order number to search for
   * @return Optional containing the order if found
   */
  Optional<Order> findByOrderNumber(int orderNumber);
}