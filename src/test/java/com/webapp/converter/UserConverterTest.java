package com.webapp.converter;

import com.webapp.constant.SystemConstant;
import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.StaffResponseDTO;
import com.webapp.models.dtos.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserConverter Unit Tests")
class UserConverterTest {

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserConverter userConverter;

  private UserEntity userEntity;
  private UserDTO userDTO;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testUser");
    userEntity.setFullName("Test User");
    userEntity.setPhone("0123456789");
    userEntity.setUserRole(SystemConstant.USER_ROLE);
    userEntity.setActive(true);

    userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setUserName("testUser");
    userDTO.setFullName("Test User");
    userDTO.setPhone("0123456789");
    userDTO.setPassword("password");
    userDTO.setRoleCode(SystemConstant.USER_ROLE);
    userDTO.setStatus(1);
  }

  @Test
  @DisplayName("Should convert to StaffResponseDTO with checked status")
  void toStaffResponseDTO_checked() {
    Set<Long> assignedIds = Set.of(1L, 2L);
    StaffResponseDTO result = userConverter.toStaffResponseDTO(userEntity, assignedIds);

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUserName()).isEqualTo("testUser");
    assertThat(result.getChecked()).isEqualTo("checked");
  }

  @Test
  @DisplayName("Should convert to StaffResponseDTO without checked status")
  void toStaffResponseDTO_unchecked() {
    Set<Long> assignedIds = Set.of(2L, 3L);
    StaffResponseDTO result = userConverter.toStaffResponseDTO(userEntity, assignedIds);

    assertThat(result.getChecked()).isEmpty();
  }

  @Test
  @DisplayName("Should convert to UserDTO")
  void toUserDTO() {
    UserDTO result = userConverter.toUserDTO(userEntity);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUserName()).isEqualTo("testUser");
    assertThat(result.getRoleCode()).isEqualTo(SystemConstant.USER_ROLE);
    assertThat(result.getStatus()).isEqualTo(1);
  }

  @Test
  @DisplayName("Should convert to UserEntity")
  void toUserEntity() {
    when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

    UserEntity result = userConverter.toUserEntity(userDTO);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getUserName()).isEqualTo("testUser");
    assertThat(result.getEncrytedPassword()).isEqualTo("encodedPassword");
    assertThat(result.getUserRole()).isEqualTo(SystemConstant.USER_ROLE);
  }

  @Test
  @DisplayName("Should update UserEntity from UserDTO")
  void updateEntity() {
    userDTO.setFullName("Updated Name");
    userDTO.setStatus(0);

    userConverter.updateEntity(userDTO, userEntity);

    assertThat(userEntity.getFullName()).isEqualTo("Updated Name");
    assertThat(userEntity.isActive()).isFalse();
  }
}
