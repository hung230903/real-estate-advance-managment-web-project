package com.webapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "createddate")
  @CreatedDate
  private Date createdDate;

  @Column(name = "createdby")
  @CreatedBy
  private String createdBy;

  @Column(name = "modifieddate")
  @LastModifiedDate
  private Date modifiedDate;

  @Column(name = "modifiedby")
  @LastModifiedBy
  private String modifiedBy;
}
