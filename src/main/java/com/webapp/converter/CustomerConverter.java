package com.webapp.converter;

import com.webapp.entities.CustomerEntity;
import com.webapp.models.dtos.CustomerDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerConverter {
    private final ModelMapper modelMapper;

    public CustomerDTO toCustomerDTO(CustomerEntity entity) {
        CustomerDTO dto = modelMapper.map(entity, CustomerDTO.class);
        if (entity.getStatus() != null && !entity.getStatus().isEmpty()) {
            try {
                dto.setStatusName(com.webapp.enums.CustomerStatus.valueOf(entity.getStatus()).getName());
            } catch (Exception ignored) {}
        }
        return dto;
    }

    public CustomerEntity toCustomerEntity(CustomerDTO dto) {
        return modelMapper.map(dto, CustomerEntity.class);
    }

    public void updateEntity(CustomerDTO dto, CustomerEntity entity) {
        modelMapper.map(dto, entity);
    }
}
