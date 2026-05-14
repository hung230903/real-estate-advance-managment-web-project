package com.webapp.services;

import com.webapp.models.dtos.AssignmentCustomerDTO;
import com.webapp.models.dtos.CustomerDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.request.CustomerSearchRequest;
import com.webapp.pagination.PaginationResult;

import java.util.List;

public interface CustomerService {
    PaginationResult<CustomerDTO> getCustomers(CustomerSearchRequest searchRequest, int page, int maxResult, int maxNavigationPage);

    CustomerDTO findById(Long id);

    void saveOrUpdate(CustomerDTO customerDTO);

    void deleteCustomers(List<Long> ids);

    ResponseDTO loadStaffsByCustomerId(Long customerId);

    void updateAssignmentCustomer(AssignmentCustomerDTO assignmentCustomerDTO);
}
