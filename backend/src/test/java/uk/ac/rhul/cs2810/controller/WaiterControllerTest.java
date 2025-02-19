/**
 * 
 */
package uk.ac.rhul.cs2810.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class WaiterControllerTest {
  @Mock
  private WaiterRepository waiterRepository;

  @InjectMocks
  private WaiterController waiterController;

  @BeforeEach
  public void setUp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  public void callWaiter_WhenWaiterAvailable_ShouldReturnSuccessMessage() {
    // Arrange
    int tableNumber = 5;
    Waiter availableWaiter = new Waiter();
    availableWaiter.setBusy(false);

    when(waiterRepository.findAvailableWaiter()).thenReturn(Optional.of(availableWaiter));

    // Act
    ResponseEntity<String> response = waiterController.callWaiter(tableNumber);

    // Assert
    assertEquals("A waiter is on the way to table " + tableNumber + "!", response.getBody());
    assertTrue(availableWaiter.isBusy());
    verify(waiterRepository, times(1)).save(availableWaiter);
  }

  @Test
  public void callWaiter_WhenNoWaiterAvailable_ShouldReturnErrorMessage() {
    // Arrange
    int tableNumber = 5;
    when(waiterRepository.findAvailableWaiter()).thenReturn(Optional.empty());

    // Act
    ResponseEntity<String> response = waiterController.callWaiter(tableNumber);

    // Assert
    assertEquals("No waiters available. Please call +44 123 456 789 for assistance.",
        response.getBody());
    verify(waiterRepository, never()).save(any(Waiter.class));
  }


}
