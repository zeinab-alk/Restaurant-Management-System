package uk.ac.rhul.cs2810.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.User;

public interface UserRepository extends CrudRepository<User, Long>{

}
