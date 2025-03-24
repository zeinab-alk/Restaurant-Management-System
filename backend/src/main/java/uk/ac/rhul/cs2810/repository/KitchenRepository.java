package uk.ac.rhul.cs2810.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2810.model.Kitchen;

/**
 * Repository for kitchen.
 */
public interface KitchenRepository extends JpaRepository<Kitchen, Long> {

}
