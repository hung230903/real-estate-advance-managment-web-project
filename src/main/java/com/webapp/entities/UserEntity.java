package com.webapp.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    public static final String ROLE_EMPLOYEE = "STAFF";
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

    @OneToMany(mappedBy = "staff")
    List<AssignmentBuildingEntity> buildingAssignments;


}
