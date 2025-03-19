package uk.ac.rhul.cs2810.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2810.model.Order;

/**
 * Repository interface for managing orders in the database.
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {

  Optional<Order> findTopByOrderByOrderTimeDesc();

  List<Order> findByStatus(String status);
}