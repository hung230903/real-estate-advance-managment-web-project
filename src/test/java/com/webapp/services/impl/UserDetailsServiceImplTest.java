package com.webapp.services.impl;

import com.webapp.entities.UserEntity;
import com.webapp.repositories.AccountRepository;
import com.webapp.repositories.UserRepository;
import com.webapp.security.MyUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Unit Tests")
class UserDetailsServiceImplTest {

  @Mock
  private AccountRepository accountRepository; // Not used in impl but injected

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsService;

  private UserEntity userEntity;

  @BeforeEach
  void setUp() {
    userEntity = new UserEntity();
    userEntity.setId(1L);
    userEntity.setUserName("testUser");
    userEntity.setEncrytedPassword("encryptedPassword");
    userEntity.setUserRole("ADMIN");
    userEntity.setFullName("Test User");
    userEntity.setActive(true);
  }

  @Test
  @DisplayName("Should load user details when username exists")
  void loadUserByUsername_existingUser_returnsUserDetails() {
    when(userRepository.findByUserName("testUser")).thenReturn(userEntity);

    UserDetails result = userDetailsService.loadUserByUsername("testUser");

    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo("testUser");
    assertThat(result.getPassword()).isEqualTo("encryptedPassword");
    assertThat(result.getAuthorities()).hasSize(1);
    assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");

    assertThat(result).isInstanceOf(MyUser.class);
    MyUser myUser = (MyUser) result;
    assertThat(myUser.getId()).isEqualTo(1L);
    assertThat(myUser.getFullName()).isEqualTo("Test User");
    assertThat(myUser.getRole()).isEqualTo("ROLE_ADMIN");
    assertThat(myUser.isEnabled()).isTrue();
  }

  @Test
  @DisplayName("Should throw exception when username does not exist")
  void loadUserByUsername_nonExistingUser_throwsException() {
    when(userRepository.findByUserName("unknown")).thenReturn(null);

    assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining("was not found in the database");
  }

  @Test
  @DisplayName("Should handle role with existing ROLE_ prefix")
  void loadUserByUsername_roleWithPrefix_doesNotDuplicatePrefix() {
    userEntity.setUserRole("ROLE_MANAGER");
    when(userRepository.findByUserName("testUser")).thenReturn(userEntity);

    UserDetails result = userDetailsService.loadUserByUsername("testUser");

    assertThat(result.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_MANAGER");
  }
}
