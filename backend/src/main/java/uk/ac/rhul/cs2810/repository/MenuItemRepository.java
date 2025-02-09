package uk.ac.rhul.cs2810.repository;

import org.springframework.data.repository.CrudRepository;
import uk.ac.rhul.cs2810.model.MenuItem;

/**
 * A repository that manages MenuItem instances.
 */
public interface MenuItemRepository extends CrudRepository<MenuItem, Integer> {

}