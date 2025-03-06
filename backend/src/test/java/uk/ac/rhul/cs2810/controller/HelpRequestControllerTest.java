package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.rhul.cs2810.model.HelpRequest;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.HelpRequestRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class HelpRequestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private HelpRequestRepository helpRequestRepository;

  /**
   * Test creating a help request.
   */
  @Test
  public void testCreateHelpRequest() throws Exception {
    HelpRequest helpRequest = new HelpRequest(5, new Waiter());
    when(helpRequestRepository.save(any(HelpRequest.class))).thenReturn(helpRequest);

    mockMvc
        .perform(post("/api/help-requests").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(helpRequest))) // Send JSON body
        .andDo(print()).andExpect(status().isCreated())
        .andExpect(jsonPath("$.tableNumber").value(5));
  }

  /**
   * Test retrieving a help request by ID.
   */
  @Test
  public void testGetHelpRequestById() throws Exception {
    HelpRequest helpRequest = new HelpRequest(3, new Waiter());
    when(helpRequestRepository.findById(3L)).thenReturn(Optional.of(helpRequest));

    mockMvc.perform(get("/api/help-requests/3")).andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.tableNumber").value(3));
  }

  /**
   * Test retrieving a help request by a non-existing ID.
   */
  @Test
  public void testGetHelpRequestByIdNotFound() throws Exception {
    when(helpRequestRepository.findById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/help-requests/99")).andDo(print()).andExpect(status().isNotFound());
  }

  /**
   * Test resolving a help request.
   */
  @Test
  public void testResolveHelpRequest() throws Exception {
    HelpRequest helpRequest = new HelpRequest(4, new Waiter());
    helpRequest.setResolved(false);
    when(helpRequestRepository.findById(4L)).thenReturn(Optional.of(helpRequest));
    when(helpRequestRepository.save(helpRequest)).thenReturn(helpRequest);

    mockMvc.perform(put("/api/help-requests/4/resolve").contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.resolved").value(true));
  }

  /**
   * Test resolving a help request that does not exist.
   */
  @Test
  public void testResolveHelpRequestNotFound() throws Exception {
    when(helpRequestRepository.findById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(put("/api/help-requests/99/resolve").contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isNotFound());
  }

  /**
   * Test retrieving all help requests.
   */
  @Test
  public void testGetAllHelpRequests() throws Exception {
    when(helpRequestRepository.findByResolvedFalse()).thenReturn(java.util.Collections.emptyList());

    mockMvc.perform(get("/api/help-requests")).andDo(print()).andExpect(status().isOk());
  }
}

