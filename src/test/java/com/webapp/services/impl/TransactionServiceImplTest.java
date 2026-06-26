package com.webapp.services.impl;

import com.webapp.converter.TransactionConverter;
import com.webapp.entities.CustomerEntity;
import com.webapp.entities.TransactionEntity;
import com.webapp.enums.TransactionType;
import com.webapp.models.dtos.TransactionDTO;
import com.webapp.repositories.CustomerRepository;
import com.webapp.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionServiceImpl Unit Tests")
class TransactionServiceImplTest {

  @Mock
  private TransactionRepository transactionRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private TransactionConverter transactionConverter;
  @InjectMocks
  private TransactionServiceImpl transactionService;

  private TransactionEntity txEntity;
  private TransactionDTO txDTO;
  private CustomerEntity customer;

  @BeforeEach
  void setUp() {
    customer = new CustomerEntity();
    customer.setId(1L);

    txEntity = new TransactionEntity();
    txEntity.setId(1L);
    txEntity.setCode("CSKH");
    txEntity.setNote("Test note");
    txEntity.setIsActive(1);
    txEntity.setCustomer(customer);

    txDTO = new TransactionDTO();
    txDTO.setId(1L);
    txDTO.setCustomerId(1L);
    txDTO.setCode("CSKH");
    txDTO.setNote("Test note");
  }

  @Nested
  @DisplayName("findByCustomerIdAndCode()")
  class FindByCustomerIdAndCodeTests {
    @Test
    @DisplayName("Returns transactions for valid params")
    void returnsListForValidParams() {
      when(transactionRepository.findByCustomerIdAndCodeAndIsActive(1L, "CSKH", 1))
          .thenReturn(List.of(txEntity));
      when(transactionConverter.toDTO(txEntity)).thenReturn(txDTO);

      List<TransactionDTO> result = transactionService.findByCustomerIdAndCode(1L, "CSKH");
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getCode()).isEqualTo("CSKH");
    }

    @Test
    @DisplayName("Returns empty list when none found")
    void returnsEmptyWhenNone() {
      when(transactionRepository.findByCustomerIdAndCodeAndIsActive(999L, "CSKH", 1))
          .thenReturn(List.of());
      assertThat(transactionService.findByCustomerIdAndCode(999L, "CSKH")).isEmpty();
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {
    @Test
    @DisplayName("Creates new transaction when ID is null")
    void createsNewTransaction() {
      TransactionDTO newDTO = new TransactionDTO();
      newDTO.setCustomerId(1L);
      newDTO.setCode("CSKH");
      newDTO.setNote("New");

      TransactionEntity newEntity = new TransactionEntity();
      when(transactionConverter.toEntity(newDTO)).thenReturn(newEntity);
      when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

      transactionService.save(newDTO);

      assertThat(newEntity.getCustomer()).isEqualTo(customer);
      assertThat(newEntity.getIsActive()).isEqualTo(1);
      verify(transactionRepository).save(newEntity);
    }

    @Test
    @DisplayName("Updates existing transaction note")
    void updatesExistingNote() {
      TransactionDTO upDTO = new TransactionDTO();
      upDTO.setId(1L);
      upDTO.setNote("Updated");

      when(transactionRepository.findById(1L)).thenReturn(Optional.of(txEntity));
      transactionService.save(upDTO);

      assertThat(txEntity.getNote()).isEqualTo("Updated");
      verify(transactionRepository).save(txEntity);
    }

    @Test
    @DisplayName("Throws when transaction not found for update")
    void throwsWhenNotFoundForUpdate() {
      TransactionDTO upDTO = new TransactionDTO();
      upDTO.setId(999L);
      when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> transactionService.save(upDTO))
          .isInstanceOf(RuntimeException.class).hasMessage("Transaction not found");
    }

    @Test
    @DisplayName("Throws when customer not found for new transaction")
    void throwsWhenCustomerNotFound() {
      TransactionDTO newDTO = new TransactionDTO();
      newDTO.setCustomerId(999L);
      newDTO.setCode("CSKH");

      when(transactionConverter.toEntity(newDTO)).thenReturn(new TransactionEntity());
      when(customerRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> transactionService.save(newDTO))
          .isInstanceOf(RuntimeException.class).hasMessage("Customer not found");
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {
    @Test
    @DisplayName("Returns DTO when found")
    void returnsDTOWhenFound() {
      when(transactionRepository.findById(1L)).thenReturn(Optional.of(txEntity));
      when(transactionConverter.toDTO(txEntity)).thenReturn(txDTO);
      TransactionDTO result = transactionService.findById(1L);
      assertThat(result.getNote()).isEqualTo("Test note");
    }

    @Test
    @DisplayName("Throws when not found")
    void throwsWhenNotFound() {
      when(transactionRepository.findById(999L)).thenReturn(Optional.empty());
      assertThatThrownBy(() -> transactionService.findById(999L))
          .isInstanceOf(RuntimeException.class).hasMessage("Transaction not found");
    }
  }

  @Nested
  @DisplayName("delete()")
  class DeleteTests {
    @Test
    @DisplayName("Soft deletes by setting isActive to 0")
    void softDeletes() {
      when(transactionRepository.findById(1L)).thenReturn(Optional.of(txEntity));
      transactionService.delete(1L);
      assertThat(txEntity.getIsActive()).isEqualTo(0);
      verify(transactionRepository).save(txEntity);
    }

    @Test
    @DisplayName("Throws when not found")
    void throwsWhenNotFound() {
      when(transactionRepository.findById(999L)).thenReturn(Optional.empty());
      assertThatThrownBy(() -> transactionService.delete(999L))
          .isInstanceOf(RuntimeException.class).hasMessage("Transaction not found");
    }
  }

  @Nested
  @DisplayName("getTransactionsByCustomerId()")
  class GetByCustomerIdTests {
    @Test
    @DisplayName("Returns grouped map by transaction type")
    void returnsGroupedMap() {
      TransactionEntity e1 = new TransactionEntity();
      e1.setId(1L);
      e1.setCode("CSKH");
      e1.setIsActive(1);
      TransactionEntity e2 = new TransactionEntity();
      e2.setId(2L);
      e2.setCode("DDX");
      e2.setIsActive(1);

      TransactionDTO d1 = new TransactionDTO();
      d1.setCode("CSKH");
      TransactionDTO d2 = new TransactionDTO();
      d2.setCode("DDX");

      when(transactionRepository.findByCustomerIdAndIsActive(1L, 1)).thenReturn(List.of(e1, e2));
      when(transactionConverter.toDTO(e1)).thenReturn(d1);
      when(transactionConverter.toDTO(e2)).thenReturn(d2);

      Map<String, List<TransactionDTO>> result = transactionService.getTransactionsByCustomerId(1L);
      assertThat(result).containsKeys("CSKH", "DDX");
      assertThat(result.get("CSKH")).hasSize(1);
      assertThat(result.get("DDX")).hasSize(1);
    }

    @Test
    @DisplayName("Returns empty lists when no transactions")
    void returnsEmptyWhenNone() {
      when(transactionRepository.findByCustomerIdAndIsActive(1L, 1)).thenReturn(List.of());
      Map<String, List<TransactionDTO>> result = transactionService.getTransactionsByCustomerId(1L);
      for (TransactionType type : TransactionType.values()) {
        assertThat(result.get(type.name())).isEmpty();
      }
    }
  }
}
