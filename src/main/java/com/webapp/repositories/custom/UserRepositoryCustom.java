package com.webapp.repositories.custom;

import com.webapp.entities.UserEntity;
import java.util.List;

public interface UserRepositoryCustom {
  List<UserEntity> findUsers(String key, int page, int maxResult);

  int countUsers(String key);
}
