package com.webapp.converter;

import com.webapp.entities.CustomerEntity;
import com.webapp.entities.TransactionEntity;
import com.webapp.models.dtos.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionConverter Unit Tests")
class TransactionConverterTest {

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TransactionConverter transactionConverter;

    private TransactionEntity entity;
    private TransactionDTO dto;

    @BeforeEach
    void setUp() {
        entity = new TransactionEntity();
        entity.setId(1L);
        entity.setNote("Test Note");
        entity.setCode("CSKH");

        CustomerEntity customer = new CustomerEntity();
        customer.setId(2L);
        entity.setCustomer(customer);

        dto = new TransactionDTO();
        dto.setNote("Test Note");
        dto.setCode("CSKH");
    }

    @Test
    @DisplayName("toDTO should manually map entity to dto and set customer id")
    void toDTO() {
        TransactionDTO result = transactionConverter.toDTO(entity);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNote()).isEqualTo("Test Note");
        assertThat(result.getCode()).isEqualTo("CSKH");
        assertThat(result.getCustomerId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("toDTO should handle null customer")
    void toDTO_nullCustomer() {
        entity.setCustomer(null);
        TransactionDTO result = transactionConverter.toDTO(entity);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isNull();
    }

    @Test
    @DisplayName("toEntity should map dto to entity")
    void toEntity() {
        TransactionEntity result = transactionConverter.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getNote()).isEqualTo("Test Note");
        assertThat(result.getCode()).isEqualTo("CSKH");
    }
}
