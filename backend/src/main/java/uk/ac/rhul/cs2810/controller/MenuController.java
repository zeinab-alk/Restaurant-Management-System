package uk.ac.rhul.cs2810.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.Menu;
import uk.ac.rhul.cs2810.repository.MenuRepository;

/**
 * A REST controller that handles menu instances.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class MenuController {

  private final MenuRepository menuRepository;

  /**
   * Constructor for repositories.
   *
   * @param menuRepository class object
   */
  public MenuController(MenuRepository menuRepository) {
    this.menuRepository = menuRepository;
  }

  /**
   * getMenu GET handler.
   *
   * @param params contains "menu_id"
   * @return the requested menu if found
   */
  @GetMapping(value = "/menu/getMenu")
  public ResponseEntity<Menu> getMenu(@RequestParam Map<String, String> params) {
    int menuId = Integer.parseInt(params.get("menu_id"));
    return menuRepository.findById(menuId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(404).body(null)); // Return 404 if menu is not found
  }
  
  /**
   * Get a menu by its ID.
   *
   * @param id the menu ID
   * @return the requested menu if found
   */
  @GetMapping("/menus/{id}")
  public ResponseEntity<Menu> getMenuById(@PathVariable int id) {
    return menuRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.status(404).body(null));
  }
}