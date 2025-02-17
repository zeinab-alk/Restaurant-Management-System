package uk.ac.rhul.cs2810.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.Menu;

/**
 * A repository that manages Menu instances.
 */
public interface MenuRepository extends CrudRepository<Menu, Integer> {

}
