package com.webapp.models.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSearchRequest {
    private String fullName;
    private String phone;
    private String email;
    private Long staffId;
}
