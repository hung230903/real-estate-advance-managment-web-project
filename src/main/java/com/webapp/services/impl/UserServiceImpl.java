package com.webapp.services.impl;

import com.webapp.constant.SystemConstant;
import com.webapp.converter.UserConverter;
import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.UserDTO;
import com.webapp.pagination.PaginationResult;
import com.webapp.repositories.UserRepository;
import com.webapp.services.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserConverter userConverter;

    @Override
    public Map<Long, String> getAllStaff() {
        List<UserEntity> allStaff = userRepository.findByActiveAndUserRole(true,
                "ROLE_" + UserEntity.ROLE_EMPLOYEE);
        return allStaff.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getUserName));
    }

    @Override
    public PaginationResult<UserEntity> listUserInfo(String key, int page, int maxResult, int maxNavigationPage) {
        // 1. Đếm số bản ghi
        int totalRecords = userRepository.countUsers(key);

        // 2. Tính toán số trang và gán số trang (clamp)
        int totalPages = (int) Math.ceil((double) totalRecords / maxResult);
        int actualPage = (page > 1 && page > totalPages) ? 1 : Math.max(page, 1);

        // 3. Lấy dữ liệu phân trang
        List<UserEntity> userEntities = userRepository.findUsers(key, actualPage, maxResult);

        return new PaginationResult<>(userEntities, totalRecords, actualPage, maxResult, maxNavigationPage);
    }

    @Override
    public UserEntity save(UserDTO userDTO) {
        String userName = userDTO.getUserName();

        if (userName != null && !userName.isEmpty()) {
            UserEntity existingUser = userRepository.findByUserName(userName);
            if (existingUser != null) {
                throw new EntityExistsException("UserEntity with name " + userName + " already exists");
            }
        }

        UserEntity userEntity = userConverter.toUserEntity(userDTO);

        if (userEntity.getEncrytedPassword() == null || userEntity.getEncrytedPassword().isEmpty()) {
            userEntity.setEncrytedPassword(
                    passwordEncoder.encode(SystemConstant.PASSWORD_DEFAULT)
            );
        }

        if (userDTO.getStatus() != null) {
            userEntity.setActive(userDTO.getStatus() == 1);
        } else {
            userEntity.setActive(true);
        }

        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity update(UserDTO userDTO) {
        String userName = userDTO.getUserName();
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        UserEntity existingUserWithSameName = userRepository.findByUserName(userName);
        if (existingUserWithSameName != null && !existingUserWithSameName.getId().equals(userDTO.getId())) {
            throw new EntityExistsException("UserEntity with name " + userName + " already exists");
        }

        UserEntity userEntity = userRepository.findById(userDTO.getId()).orElseThrow(
                () -> new RuntimeException("User not found"));

        userConverter.updateEntity(userDTO, userEntity);
        return userRepository.save(userEntity);
    }

    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            Optional<UserEntity> userEntity = userRepository.findById(id);
            userEntity.ifPresent(value -> value.setActive(false));
        }
    }

    @Override
    public UserDTO findByUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) return null;
        UserEntity entity = userRepository.findByUserName(userName);
        return userConverter.toUserDTO(entity);
    }

    @Override
    public UserDTO findById(Long id) {
        if (id == null) return null;
        UserEntity entity = userRepository.findById(id).orElse(null);
        return userConverter.toUserDTO(entity);
    }

    @Override
    public byte[] getImage(String userName) {
        if (userName == null || userName.trim().isEmpty()) return null;
        UserEntity entity = userRepository.findByUserName(userName);
        return (entity != null) ? entity.getImage() : null;
    }
}
