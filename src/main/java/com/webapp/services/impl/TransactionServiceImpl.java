package com.webapp.services.impl;

import com.webapp.converter.TransactionConverter;
import com.webapp.entities.CustomerEntity;
import com.webapp.entities.TransactionEntity;
import com.webapp.models.dtos.TransactionDTO;
import com.webapp.repositories.CustomerRepository;
import com.webapp.repositories.TransactionRepository;
import com.webapp.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final TransactionConverter transactionConverter;

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> findByCustomerIdAndCode(Long customerId, String code) {
        return transactionRepository.findByCustomerIdAndCode(customerId, code).stream()
                .map(transactionConverter::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(TransactionDTO transactionDTO) {
        TransactionEntity entity;
        if (transactionDTO.getId() != null) {
            entity = transactionRepository.findById(transactionDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));
            entity.setNote(transactionDTO.getNote());
        } else {
            entity = transactionConverter.toEntity(transactionDTO);
            CustomerEntity customer = customerRepository.findById(transactionDTO.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            entity.setCustomer(customer);
        }
        transactionRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDTO findById(Long id) {
        TransactionEntity entity = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return transactionConverter.toDTO(entity);
    }
    @Override
    @Transactional
    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }
}
