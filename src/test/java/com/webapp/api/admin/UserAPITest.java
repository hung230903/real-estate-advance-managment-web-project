package com.webapp.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.components.JwtTokenUtils;
import com.webapp.models.dtos.LoginDTO;
import com.webapp.models.dtos.PasswordDTO;
import com.webapp.models.dtos.UserDTO;
import com.webapp.security.MyUser;
import com.webapp.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAPI.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserAPI Unit Tests")
class UserAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private AuthenticationManager authenticationManager;

  @MockBean
  private JwtTokenUtils jwtTokenUtils;

  @MockBean
  private UserDetailsService userDetailsService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("POST /login should authenticate and return token")
  void login() throws Exception {
    LoginDTO loginDTO = new LoginDTO();
    loginDTO.setUsername("testUser");
    loginDTO.setPassword("password");

    MyUser myUser = new MyUser("testUser", "password", true, true, true, true, List.of());
    Authentication authentication = new UsernamePasswordAuthenticationToken(myUser, null);

    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(jwtTokenUtils.generateToken(myUser)).thenReturn("mockedToken");

    mockMvc.perform(post("/admin/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Login successful"))
        .andExpect(jsonPath("$.data").value("mockedToken"));
  }

  @Test
  @DisplayName("POST /login should return 401 on failure")
  void login_failure() throws Exception {
    LoginDTO loginDTO = new LoginDTO();
    loginDTO.setUsername("testUser");
    loginDTO.setPassword("wrong");

    when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Bad credentials"));

    mockMvc.perform(post("/admin/api/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginDTO)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Login failed: Bad credentials"));
  }

  @Test
  @DisplayName("POST /register should register new user")
  void register() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setUserName("newUser");
    userDTO.setFullName("New User");

    when(userService.save(any(UserDTO.class))).thenReturn(null);

    mockMvc.perform(post("/admin/api/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(userDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Registration Successfully"));
  }

  @Test
  @DisplayName("DELETE / should delete users")
  void deleteUsers() throws Exception {
    doNothing().when(userService).delete(anyList());

    mockMvc.perform(delete("/admin/api/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content("[1, 2, 3]"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Delete Successfully"));

    verify(userService).delete(List.of(1L, 2L, 3L));
  }

  @Test
  @DisplayName("PUT /password/{id} should return success response")
  void updatePassword() throws Exception {
    PasswordDTO dto = new PasswordDTO("old", "new", "new");

    mockMvc.perform(put("/admin/api/users/password/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }
}
