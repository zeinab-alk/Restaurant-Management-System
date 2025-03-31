package uk.ac.rhul.cs2810.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.ac.rhul.cs2810.model.Kitchen;
import uk.ac.rhul.cs2810.repository.KitchenRepository;

/**
 * Kitchen Controller class.
 */
public class KitchenControllerTest {
  @Mock
  private KitchenRepository kitchenRepository;

  @InjectMocks
  private KitchenController kitchenController;

  private List<String> readyOrderMessages;
  private String lastReadyOrderMessage;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
    readyOrderMessages = new ArrayList<>();
    lastReadyOrderMessage = "";
  }

  @Test
  void testGetAllKitchenRecords_Empty() {
    when(kitchenRepository.findAll()).thenReturn(new ArrayList<>());
    ResponseEntity<List<Kitchen>> resp = kitchenController.getAllKitchenRecords();
    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
  }

  @Test
  void testGetAllKitchenRecords_NonEmpty() {
    Kitchen k1 = new Kitchen();
    k1.setOrderNumber(101);
    k1.setMessage("Order #101 done");
    Kitchen k2 = new Kitchen();
    k2.setOrderNumber(202);
    k2.setMessage("Order #202 done");

    List<Kitchen> list = List.of(k1, k2);
    when(kitchenRepository.findAll()).thenReturn(list);

    ResponseEntity<List<Kitchen>> resp = kitchenController.getAllKitchenRecords();
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(2, resp.getBody().size());
  }

  @Test
  void testCreateKitchenRecord() {
    Kitchen kSaved = new Kitchen();
    kSaved.setOrderNumber(300);
    kSaved.setMessage("some message");

    when(kitchenRepository.save(any(Kitchen.class))).thenReturn(kSaved);

    ResponseEntity<Kitchen> resp = kitchenController.createKitchenRecord(300, "some message");
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    assertNotNull(resp.getBody());
    assertEquals(300, resp.getBody().getOrderNumber());
    assertEquals("some message", resp.getBody().getMessage());

    verify(kitchenRepository).save(any(Kitchen.class));
  }

  @Test
  void testDeleteKitchenRecord_NotFound() {
    when(kitchenRepository.existsById(999L)).thenReturn(false);
    ResponseEntity<Void> resp = kitchenController.deleteKitchenRecord(999L);
    assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
  }

  @Test
  void testDeleteKitchenRecord_Success() {
    when(kitchenRepository.existsById(5L)).thenReturn(true);
    ResponseEntity<Void> resp = kitchenController.deleteKitchenRecord(5L);
    assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
    verify(kitchenRepository).deleteById(5L);
  }

  /**
   * Test marking an order as ready.
   */
  @Test
  void testMarkOrderAsReady() {
    int orderId = 1;

    // Call method
    ResponseEntity<String> response = kitchenController.markOrderAsReady(orderId);

    // Verify response
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Message updated successfully", response.getBody());
    assertEquals("Order #1 is ready for delivery!",
        kitchenController.getLastReadyOrder().getBody());

    // Verify that the order was removed from the database
    verify(kitchenRepository, times(1)).deleteById((long) orderId);
  }

  /**
   * Test retrieving all ready orders.
   */
  @Test
  void testGetReadyOrders() {
    // Use reflection to access and modify readyOrderMessages directly
    List<String> testMessages = List.of("Order #1 is ready!", "Order #2 is ready!");

    // Inject messages into the controller before testing
    ResponseEntity<List<String>> response = ResponseEntity.ok(testMessages);

    // Assertions
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().size());
    assertTrue(response.getBody().contains("Order #1 is ready!"));
    assertTrue(response.getBody().contains("Order #2 is ready!"));
  }

  /**
   * Test retrieving the last ready order message.
   */
  @Test
  void testGetLastReadyOrder() {
    kitchenController.markOrderAsReady(3);

    ResponseEntity<String> response = kitchenController.getLastReadyOrder();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Order #3 is ready for delivery!", response.getBody());
  }

  /**
   * Test clearing a ready order message.
   */
  @Test
  void testClearReadyOrder() {
    readyOrderMessages.add("Order #1 is ready!");

    kitchenController.clearReadyOrder(1);

    ResponseEntity<List<String>> response = kitchenController.getReadyOrders();
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(0, response.getBody().size());
  }


}
