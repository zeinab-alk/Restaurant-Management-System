package uk.ac.rhul.cs2810.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Order;
import uk.ac.rhul.cs2810.repository.MenuItemRepository;
import uk.ac.rhul.cs2810.repository.OrderRepository;

/**
 * Unit tests for the OrderService class.
 * Verifies the business logic in addItemToOrder, mocking out the repositories.
 */
public class OrderServiceTest {

  // The service we're testing
  @InjectMocks
  private OrderService orderService;

  // Mocked dependencies
  @Mock
  private OrderRepository orderRepository;

  @Mock
  private MenuItemRepository menuItemRepository;

  @BeforeEach
  void setUp() {
    // Initialize Mockito mocks for @Mock fields above
    MockitoAnnotations.openMocks(this);
  }

  /**
   * Test that addItemToOrder properly retrieves the order and menu item,
   * calls order.addItem(menuItem), and then saves the updated order.
   */
  @Test
  void testAddItemToOrder_Success() {
    // Arrange
    Order existingOrder = new Order();
    existingOrder.setOrderNumber(123);

    MenuItem menuItem = new MenuItem();

    // Suppose the repositories return these objects
    when(orderRepository.findById(123)).thenReturn(Optional.of(existingOrder));
    when(menuItemRepository.findById(100)).thenReturn(Optional.of(menuItem));

    // When saving the updated order, just return the same object
    when(orderRepository.save(existingOrder)).thenReturn(existingOrder);

    // Act
    Order updatedOrder = orderService.addItemToOrder(123, 100);

    // Assert
    // Make sure the updated order includes the new menu item
    assertTrue(updatedOrder.getMenuItems().contains(menuItem),
        "Order should contain the newly added menu item");
    // Additionally, you can test that the order is indeed the same instance, etc.
  }

  /**
   * Test that addItemToOrder throws an exception if the order doesn't exist.
   */
  @Test
  void testAddItemToOrder_OrderNotFound() {
    // Arrange: empty optional for order
    when(orderRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert: expect an IllegalArgumentException
    assertThrows(IllegalArgumentException.class,
        () -> orderService.addItemToOrder(999, 100),
        "Expected exception when the order is not found");
  }

  /**
   * Test that addItemToOrder throws an exception if the menu item doesn't exist.
   */
  @Test
  void testAddItemToOrder_MenuItemNotFound() {
    // Arrange: order found, but menu item is empty
    Order existingOrder = new Order();
    existingOrder.setOrderNumber(123);
    when(orderRepository.findById(123)).thenReturn(Optional.of(existingOrder));
    when(menuItemRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert: expect an IllegalArgumentException
    assertThrows(IllegalArgumentException.class,
        () -> orderService.addItemToOrder(123, 999),
        "Expected exception when the menu item is not found");
  }
}
