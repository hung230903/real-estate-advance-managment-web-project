package com.webapp.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDTO extends AbstractDTO {
    @NotNull(message = "Full name required")
    private String fullName;
    @NotNull(message = "Phone number required")
    private String phone;
    private String email;
    private String companyName;
    private String demand;
    private String status;
    private String statusName;
}
