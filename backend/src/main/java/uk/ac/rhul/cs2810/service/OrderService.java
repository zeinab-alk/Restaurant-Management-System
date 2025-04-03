package uk.ac.rhul.cs2810.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import uk.ac.rhul.cs2810.repository.OrderRepository;

/**
 * Service layer for order management.
 * Contains business logic for handling orders.
 */
@Service
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private MenuItemRepository menuItemRepository;

  /**
   * Adds a menu item to an order.
   * Note: The Order class uses the method addItem(MenuItem), not addMenuItem.
   *
   * @param orderId the order ID
   * @param menuItemId the menu item ID to add
   * @return the updated Order after saving
   */
  public Order addItemToOrder(Integer orderId, Integer menuItemId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));
    MenuItem menuItem = menuItemRepository.findById(menuItemId)
        .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
    // Use the correct method from Order: addItem
    order.addItem(menuItem);
    return orderRepository.save(order);
  }
}
