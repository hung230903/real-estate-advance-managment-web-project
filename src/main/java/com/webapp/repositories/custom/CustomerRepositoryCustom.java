package com.webapp.repositories.custom;

import com.webapp.entities.CustomerEntity;
import com.webapp.models.request.CustomerSearchRequest;

import java.util.List;

public interface CustomerRepositoryCustom {
  List<CustomerEntity> searchCustomers(CustomerSearchRequest searchRequest, int page, int maxResult);

  int countAll(CustomerSearchRequest searchRequest);
}
