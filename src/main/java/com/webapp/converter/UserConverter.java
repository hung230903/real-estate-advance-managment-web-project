package com.webapp.converter;

import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class UserConverter {


    @Autowired
    PasswordEncoder passwordEncoder;

    public UserDTO toUserDTO(UserEntity userEntity) {
        if (userEntity == null) return null;

        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setUserName(userEntity.getUserName());
        userDTO.setFullName(userEntity.getFullName());
        userDTO.setRoleCode(userEntity.getUserRole());
        userDTO.setPhone(userEntity.getPhone());
        userDTO.setStatus(userEntity.isActive() ? 1 : 0);
        userDTO.initRoles();

        return userDTO;
    }

    public UserEntity toUserEntity(UserDTO userDTO) {
        if (userDTO == null) return null;

        UserEntity userEntity = new UserEntity();
        if (userDTO.getId() != null) {
            userEntity.setId(userDTO.getId());
        }
        userEntity.setUserName(userDTO.getUserName());
        userEntity.setFullName(userDTO.getFullName());
        userEntity.setPhone(userDTO.getPhone());
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            userEntity.setEncrytedPassword(
                    passwordEncoder.encode(userDTO.getPassword())
            );
        }

        String role = (userDTO.getRoleCode() != null && !userDTO.getRoleCode().isEmpty())
                ? userDTO.getRoleCode()
                : "ROLE_" + UserEntity.ROLE_USER;

        userEntity.setUserRole(role);

        byte[] image = extractImage(userDTO);
        if (image != null && image.length > 0) {
            userEntity.setImage(image);
        }

        return userEntity;
    }

    public void updateEntity(UserDTO userDTO, UserEntity userEntity) {
        if (userDTO == null || userEntity == null) return;

        userEntity.setUserName(userDTO.getUserName());

        if (userDTO.getStatus() != null) {
            userEntity.setActive(userDTO.getStatus() == 1);
        } else {
            userEntity.setActive(true);
        }

        userEntity.setFullName(userDTO.getFullName());
        userEntity.setPhone(userDTO.getPhone());

        if (userDTO.getRoleCode() != null && !userDTO.getRoleCode().isEmpty()) {
            userEntity.setUserRole(userDTO.getRoleCode());
        }

        // chỉ update ảnh nếu có dữ liệu mới
        byte[] image = extractImage(userDTO);
        if (image != null && image.length > 0) {
            userEntity.setImage(image);
        }
    }

    private byte[] extractImage(UserDTO userDTO) {
        try {
            if (userDTO.getFileData() != null && !userDTO.getFileData().isEmpty()) {
                return userDTO.getFileData().getBytes();
            }

            if (userDTO.getBase64Image() != null && !userDTO.getBase64Image().isEmpty()) {
                String base64 = userDTO.getBase64Image();

                if (base64.contains(",")) {
                    base64 = base64.split(",")[1];
                }

                return Base64.getDecoder().decode(base64);
            }

        } catch (Exception e) {
            throw new RuntimeException("Invalid image data", e);
        }

        return null;
    }
}