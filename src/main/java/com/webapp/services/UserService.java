package com.webapp.services;

import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.UserDTO;
import com.webapp.pagination.PaginationResult;

import java.util.List;
import java.util.Map;

public interface UserService {
  Map<Long, String> getAllStaff();

  PaginationResult<UserEntity> listUserInfo(String key, int page, int maxResult, int maxNavigationPage);

  UserEntity save(UserDTO userDTO);

  UserEntity update(UserDTO userDTO);

  void delete(List<Long> ids);

  UserDTO findByUserName(String userName);

  UserEntity getUserByUserName(String userName);

  UserDTO findById(Long id);

  byte[] getImage(String userName);
}
