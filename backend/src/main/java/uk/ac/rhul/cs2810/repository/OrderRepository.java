package uk.ac.rhul.cs2810.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2810.model.Order;

/**
 * Repository interface for managing orders in the database.
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {
}