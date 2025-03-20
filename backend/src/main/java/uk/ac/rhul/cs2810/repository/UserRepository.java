package uk.ac.rhul.cs2810.repository;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.User;

/**
 * A repository that manages User instances.
 */
public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
