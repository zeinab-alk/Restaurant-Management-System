package uk.ac.rhul.cs2810.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.rhul.cs2810.model.User;

/**
 * Repository interface for User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByPin(String pin);
}
