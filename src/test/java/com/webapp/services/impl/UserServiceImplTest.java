package com.webapp.services.impl;

import com.webapp.constant.SystemConstant;
import com.webapp.converter.UserConverter;
import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.UserDTO;
import com.webapp.pagination.PaginationResult;
import com.webapp.repositories.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Unit Tests")
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserConverter userConverter;

  @InjectMocks
  private UserServiceImpl userService;

  private UserEntity userEntity;
  private UserDTO userDTO;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testUser");
    userEntity.setFullName("Test User");
    userEntity.setActive(true);
    userEntity.setUserRole(SystemConstant.USER_ROLE);
    userEntity.setEncrytedPassword("encryptedPassword");

    userDTO = new UserDTO();
    userDTO.setId(1L);
    userDTO.setUserName("testUser");
    userDTO.setFullName("Test User");
    userDTO.setStatus(1);
  }

  @Nested
  @DisplayName("getAllStaff()")
  class GetAllStaffTests {
    @Test
    @DisplayName("Returns map of staff IDs to usernames")
    void returnsMapOfStaff() {
      UserEntity staff1 = new UserEntity();
      staff1.setId(1L);
      staff1.setUserName("staff1");
      UserEntity staff2 = new UserEntity();
      staff2.setId(2L);
      staff2.setUserName("staff2");

      when(userRepository.findByActiveAndUserRole(true, SystemConstant.STAFF_ROLE))
          .thenReturn(List.of(staff1, staff2));

      Map<Long, String> result = userService.getAllStaff();

      assertThat(result).hasSize(2)
          .containsEntry(1L, "staff1")
          .containsEntry(2L, "staff2");
    }
  }

  @Nested
  @DisplayName("listUserInfo()")
  class ListUserInfoTests {
    @Test
    @DisplayName("Returns paginated user list")
    void returnsPaginatedUsers() {
      when(userRepository.countUsers("test")).thenReturn(1);
      when(userRepository.findUsers("test", 1, 3)).thenReturn(List.of(userEntity));

      PaginationResult<UserEntity> result = userService.listUserInfo("test", 1, 3, 3);

      assertThat(result.getTotalRecords()).isEqualTo(1);
      assertThat(result.getEntityList()).hasSize(1);
      assertThat(result.getEntityList().get(0).getUserName()).isEqualTo("testUser");
    }
  }

  @Nested
  @DisplayName("save()")
  class SaveTests {
    @Test
    @DisplayName("Successfully creates new user")
    void createsNewUser() {
      UserDTO newUserDTO = new UserDTO();
      newUserDTO.setUserName("newUser");

      UserEntity newUserEntity = new UserEntity();
      newUserEntity.setUserName("newUser");

      when(userRepository.findByUserName("newUser")).thenReturn(null);
      when(userConverter.toUserEntity(newUserDTO)).thenReturn(newUserEntity);
      when(passwordEncoder.encode(SystemConstant.PASSWORD_DEFAULT)).thenReturn("encodedDefault");
      when(userRepository.save(newUserEntity)).thenReturn(newUserEntity);

      UserEntity result = userService.save(newUserDTO);

      assertThat(result.getEncrytedPassword()).isEqualTo("encodedDefault");
      assertThat(result.isActive()).isTrue();
      verify(userRepository).save(newUserEntity);
    }

    @Test
    @DisplayName("Throws exception if username exists")
    void throwsWhenUsernameExists() {
      when(userRepository.findByUserName("testUser")).thenReturn(userEntity);

      assertThatThrownBy(() -> userService.save(userDTO))
          .isInstanceOf(EntityExistsException.class)
          .hasMessageContaining("already exists");
    }
  }

  @Nested
  @DisplayName("update()")
  class UpdateTests {
    @Test
    @DisplayName("Successfully updates user")
    void updatesUser() {
      when(userRepository.findByUserName("testUser")).thenReturn(userEntity);
      when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
      when(userRepository.save(userEntity)).thenReturn(userEntity);

      userService.update(userDTO);

      verify(userConverter).updateEntity(userDTO, userEntity);
      verify(userRepository).save(userEntity);
    }

    @Test
    @DisplayName("Throws exception if username exists for different user")
    void throwsWhenUsernameExistsForOtherUser() {
      UserEntity otherUser = new UserEntity();
      otherUser.setId(2L);
      otherUser.setUserName("testUser");

      when(userRepository.findByUserName("testUser")).thenReturn(otherUser);

      assertThatThrownBy(() -> userService.update(userDTO))
          .isInstanceOf(EntityExistsException.class)
          .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("Throws exception if username is null or empty")
    void throwsWhenUsernameEmpty() {
      userDTO.setUserName("");
      assertThatThrownBy(() -> userService.update(userDTO))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Username is required");
    }
  }

  @Nested
  @DisplayName("delete()")
  class DeleteTests {
    @Test
    @DisplayName("Soft deletes users")
    void softDeletesUsers() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
      userService.delete(List.of(1L));
      assertThat(userEntity.isActive()).isFalse();
    }
  }

  @Nested
  @DisplayName("findByUserName()")
  class FindByUserNameTests {
    @Test
    @DisplayName("Returns user DTO")
    void returnsUserDTO() {
      when(userRepository.findByUserName("testUser")).thenReturn(userEntity);
      when(userConverter.toUserDTO(userEntity)).thenReturn(userDTO);

      UserDTO result = userService.findByUserName("testUser");
      assertThat(result).isNotNull();
      assertThat(result.getUserName()).isEqualTo("testUser");
    }

    @Test
    @DisplayName("Returns null for empty username")
    void returnsNullForEmpty() {
      assertThat(userService.findByUserName("")).isNull();
    }
  }

  @Nested
  @DisplayName("getUserByUserName()")
  class GetUserByUserNameTests {
    @Test
    @DisplayName("Returns user Entity")
    void returnsUserEntity() {
      when(userRepository.findByUserName("testUser")).thenReturn(userEntity);
      UserEntity result = userService.getUserByUserName("testUser");
      assertThat(result).isNotNull();
      assertThat(result.getUserName()).isEqualTo("testUser");
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {
    @Test
    @DisplayName("Returns user DTO")
    void returnsUserDTO() {
      when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
      when(userConverter.toUserDTO(userEntity)).thenReturn(userDTO);

      UserDTO result = userService.findById(1L);
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Returns null for null ID")
    void returnsNullForNullId() {
      assertThat(userService.findById(null)).isNull();
    }
  }

  @Nested
  @DisplayName("getImage()")
  class GetImageTests {
    @Test
    @DisplayName("Returns image bytes")
    void returnsImageBytes() {
      byte[] img = new byte[] { 1, 2, 3 };
      userEntity.setImage(img);
      when(userRepository.findByUserName("testUser")).thenReturn(userEntity);

      byte[] result = userService.getImage("testUser");
      assertThat(result).isEqualTo(img);
    }

    @Test
    @DisplayName("Returns null for empty username")
    void returnsNullForEmpty() {
      assertThat(userService.getImage("")).isNull();
    }
  }
}
