package com.webapp.services.impl;

import com.webapp.entities.UserEntity;
import com.webapp.repositories.UserRepository;
import com.webapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Map<Long, String> getAllStaff() {
        List<UserEntity> allStaff = userRepository.findByActiveAndUserRole(true,
                "ROLE_" + UserEntity.ROLE_EMPLOYEE);
        return allStaff.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getUserName));
    }
}
