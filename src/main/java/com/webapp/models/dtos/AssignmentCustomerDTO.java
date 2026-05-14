package com.webapp.models.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignmentCustomerDTO {
    private Long customerId;
    private List<Long> staffIds;
}
