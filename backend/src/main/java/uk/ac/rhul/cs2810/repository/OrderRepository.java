package uk.ac.rhul.cs2810.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.model.OrderStatus;

/**
 * Repository interface for managing orders in the database.
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {

  // Find the most recent order
  Optional<Order> findTopByOrderByOrderTimeDesc();

  /**
   * Find an order by its order number.
   * 
   *
   * @param orderNumber The order number to search for
   * @return Optional containing the order if found
   */
  Optional<Order> findByOrderNumber(int orderNumber);

  // Find all orders with a specific status
  List<Order> findByStatus(OrderStatus status);
}
