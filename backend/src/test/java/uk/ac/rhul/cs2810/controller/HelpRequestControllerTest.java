package uk.ac.rhul.cs2810.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import uk.ac.rhul.cs2810.model.HelpRequest;
import uk.ac.rhul.cs2810.model.Waiter;
import uk.ac.rhul.cs2810.repository.HelpRequestRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class HelpRequestControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private HelpRequestRepository helpRequestRepository;

  @Test
  @WithMockUser
  public void testCreateHelpRequest() throws Exception {
    // Create a HelpRequest instance for testing.
    HelpRequest helpRequest = new HelpRequest(5, new Waiter());
    doReturn(helpRequest).when(helpRequestRepository).save(any(HelpRequest.class));

    mockMvc.perform(post("/api/help-requests").param("tableNumber", "5").param("waiterId", "1")
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
        .andExpect(jsonPath("$.tableNumber").value(5));
  }

  @Test
  @WithMockUser
  public void testGetAllHelpRequests() throws Exception {
    HelpRequest req1 = new HelpRequest(1, new Waiter());
    HelpRequest req2 = new HelpRequest(2, new Waiter());
    when(helpRequestRepository.findAll()).thenReturn(Arrays.asList(req1, req2));

    mockMvc.perform(get("/api/help-requests")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0].tableNumber").value(1))
        .andExpect(jsonPath("$[1].tableNumber").value(2));
  }

  @Test
  @WithMockUser
  public void testGetHelpRequestById() throws Exception {
    HelpRequest helpRequest = new HelpRequest(3, new Waiter());
    when(helpRequestRepository.findById(3L)).thenReturn(Optional.of(helpRequest));

    mockMvc.perform(get("/api/help-requests/3")).andExpect(status().isOk())
        .andExpect(jsonPath("$.tableNumber").value(3));
  }

  @Test
  @WithMockUser
  public void testGetHelpRequestByIdNotFound() throws Exception {
    when(helpRequestRepository.findById(5L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/help-requests/5")).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  public void testResolveHelpRequest() throws Exception {
    HelpRequest helpRequest = new HelpRequest(4, new Waiter());
    helpRequest.setResolved(false);
    when(helpRequestRepository.findById(4L)).thenReturn(Optional.of(helpRequest));
    when(helpRequestRepository.save(helpRequest)).thenReturn(helpRequest);

    mockMvc.perform(put("/api/help-requests/4/resolve").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.resolved").value(true));
  }

}
