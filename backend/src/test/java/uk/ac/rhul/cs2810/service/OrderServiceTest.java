package uk.ac.rhul.cs2810.service;

import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import uk.ac.rhul.cs2810.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OrderService.
 * Uses Mockito to simulate repository behavior.
 */
public class OrderServiceTest {

  @InjectMocks
  private OrderService orderService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private MenuItemRepository menuItemRepository;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Test that a menu item is properly added to an order.
   */
  @Test
  void testAddItemToOrder() {
    // Arrange: create a new order and a menu item
    Order order = new Order();
    order.setOrderNumber(1);
    MenuItem menuItem = new MenuItem();
    // (No need to set IDs manually if they're auto-generated)

    // Configure the repository mocks:
    when(orderRepository.findById(1)).thenReturn(Optional.of(order));
    when(menuItemRepository.findById(100)).thenReturn(Optional.of(menuItem));
    when(orderRepository.save(order)).thenReturn(order);

    // Act: call the service method to add the menu item
    Order updatedOrder = orderService.addItemToOrder(1, 100);

    // Assert: the updated order should contain the new menu item
    assertTrue(updatedOrder.getMenuItems().contains(menuItem));
  }
}