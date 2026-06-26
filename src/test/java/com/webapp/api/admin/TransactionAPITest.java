package com.webapp.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.models.dtos.TransactionDTO;
import com.webapp.services.TransactionService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionAPI.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("TransactionAPI Unit Tests")
class TransactionAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionService transactionService;

  @MockBean
  private UserDetailsService userDetailsService;

  @MockBean
  private JwtTokenUtils jwtTokenUtils;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST / should save transaction")
  void save() throws Exception {
    TransactionDTO dto = new TransactionDTO();
    dto.setCustomerId(1L);
    dto.setCode("CSKH");
    dto.setNote("Test note");

    doNothing().when(transactionService).save(any(TransactionDTO.class));

    mockMvc.perform(post("/admin/api/transactions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Transaction saved successfully"));
  }

  @Test
  @DisplayName("GET /{id} should return transaction note")
  void getById() throws Exception {
    TransactionDTO dto = new TransactionDTO();
    dto.setNote("Test note");
    when(transactionService.findById(1L)).thenReturn(dto);

    mockMvc.perform(get("/admin/api/transactions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").value("Test note"));
  }

  @Test
  @DisplayName("DELETE /{id} should delete transaction")
  void deleteTransaction() throws Exception {
    doNothing().when(transactionService).delete(1L);

    mockMvc.perform(delete("/admin/api/transactions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));

    verify(transactionService).delete(1L);
  }

  @Test
  @DisplayName("GET /customer/{customerId} should return transactions for customer")
  void getByCustomerId() throws Exception {
    Map<String, List<TransactionDTO>> transactions = new HashMap<>();
    TransactionDTO tx = new TransactionDTO();
    tx.setCode("CSKH");
    transactions.put("CSKH", List.of(tx));

    when(transactionService.getTransactionsByCustomerId(1L)).thenReturn(transactions);

    mockMvc.perform(get("/admin/api/transactions/customer/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.CSKH").isArray())
        .andExpect(jsonPath("$.data.CSKH[0].code").value("CSKH"));
  }
}
