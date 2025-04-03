package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.ac.rhul.cs2810.model.HelpRequest;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.HelpRequestRepository;
import uk.ac.rhul.cs2810.repository.WaiterRepository;

/**
 * This tells us that the tests are run via springboot.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class HelpRequestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private HelpRequestRepository helpRequestRepository;
  
  @MockBean
  private WaiterRepository waiterRepository;
  
  private Waiter testWaiter;
  private HelpRequest testHelpRequest;
  
  /**
   * This will always run first before the tests.
   */
  @BeforeEach
  public void setup() {
    testWaiter = new Waiter();
    testWaiter.setId(1);
    testWaiter.setName("Test Waiter");
    
    testHelpRequest = new HelpRequest(5, testWaiter);
    testHelpRequest.setId(1L);
    testHelpRequest.setResolved(false);
  }

  /**
   * Test creating a help request.
   */
  @Test
  public void testCreateHelpRequest() throws Exception {
    // Set up mock repository behavior
    when(waiterRepository.findAll()).thenReturn(Arrays.asList(testWaiter));
    when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(testHelpRequest);

    mockMvc
        .perform(post("/api/help-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"tableNumber\":5}"))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.tableNumber").value(5))
        .andExpect(jsonPath("$.resolved").value(false));
  }
  
  /**
   * Test creating a help request with invalid table number.
   */
  @Test
  public void testCreateHelpRequestWithInvalidTableNumber() throws Exception {
    mockMvc
        .perform(post("/api/help-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"tableNumber\":-1}"))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }
  
  /**
   * Test creating a help request when no waiters are available.
   */
  @Test
  public void testCreateHelpRequestNoWaitersAvailable() throws Exception {
    // Set up empty waiter list
    when(waiterRepository.findAll()).thenReturn(Collections.emptyList());

    mockMvc
        .perform(post("/api/help-requests")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"tableNumber\":5}"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  /**
   * Test retrieving a help request by ID.
   */
  @Test
  public void testGetHelpRequestById() throws Exception {
    when(helpRequestRepository.findById(1L)).thenReturn(Optional.of(testHelpRequest));

    mockMvc.perform(get("/api/help-requests/1"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tableNumber").value(5))
        .andExpect(jsonPath("$.resolved").value(false));
  }

  /**
   * Test retrieving a help request by a non-existing ID.
   */
  @Test
  public void testGetHelpRequestByIdNotFound() throws Exception {
    when(helpRequestRepository.findById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/help-requests/99"))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  /**
   * Test resolving a help request.
   */
  @Test
  public void testResolveHelpRequest() throws Exception {
    HelpRequest resolvedRequest = new HelpRequest(5, testWaiter);
    resolvedRequest.setId(1L);
    resolvedRequest.setResolved(true);
    
    when(helpRequestRepository.findById(1L)).thenReturn(Optional.of(testHelpRequest));
    when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(resolvedRequest);

    mockMvc.perform(put("/api/help-requests/1/resolve")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resolved").value(true));
  }

  /**
   * Test resolving a help request that does not exist.
   */
  @Test
  public void testResolveHelpRequestNotFound() throws Exception {
    when(helpRequestRepository.findById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(put("/api/help-requests/99/resolve")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  /**
   * Test retrieving all active (unresolved) help requests.
   */
  @Test
  public void testGetActiveHelpRequests() throws Exception {
    List<HelpRequest> activeRequests = Arrays.asList(testHelpRequest);
    when(helpRequestRepository.findByResolvedFalse()).thenReturn(activeRequests);

    mockMvc.perform(get("/api/help-requests"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].tableNumber").value(5))
        .andExpect(jsonPath("$[0].resolved").value(false));
  }
  
  /**
   * Test retrieving all help requests when none are active.
   */
  @Test
  public void testGetActiveHelpRequestsEmpty() throws Exception {
    when(helpRequestRepository.findByResolvedFalse()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/help-requests"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
  }
}

