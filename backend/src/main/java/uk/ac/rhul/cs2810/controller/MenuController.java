package uk.ac.rhul.cs2810.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.rhul.cs2810.model.Menu;
import uk.ac.rhul.cs2810.repository.MenuRepository;

/**
 * A rest controller handles menu instances.
 */
@RestController
public class MenuController {

  private final MenuRepository menuRepository;

  /**
   * Constructors for repositories.
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

    Menu menu = menuRepository.findById(Integer.valueOf(params.get("menu_id"))).orElseThrow();

    return ResponseEntity.ok(menu);
  }

}