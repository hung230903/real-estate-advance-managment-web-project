package com.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {

    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_EMPLOYEE = "EMPLOYEE";
    public static final String ROLE_USER = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(name = "username", length = 20, nullable = false)
    private String userName;

    @Column(name = "password", length = 128, nullable = false)
    private String encrytedPassword;

    @Column(name = "active", length = 1, nullable = false)
    private boolean active;

    @Column(name = "userrole", length = 20, nullable = false)
    private String userRole;

    @Column(name = "fullname", length = 250, nullable = false)
    private String fullName;

    @Column(name = "phone", length = 10, nullable = false)
    private String phone;

    @Lob
    @Column(name = "image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

    public UserEntity(Long id, String userName, boolean active, String userRole, String fullName, String phone) {
        this.id = id;
        this.userName = userName;
        this.active = active;
        this.userRole = userRole;
        this.fullName = fullName;
        this.phone = phone;
    }

    @ManyToMany(mappedBy = "userEntities", fetch = FetchType.LAZY)
    List<BuildingEntity> buildingEntities = new ArrayList<>();
}
