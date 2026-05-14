package com.webapp.repositories;

import com.webapp.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByCustomerIdAndCodeAndIsActive(Long customerId, String code, Integer isActive);
    List<TransactionEntity> findByCustomerIdAndIsActive(Long customerId, Integer isActive);
}
