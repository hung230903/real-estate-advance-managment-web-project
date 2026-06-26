package com.webapp.converter;

import com.webapp.entities.TransactionEntity;
import com.webapp.models.dtos.TransactionDTO;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionConverter {

  private final ModelMapper modelMapper;

  public TransactionDTO toDTO(TransactionEntity entity) {
    TransactionDTO dto = new TransactionDTO();
    dto.setId(entity.getId());
    dto.setNote(entity.getNote());
    dto.setCode(entity.getCode());
    dto.setCreatedDate(entity.getCreatedDate());
    dto.setCreatedBy(entity.getCreatedBy());
    dto.setModifiedDate(entity.getModifiedDate());
    dto.setModifiedBy(entity.getModifiedBy());
    if (entity.getCustomer() != null) {
      dto.setCustomerId(entity.getCustomer().getId());
    }
    return dto;
  }

  public TransactionEntity toEntity(TransactionDTO dto) {
    TransactionEntity entity = new TransactionEntity();
    entity.setNote(dto.getNote());
    entity.setCode(dto.getCode());
    return entity;
  }
}
