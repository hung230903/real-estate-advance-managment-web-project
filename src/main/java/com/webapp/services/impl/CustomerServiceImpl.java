package com.webapp.services.impl;

import com.webapp.constant.SystemConstant;
import com.webapp.converter.CustomerConverter;
import com.webapp.converter.UserConverter;
import com.webapp.entities.CustomerEntity;
import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.AssignmentCustomerDTO;
import com.webapp.models.dtos.CustomerDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.dtos.StaffResponseDTO;
import com.webapp.models.request.CustomerSearchRequest;
import com.webapp.pagination.PaginationResult;
import com.webapp.repositories.CustomerRepository;
import com.webapp.repositories.UserRepository;
import com.webapp.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerConverter customerConverter;
    private final UserConverter userConverter;
    private final UserRepository userRepository;

    @Override
    public PaginationResult<CustomerDTO> getCustomers(CustomerSearchRequest searchRequest, int page, int maxResult, int maxNavigationPage) {
        List<CustomerEntity> entities = customerRepository.searchCustomers(searchRequest, page, maxResult);
        int totalItems = customerRepository.countAll(searchRequest);

        List<CustomerDTO> dtos = entities.stream()
                .map(customerConverter::toCustomerDTO)
                .toList();

        return new PaginationResult<>(dtos, totalItems, page, maxResult, maxNavigationPage);
    }


    @Override
    @Transactional(readOnly = true)
    public CustomerDTO findById(Long id) {
        CustomerEntity entity = customerRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Customer not found"));
        return customerConverter.toCustomerDTO(entity);
    }

    @Override
    @Transactional
    public void saveOrUpdate(CustomerDTO customerDTO) {
        CustomerEntity entity;
        if (customerDTO.getId() != null) {
            entity = customerRepository.findById(customerDTO.getId()).orElse(new CustomerEntity());
            customerConverter.updateEntity(customerDTO, entity);
        } else {
            entity = customerConverter.toCustomerEntity(customerDTO);
            entity.setIsActive(1);
        }
        customerRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteCustomers(List<Long> ids) {
        for (Long id : ids) {
            CustomerEntity entity = customerRepository.findById(id).orElseThrow(()
                    -> new RuntimeException("Customer not found"));
            entity.setIsActive(0);
            customerRepository.save(entity);
        }
    }


    @Override
    public ResponseDTO loadStaffsByCustomerId(Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<UserEntity> staffList = userRepository.findByActiveAndUserRole(true, SystemConstant.STAFF_ROLE);
        Set<Long> assignedStaffIds = customer.getUserEntities().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());

        List<StaffResponseDTO> staffResponses = staffList.stream()
                .map(user -> userConverter.toStaffResponseDTO(user, assignedStaffIds))
                .toList();

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(staffResponses);
        responseDTO.setMessage("Load Staffs successfully");
        return responseDTO;
    }

    @Override
    @Transactional
    public void updateAssignmentCustomer(AssignmentCustomerDTO assignmentCustomerDTO) {
        CustomerEntity customer = customerRepository.findById(assignmentCustomerDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<UserEntity> staffs = userRepository.findAllById(assignmentCustomerDTO.getStaffIds());
        customer.setUserEntities(staffs);
        customerRepository.save(customer);
    }
}
