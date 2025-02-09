package uk.ac.rhul.cs2810.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import org.springframework.http.HttpStatus;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class MenuItemController {

    private final MenuItemRepository menuItemRepository;

    public MenuItemController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/menuItems")
    public ResponseEntity<Iterable<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemRepository.findAll());
    }

    @PostMapping("/menuItems")
    public ResponseEntity<?> addMenuItem(@RequestBody MenuItem menuItem) {
        try {
            menuItem.setItemId(0);
            MenuItem savedMenuItem = menuItemRepository.save(menuItem);
            return ResponseEntity.ok(savedMenuItem);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error saving menu item: " + e.getMessage());
        }
    }

    @PutMapping("/menuItems/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable int id, @RequestBody MenuItem menuItem) {
        menuItem.setItemId(id);
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @GetMapping("/menuItems/{id}")
    public ResponseEntity<?> getMenuItem(@PathVariable int id) {
        try {
            Optional<MenuItem> menuItem = menuItemRepository.findById(id);
            if (menuItem.isPresent()) {
                return ResponseEntity.ok(menuItem.get());
            } else {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Menu item not found");
            }
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving menu item: " + e.getMessage());
        }
    }
} 