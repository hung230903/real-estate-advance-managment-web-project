package com.webapp.services;

import com.webapp.models.dtos.TransactionDTO;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<TransactionDTO> findByCustomerIdAndCode(Long customerId, String code);

    void save(TransactionDTO transactionDTO);

    TransactionDTO findById(Long id);

    void delete(Long id);

    Map<String, List<TransactionDTO>> getTransactionsByCustomerId(Long customerId);
}
