package com.webapp.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
  @NotBlank(message = "Username required")
  private String username;

  @NotBlank(message = "Password required")
  private String password;
}
