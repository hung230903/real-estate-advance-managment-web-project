package com.webapp.repositories;

import com.webapp.entities.UserEntity;
import com.webapp.repositories.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {
    List<UserEntity> findByActiveAndUserRole(boolean active, String userRole);

    UserEntity findByUserName(String userName);
}
