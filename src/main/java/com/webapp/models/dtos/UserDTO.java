package com.webapp.models.dtos;

import com.webapp.constant.SystemConstant;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends AbstractDTO {
    @NotBlank(message = "UserName is required")
    private String userName;
    @NotBlank(message = "FullName is required")
    private String fullName;
    @Size(min = 3)
    private String password;
    private Integer status;
    private MultipartFile fileData;
    private Map<String, String> roleDTO;
    private String roleCode;
    private String phone;

    private String base64Image;
    private String imageName;

    public void initRoles() {
        this.roleDTO = new HashMap<>();
        this.roleDTO.put(SystemConstant.MANAGER_ROLE, "Manager");
        this.roleDTO.put(SystemConstant.STAFF_ROLE, "Staff");
        this.roleDTO.put(SystemConstant.USER_ROLE, "User");
    }
}
