package com.webapp.models.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordDTO {
  private String oldPassword;
  private String newPassword;
  private String confirmPassword;
}
