package com.webapp.models.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ResponseDTO {
    Object data;
    String message;
    List<String> errorDetails;
}
