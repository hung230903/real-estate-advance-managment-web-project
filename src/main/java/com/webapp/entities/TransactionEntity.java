package com.webapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class TransactionEntity extends BaseEntity {

  @Column(name = "code")
  private String code;

  @Column(name = "note")
  private String note;

  @Column(name = "is_active")
  private Integer isActive = 1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "customerid")
  private CustomerEntity customer;
}
