package com.webapp.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO extends AbstractDTO {
    private Long customerId;
    private String code;
    private String note;
}
