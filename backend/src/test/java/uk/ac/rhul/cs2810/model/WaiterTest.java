package uk.ac.rhul.cs2810.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class WaiterTest {

  private Waiter waiter;
  private Menu mockMenu;
  private MenuItem mockMenuItem;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalSystemOut = System.out;

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

  // Reset System.out after the test
  @AfterEach
  public void restoreSystemOut() {
    System.setOut(originalSystemOut);
  }
}

