package com.webapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "customer")
public class CustomerEntity extends BaseEntity {

  @Column(name = "fullname")
  private String fullName;

  @Column(name = "phone")
  private String phone;

  @Column(name = "email")
  private String email;

  @Column(name = "companyname")
  private String companyName;

  @Column(name = "demand")
  private String demand;

  @Column(name = "status")
  private String status;

  @Column(name = "is_active")
  private Integer isActive = 1;

  @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<TransactionEntity> transactionEntities = new ArrayList<>();

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "assignmentcustomer", joinColumns = @JoinColumn(name = "customerid", nullable = false), inverseJoinColumns = @JoinColumn(name = "staffid", nullable = false))
  private List<UserEntity> userEntities = new ArrayList<>();
}
