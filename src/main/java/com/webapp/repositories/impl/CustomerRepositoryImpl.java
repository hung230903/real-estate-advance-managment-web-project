package com.webapp.repositories.impl;

import com.webapp.entities.CustomerEntity;
import com.webapp.models.request.CustomerSearchRequest;
import com.webapp.repositories.custom.CustomerRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomerRepositoryImpl implements CustomerRepositoryCustom {

  private final EntityManager entityManager;

  private void buildJoin(CustomerSearchRequest searchRequest, StringBuilder sql) {
    Long staffId = searchRequest.getStaffId();
    if (staffId != null) {
      sql.append(" JOIN assignmentcustomer ac ON c.id = ac.customerid ");
    }
  }

  private void buildWhere(StringBuilder sql, CustomerSearchRequest searchRequest) {
    try {
      Field[] fields = CustomerSearchRequest.class.getDeclaredFields();
      for (Field field : fields) {
        String fieldName = field.getName();
        field.setAccessible(true);
        if (!fieldName.equals("staffId")) {
          Object value = field.get(searchRequest);
          if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
            sql.append(" AND c.").append(fieldName.toLowerCase()).append(" LIKE '%").append(value).append("%'");
          }
        }
      }
    } catch (Exception e) {
      log.error("Reflection error in buildWhere: ", e);
    }

    Long staffId = searchRequest.getStaffId();
    if (staffId != null) {
      sql.append(" AND ac.staffid = ").append(staffId);
    }

    // Only search for active customers
    sql.append(" AND c.is_active = 1 ");
  }

  @Override
  public List<CustomerEntity> searchCustomers(CustomerSearchRequest searchRequest, int page, int maxResult) {
    StringBuilder sql = new StringBuilder("SELECT c.* FROM customer c");
    buildJoin(searchRequest, sql);
    StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
    buildWhere(whereSql, searchRequest);
    sql.append(whereSql);
    sql.append(" GROUP BY c.id ");
    sql.append(" ORDER BY c.createddate DESC ");

    if (page > 0 && maxResult > 0) {
      sql.append(" LIMIT ").append(maxResult).append(" OFFSET ").append((page - 1) * maxResult);
    }

    log.info("Customer Query: {}", sql.toString());

    try {
      Query queryObject = entityManager.createNativeQuery(sql.toString(), CustomerEntity.class);
      return queryObject.getResultList();
    } catch (Exception e) {
      log.error("Error in searchCustomers: ", e);
      throw e;
    }
  }

  @Override
  public int countAll(CustomerSearchRequest searchRequest) {
    StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT c.id) FROM customer c");
    buildJoin(searchRequest, sql);
    StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
    buildWhere(whereSql, searchRequest);
    sql.append(whereSql);

    try {
      Query queryObject = entityManager.createNativeQuery(sql.toString());
      return ((Number) queryObject.getSingleResult()).intValue();
    } catch (Exception e) {
      log.error("Error in countAll: ", e);
      throw e;
    }
  }
}
