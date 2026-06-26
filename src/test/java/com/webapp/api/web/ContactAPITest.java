package com.webapp.api.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.models.dtos.CustomerDTO;
import com.webapp.services.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import com.webapp.components.JwtTokenUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactAPI.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ContactAPI Unit Tests")
class ContactAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CustomerService customerService;

  @MockBean
  private UserDetailsService userDetailsService;

  @MockBean
  private JwtTokenUtils jwtTokenUtils;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST / should save contact and return success")
  void receiveContact() throws Exception {
    CustomerDTO dto = new CustomerDTO();
    dto.setFullName("Contact User");
    dto.setPhone("0123456789");

    doNothing().when(customerService).saveOrUpdate(any(CustomerDTO.class));

    mockMvc.perform(post("/api/contact")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Message sent successfully!"));
  }
}
