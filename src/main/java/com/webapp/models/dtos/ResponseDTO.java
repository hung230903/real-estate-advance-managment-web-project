package com.webapp.models.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    Object data;
    String message;
    String errorDetails;
}
