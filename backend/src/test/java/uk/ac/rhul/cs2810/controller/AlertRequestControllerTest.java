package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.rhul.cs2810.model.AlertRequest;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.AlertRequestRepository;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

@WebMvcTest(AlertRequestController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables security filters for testing.
public class AlertRequestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AlertRequestRepository alertRequestRepository;

  @MockBean
  private WaiterRepository waiterRepository;

  @MockBean
  private SimpMessagingTemplate messagingTemplate;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  public void testCreateAlertRequest_Valid() throws Exception {
    AlertRequest request = new AlertRequest();
    request.setTableNumber(5);
    request.setAlertType("urgent");

    // Create a Waiter using the constructor with int ID
    Waiter waiter = new Waiter(1, "John", "Available", null);

    // Simulate available waiter.
    when(waiterRepository.findAll()).thenReturn(Collections.singletonList(waiter));
    when(alertRequestRepository.save(any(AlertRequest.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated());

    verify(alertRequestRepository).save(any(AlertRequest.class));
    verify(messagingTemplate).convertAndSend(eq("/topic/alerts"), any(AlertRequest.class));
  }

  @Test
  public void testCreateAlertRequest_InvalidTableNumber() throws Exception {
    AlertRequest request = new AlertRequest();
    request.setTableNumber(0); // Invalid value.
    request.setAlertType("urgent");

    mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateAlertRequest_EmptyAlertType() throws Exception {
    AlertRequest request = new AlertRequest();
    request.setTableNumber(5);
    request.setAlertType(""); // Empty alert type.

    mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateAlertRequest_NullAlertType() throws Exception {
    // Table number > 0 but alertType is null => BAD_REQUEST
    AlertRequest request = new AlertRequest();
    request.setTableNumber(5);
    request.setAlertType(null);

    mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().isBadRequest());
  }

  @Test
  public void testCreateAlertRequest_NoWaitersAvailable() throws Exception {
    AlertRequest request = new AlertRequest();
    request.setTableNumber(5);
    request.setAlertType("urgent");

    // Simulate no available waiters.
    when(waiterRepository.findAll()).thenReturn(Collections.emptyList());

    mockMvc.perform(post("/api/alerts").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))).andExpect(status().isNotFound());
  }

  @Test
  public void testGetActiveAlertRequests() throws Exception {
    AlertRequest alert = new AlertRequest();
    alert.setId(1L);
    alert.setTableNumber(5);
    alert.setAlertType("urgent");
    alert.setResolved(false);

    when(alertRequestRepository.findByResolvedFalse()).thenReturn(Collections.singletonList(alert));

    mockMvc.perform(get("/api/alerts")).andExpect(status().isOk());

    verify(alertRequestRepository).findByResolvedFalse();
  }

  @Test
  public void testResolveAlertRequest_ValidId() throws Exception {
    AlertRequest alert = new AlertRequest();
    alert.setId(1L);
    alert.setResolved(false);

    when(alertRequestRepository.findById(1L)).thenReturn(Optional.of(alert));
    when(alertRequestRepository.save(any(AlertRequest.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc.perform(put("/api/alerts/{id}/resolve", 1L)).andExpect(status().isOk());

    verify(alertRequestRepository).findById(1L);
    verify(alertRequestRepository).save(any(AlertRequest.class));
    verify(messagingTemplate).convertAndSend(eq("/topic/alerts"), any(AlertRequest.class));
  }

  @Test
  public void testResolveAlertRequest_InvalidId() throws Exception {
    when(alertRequestRepository.findById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(put("/api/alerts/{id}/resolve", 999L)).andExpect(status().isNotFound());

    verify(alertRequestRepository).findById(999L);
  }
}

