package com.webapp.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.models.dtos.AssignmentCustomerDTO;
import com.webapp.models.dtos.CustomerDTO;
import com.webapp.models.dtos.ResponseDTO;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerAPI.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CustomerAPI Unit Tests")
class CustomerAPITest {

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
  @DisplayName("POST / should save customer")
  void saveOrUpdate() throws Exception {
    CustomerDTO dto = new CustomerDTO();
    dto.setFullName("Test Customer");

    doNothing().when(customerService).saveOrUpdate(any(CustomerDTO.class));

    mockMvc.perform(post("/admin/api/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Successfully saved/updated customer"));
  }

  @Test
  @DisplayName("POST / should handle exception")
  void saveOrUpdate_exception() throws Exception {
    CustomerDTO dto = new CustomerDTO();
    dto.setFullName("Test Customer");

    doThrow(new RuntimeException("Error")).when(customerService).saveOrUpdate(any(CustomerDTO.class));

    mockMvc.perform(post("/admin/api/customers")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Error"));
  }

  @Test
  @DisplayName("DELETE /{ids} should delete customers")
  void deleteCustomers() throws Exception {
    doNothing().when(customerService).deleteCustomers(any());

    mockMvc.perform(delete("/admin/api/customers/1,2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Successfully deleted customers"));

    verify(customerService).deleteCustomers(List.of(1L, 2L));
  }

  @Test
  @DisplayName("GET /{id}/staff should return staff")
  void getStaff() throws Exception {
    ResponseDTO response = new ResponseDTO();
    response.setMessage("Success");
    when(customerService.loadStaffsByCustomerId(1L)).thenReturn(response);

    mockMvc.perform(get("/admin/api/customers/1/staff"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Success"));
  }

  @Test
  @DisplayName("PUT /assign should assign staff to customer")
  void assignStaff() throws Exception {
    AssignmentCustomerDTO dto = new AssignmentCustomerDTO();
    dto.setCustomerId(1L);
    dto.setStaffIds(List.of(1L, 2L));

    doNothing().when(customerService).updateAssignmentCustomer(any(AssignmentCustomerDTO.class));

    mockMvc.perform(put("/admin/api/customers/assign")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Assigned staff successfully"));
  }
}
