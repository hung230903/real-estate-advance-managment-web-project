package com.webapp.services;

import com.webapp.models.dtos.TransactionDTO;
import java.util.List;

public interface TransactionService {
    List<TransactionDTO> findByCustomerIdAndCode(Long customerId, String code);
    void save(TransactionDTO transactionDTO);
    TransactionDTO findById(Long id);
    void delete(Long id);
    java.util.Map<String, List<TransactionDTO>> getTransactionsByCustomerId(Long customerId);
}
