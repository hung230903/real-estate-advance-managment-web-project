package com.webapp.services.impl;

import com.webapp.constant.SystemConstant;
import com.webapp.converter.UserConverter;
import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.UserDTO;
import com.webapp.pagination.PaginationResult;
import com.webapp.repositories.UserRepository;
import com.webapp.services.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserConverter userConverter;


    @Override
    public Map<Long, String> getAllStaff() {
        List<UserEntity> allStaff = userRepository.findByActiveAndUserRole(true,
                "ROLE_" + UserEntity.ROLE_EMPLOYEE);
        return allStaff.stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getUserName));
    }

    @Override
    public PaginationResult<UserEntity> listUserInfo(String key, int page, int maxResult, int maxNavigationPage) {
        StringBuilder sql = new StringBuilder("SELECT NEW " + UserEntity.class.getName() + "(u.id, u.userName, u.active, u.userRole, u.fullName, u.phone) " + "FROM " + UserEntity.class.getName() + " u ");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(u.id) FROM " + UserEntity.class.getName() + " u ");

        sql.append("WHERE u.active = true ");
        countSql.append("WHERE u.active = true ");

        if (key != null && !key.trim().isEmpty()) {
            sql.append("AND (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
            countSql.append("AND (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
        }

        sql.append("ORDER BY u.userName DESC");

        TypedQuery<UserEntity> query = entityManager.createQuery(sql.toString(), UserEntity.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        if (key != null && !key.trim().isEmpty()) {
            String searchKey = "%" + key.toLowerCase() + "%";
            query.setParameter("key", searchKey);
            countQuery.setParameter("key", searchKey);
        }
        return new PaginationResult<>(query, countQuery, page, maxResult, maxNavigationPage);
    }

    @Override
    public void save(UserDTO userDTO) {
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

        userRepository.save(userEntity);
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
