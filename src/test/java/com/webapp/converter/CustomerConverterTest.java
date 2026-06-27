package com.webapp.converter;

import com.webapp.entities.CustomerEntity;
import com.webapp.enums.CustomerStatus;
import com.webapp.models.dtos.CustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerConverter Unit Tests")
class CustomerConverterTest {

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CustomerConverter customerConverter;

    private CustomerEntity entity;
    private CustomerDTO dto;

    @BeforeEach
    void setUp() {
        entity = new CustomerEntity();
        entity.setId(1L);
        entity.setFullName("Nguyen Van A");

        dto = new CustomerDTO();
        dto.setId(1L);
        dto.setFullName("Nguyen Van A");
    }

    @Test
    @DisplayName("toCustomerDTO should convert entity to dto and map status name")
    void toCustomerDTO_withValidStatus() {
        entity.setStatus("CHUA_XU_LY");
        when(modelMapper.map(entity, CustomerDTO.class)).thenReturn(dto);

        CustomerDTO result = customerConverter.toCustomerDTO(entity);

        assertThat(result).isNotNull();
        assertThat(result.getStatusName()).isEqualTo(CustomerStatus.CHUA_XU_LY.getName());
        verify(modelMapper).map(entity, CustomerDTO.class);
    }

    @Test
    @DisplayName("toCustomerDTO should ignore invalid status without crashing")
    void toCustomerDTO_withInvalidStatus() {
        entity.setStatus("INVALID_STATUS");
        when(modelMapper.map(entity, CustomerDTO.class)).thenReturn(dto);

        CustomerDTO result = customerConverter.toCustomerDTO(entity);

        assertThat(result).isNotNull();
        assertThat(result.getStatusName()).isNull();
        verify(modelMapper).map(entity, CustomerDTO.class);
    }

    @Test
    @DisplayName("toCustomerEntity should convert dto to entity")
    void toCustomerEntity() {
        when(modelMapper.map(dto, CustomerEntity.class)).thenReturn(entity);

        CustomerEntity result = customerConverter.toCustomerEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(modelMapper).map(dto, CustomerEntity.class);
    }

    @Test
    @DisplayName("updateEntity should map dto to existing entity")
    void updateEntity() {
        customerConverter.updateEntity(dto, entity);
        verify(modelMapper).map(dto, entity);
    }
}
