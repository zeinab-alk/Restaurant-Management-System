package uk.ac.rhul.cs2810.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Waiter class.
 */
public class WaiterTest {

  private Waiter waiter;
  private Menu mockMenu;
  private MenuItem mockMenuItem;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalSystemOut = System.out;

  /**
   * Will run first always.
   */
  @BeforeEach
  public void setUp() {
    // Mock the Menu and MenuItem to isolate the Waiter class logic
    mockMenu = mock(Menu.class);
    mockMenuItem = mock(MenuItem.class);
    waiter = new Waiter(1, "John", "Active", mockMenu);
    System.setOut(new PrintStream(outContent));
  }

  @Test
  public void testWaiterConstructor() {
    assertNotNull(waiter);
    assertEquals(1, waiter.getId());
    assertEquals("John", waiter.getName());
    assertEquals("Active", waiter.getStatus());
  }

  @Test
  public void testChangeMenu_ItemFound_TrueAvailability() {
    // Mock behaviour: when getItemByName is called, return the mock menu item
    when(mockMenu.getItemByName("Pizza")).thenReturn(mockMenuItem);

    // Test the changeMenu method with availability = true
    waiter.changeMenu("Pizza", true);

    // Verify the setAvailable method was called on the menu item
    verify(mockMenuItem).setAvailable(true);

    // Assert the output is correct for "available"
    assertTrue(outContent.toString().contains("Pizza is now available"));
  }

  @Test
  public void testChangeMenu_ItemFound_FalseAvailability() {
    // Mock behaviour: when getItemByName is called, return the mock menu item
    when(mockMenu.getItemByName("Pizza")).thenReturn(mockMenuItem);

    // Test the changeMenu method with availability = false
    waiter.changeMenu("Pizza", false);

    // Verify the setAvailable method was called on the menu item
    verify(mockMenuItem).setAvailable(false);

    // Assert the output is correct for "unavailable"
    assertTrue(outContent.toString().contains("Pizza is now unavailable"));
  }

  @Test
  public void testChangeMenu_ItemNotFound() {
    // Mock behaviour: when getItemByName is called, return null (item not found)
    when(mockMenu.getItemByName("Burger")).thenReturn(null);

    // Test the changeMenu method
    waiter.changeMenu("Burger", false);

    // Verify that the item was not updated (no call to setAvailable)
    verify(mockMenuItem, never()).setAvailable(anyBoolean());

    // Assert the correct message is printed when item is not found
    assertTrue(outContent.toString().contains("Item Burger not found in the menu."));
  }

  @Test
  public void testChangeMenu_NoMenu() {
    // Create a waiter with no menu (null)
    Waiter waiterWithoutMenu = new Waiter(1, "John", "Active", null);

    // Test the changeMenu method
    waiterWithoutMenu.changeMenu("Pizza", true);

    // Verify that nothing happened (no menu to change)
    verify(mockMenu, never()).getItemByName(anyString());

    // Assert the correct message is printed when no menu is associated
    assertTrue(outContent.toString().contains("No menu is associated with this waiter."));
  }

  @Test
  public void testGettersAndSetters() {
    // Test all getters and setters
    waiter.setId(2);
    waiter.setName("Mike");
    waiter.setStatus("Inactive");

    assertEquals(2, waiter.getId());
    assertEquals("Mike", waiter.getName());
    assertEquals("Inactive", waiter.getStatus());
  }

  // Optionally add more tests for other methods, for example, confirmOrder, trackOrder, etc.
  @Test
  public void testConfirmOrder() {
    // Here you would check the behavior of confirmOrder
    // For now, it's just a placeholder method with no functionality
    assertDoesNotThrow(() -> waiter.confirmOrder());
  }

  @Test
  public void testTrackOrder() {
    // Here you would check the behavior of trackOrder
    assertDoesNotThrow(() -> waiter.trackOrder());
  }

  @Test
  public void testUpdateOrderStatus() {
    // Here you would check the behavior of updateOrderStatus
    assertDoesNotThrow(() -> waiter.updateOrderStatus());
  }

  @Test
  public void testCancelOrder() {
    // Here you would check the behavior of cancelOrder
    assertDoesNotThrow(() -> waiter.cancelOrder());
  }

  @Test
  public void testNotifyTeam() {
    // Here you would check the behavior of notifyTeam
    assertDoesNotThrow(() -> waiter.notifyTeam());
  }

  @Test
  public void testMarkOrderAsDeliveredMethodIfSuccessfulDelivery() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("Pending"); // Ensure status is "Pending"

    waiter.markOrderAsDelivered(mockOrder);

    // Verify that setStatus was called with the correct argument ("Delivered")
    verify(mockOrder).setStatus("Delivered");

    // Check if the correct message was printed
    String output = outContent.toString();
    assertTrue(output.contains("has been marked as Delivered"));
  }


  @Test
  public void testMarkOrderAsDeliveredMethodIfAlreadyDelivered() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("Delivered");

    waiter.markOrderAsDelivered(mockOrder);

    verify(mockOrder, never()).setStatus("Delivered");
    assertTrue(outContent.toString().contains("Order is already delivered or invalid"));
  }


  @Test
  public void testMarkOrderAsDeliveredMethodIfCaseInsensitiveStatus() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("delivered");

    waiter.markOrderAsDelivered(mockOrder);

    verify(mockOrder, never()).setStatus("Delivered");
    assertTrue(outContent.toString().contains("Order is already delivered or invalid"));
  }


  @Test
  public void testMarkOrderAsDeliveredMethodIfUnexpectedStatus() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("Processing");

    waiter.markOrderAsDelivered(mockOrder);

    verify(mockOrder).setStatus("Delivered");
  }

  @Test
  public void testMarkOrderAsDeliveredMethodOutputVerification() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("Pending");

    waiter.markOrderAsDelivered(mockOrder);

    String output = outContent.toString();
    assertTrue(output.contains("has been marked as Delivered"));
  }
  
  @Test
  public void testMarkOrderAsDeliveredMethodIfEmptyOrderStatus() {
      Order mockOrder = mock(Order.class);
      when(mockOrder.getStatus()).thenReturn(""); // Empty status

      waiter.markOrderAsDelivered(mockOrder);

      // This should trigger the "Order is already delivered or invalid" message.
      assertTrue(outContent.toString().contains("Order is already delivered or invalid"));
  }

  @Test
  public void testMarkOrderAsDeliveredMethodIfInProgressStatus() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("in-progress");

    waiter.markOrderAsDelivered(mockOrder);

    verify(mockOrder).setStatus("Delivered");
    assertTrue(outContent.toString().contains("has been marked as Delivered"));
  }

  @Test
  public void testMarkOrderAsDeliveredMethodIfCompletedStatus() {
    // Mock an order with the status "Completed"
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("Completed");

    // Call the markOrderAsDelivered method
    waiter.markOrderAsDelivered(mockOrder);

    // Verify that the status is changed to "Delivered"
    verify(mockOrder).setStatus("Delivered");

    // Check that the correct output is printed
    assertTrue(outContent.toString().contains("has been marked as Delivered"));
  }

  @Test
  public void testMarkOrderAsDeliveredMethodIfCancelledStatus() {
    // Mock an order with the status "Cancelled"
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("Cancelled");

    // Call the markOrderAsDelivered method
    waiter.markOrderAsDelivered(mockOrder);

    // Verify that the status is changed to "Delivered"
    verify(mockOrder).setStatus("Delivered");

    // Check that the correct output is printed
    assertTrue(outContent.toString().contains("has been marked as Delivered"));
  }
  // Reset System.out after the test
  @AfterEach
  public void restoreSystemOut() {
    System.setOut(originalSystemOut);
  }

  @Test
  public void testOrderNull() {
    waiter.markOrderAsDelivered(null);
    assertTrue(outContent.toString().contains("Order is null"));
  }

  @Test
  public void testMarkOrderAsDeliveredMethodIfWhitespaceOrderStatus() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn("   "); // Order status is only whitespace

    waiter.markOrderAsDelivered(mockOrder);

    assertTrue(outContent.toString().contains("Order is already delivered or invalid."));
  }

  @Test
  public void testMarkOrderAsDeliveredMethodIfEmptyStringOrderStatus() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getStatus()).thenReturn(""); // Empty string

    waiter.markOrderAsDelivered(mockOrder);

    assertTrue(outContent.toString().contains("Order is already delivered or invalid."));
  }

  @Test
  public void testSetAndGetMenu() {
    waiter.setMenu(mockMenu);
    assertSame(mockMenu, waiter.getMenu(), "The menu returned was not the one set.");
  }

  @Test
  public void testSetAndGetAssignedTables() {
    List<Integer> assignedTables = Arrays.asList(1, 2, 3);
    waiter.setAssignedTables(assignedTables);
    assertEquals(assignedTables, waiter.getAssignedTables(),
        "The assigned tables returned did not match the ones set.");
  }

  @Test
  public void testSetAndGetCurrentOrders() {
    List<String> currentOrders = Arrays.asList("Order1", "Order2");
    waiter.setCurrentOrders(currentOrders);
    assertEquals(currentOrders, waiter.getCurrentOrders(),
        "The current orders returned did not match the ones set.");
  }

  @Test
  public void testNotifyNewOrder_AddsOrderToList() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getOrderNumber()).thenReturn(101);

    waiter.notifyNewOrder(mockOrder);

    assertNotNull(waiter.getCurrentOrders(), "Current orders list should not be null");
    assertTrue(waiter.getCurrentOrders().contains("101"),
        "Order number should be added to the list");
  }

  @Test
  void testNotifyNewOrder_PrintsNotificationMessage() {
    // Arrange
    Waiter waiter = new Waiter(1, "John", "Available", new Menu());
    Order order = new Order();
    order.setOrderNumber(123); // Ensure order has a valid number

    // Capture console output
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));

    // Act
    waiter.notifyNewOrder(order);

    // Reset System.out
    System.setOut(System.out);

    // Expected output
    String expectedMessage = "Waiter John has been notified of new order: 123";
    String actualOutput = outContent.toString().trim();

    // Assert
    assertTrue(actualOutput.contains(expectedMessage), "Notification message should be printed");
  }


  @Test
  public void testNotifyNewOrder_WithNullOrder() {
    waiter.notifyNewOrder(null);

    assertTrue(waiter.getCurrentOrders() == null || waiter.getCurrentOrders().isEmpty(),
        "Current orders list should remain null or empty when null order is passed");
  }

  @Test
  public void testConfirmOrder_AllItemsAvailable() {
    Order mockOrder = mock(Order.class);
    MenuItem item1 = mock(MenuItem.class);
    MenuItem item2 = mock(MenuItem.class);

    when(item1.getName()).thenReturn("Pizza");
    when(item2.getName()).thenReturn("Burger");
    when(item1.isAvailable()).thenReturn(true);
    when(item2.isAvailable()).thenReturn(true);

    when(mockOrder.getMenuItems()).thenReturn(Arrays.asList(item1, item2));
    when(mockMenu.getItemByName("Pizza")).thenReturn(item1);
    when(mockMenu.getItemByName("Burger")).thenReturn(item2);

    boolean result = waiter.confirmOrder(mockOrder);

    assertTrue(result, "Order should be confirmed when all items are available");
    assertTrue(outContent.toString().contains("Order confirmed"));
  }

  @Test
  public void testConfirmOrder_ItemUnavailable() {
    Order mockOrder = mock(Order.class);
    MenuItem item1 = mock(MenuItem.class);
    MenuItem item2 = mock(MenuItem.class);

    when(item1.getName()).thenReturn("Pizza");
    when(item2.getName()).thenReturn("Burger");
    when(item1.isAvailable()).thenReturn(true);
    when(item2.isAvailable()).thenReturn(false); // One item is unavailable

    when(mockOrder.getMenuItems()).thenReturn(Arrays.asList(item1, item2));
    when(mockMenu.getItemByName("Pizza")).thenReturn(item1);
    when(mockMenu.getItemByName("Burger")).thenReturn(item2);

    boolean result = waiter.confirmOrder(mockOrder);

    assertTrue(outContent.toString().contains("Item unavailable: Burger"));
    assertTrue(!result, "Order should not be confirmed if any item is unavailable");
  }

  @Test
  public void testConfirmOrder_ItemNotInMenu() {
    Order mockOrder = mock(Order.class);
    MenuItem item1 = mock(MenuItem.class);

    when(item1.getName()).thenReturn("Sushi");

    when(mockOrder.getMenuItems()).thenReturn(Arrays.asList(item1));
    when(mockMenu.getItemByName("Sushi")).thenReturn(null); // Item not found in menu

    boolean result = waiter.confirmOrder(mockOrder);

    assertTrue(outContent.toString().contains("Item unavailable: Sushi"));
    assertTrue(!result, "Order should not be confirmed if an item is not in the menu");
  }

  @Test
  public void testConfirmOrder_EmptyOrder() {
    Order mockOrder = mock(Order.class);
    when(mockOrder.getMenuItems()).thenReturn(null);

    boolean result = waiter.confirmOrder(mockOrder);

    assertTrue(outContent.toString().contains("Order or menu is not available."));
    assertTrue(!result, "Order should not be confirmed if the order is empty");
  }



}

