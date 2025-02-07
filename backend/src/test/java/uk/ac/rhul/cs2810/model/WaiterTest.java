package uk.ac.rhul.cs2810.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

public class WaiterTest {

  private Waiter waiter;
  private Menu mockMenu;
  private MenuItem mockMenuItem;

  @BeforeEach
  public void setUp() {
    // Mock the Menu and MenuItem to isolate the Waiter class logic
    mockMenu = mock(Menu.class);
    mockMenuItem = mock(MenuItem.class);
    waiter = new Waiter(1, "John", "Active", mockMenu);
  }

  @Test
  public void testWaiterConstructor() {
    assertNotNull(waiter);
    assertEquals(1, waiter.getId());
    assertEquals("John", waiter.getName());
    assertEquals("Active", waiter.getStatus());
  }

  @Test
  public void testChangeMenu_ItemFound() {
    // Mock behavior: when getItemByName is called, return the mock menu item
    when(mockMenu.getItemByName("Pizza")).thenReturn(mockMenuItem);

    // Test the changeMenu method
    waiter.changeMenu("Pizza", true);

    // Verify the setAvailable method was called on the menu item
    verify(mockMenuItem).setAvailable(true);

    // Check the output (would require capturing system output in a real test)
    // No assert here, but you could capture the output if necessary
  }

  @Test
  public void testChangeMenu_ItemNotFound() {
    // Mock behaviour: when getItemByName is called, return null (item not found)
    when(mockMenu.getItemByName("Burger")).thenReturn(null);

    // Test the changeMenu method
    waiter.changeMenu("Burger", false);

    // Verify that the item was not updated (no call to setAvailable)
    verify(mockMenuItem, never()).setAvailable(anyBoolean());
  }

  @Test
  public void testChangeMenu_NoMenu() {
    // Create a waiter with no menu (null)
    Waiter waiterWithoutMenu = new Waiter(1, "John", "Active", null);

    // Test the changeMenu method
    waiterWithoutMenu.changeMenu("Pizza", true);

    // Verify that nothing happened (no menu to change)
    verify(mockMenu, never()).getItemByName(anyString());
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
}

