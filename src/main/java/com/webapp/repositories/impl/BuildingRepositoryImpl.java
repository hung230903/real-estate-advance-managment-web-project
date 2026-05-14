package com.webapp.repositories.impl;

import com.webapp.entities.BuildingEntity;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.repositories.custom.BuildingRepositoryCustom;
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
public class BuildingRepositoryImpl implements BuildingRepositoryCustom {

    private final EntityManager entityManager;

    private void buildJoin(BuildingSearchRequestDTO buildingSearchRequestDTO, StringBuilder sql) {
        Long staffId = buildingSearchRequestDTO.getStaffId();
        if (staffId != null) {
            sql.append(" JOIN assignmentbuilding ab ON b.id = ab.buildingid ");
        }
    }

    public void buildWhere(StringBuilder sql, BuildingSearchRequestDTO buildingSearchRequestDTO) {
        try {
            Field[] fields = BuildingSearchRequestDTO.class.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                field.setAccessible(true);
                if (!fieldName.equals("staffId")
                        && !fieldName.equals("typeCode")
                        && !fieldName.startsWith("rentArea")
                        && !fieldName.contains("rentPrice")
                ) {
                    Object value = field.get(buildingSearchRequestDTO);
                    if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
                        if (value.toString().matches("^\\d+(\\.\\d+)?$")) {
                            sql.append(" AND b.").append(fieldName.toLowerCase()).append(" = ").append(value);
                        } else {
                            sql.append(" AND b.").append(fieldName.toLowerCase()).append(" LIKE '%").append(value).append("%'");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Reflection error in buildWhere: ", e);
        }

        // Handle special cases
        Long staffId = buildingSearchRequestDTO.getStaffId();
        if (staffId != null) {
            sql.append(" AND ab.staffid = ").append(staffId);
        }

        // rentArea
        Long rentAreaFrom = buildingSearchRequestDTO.getRentAreaFrom();
        Long rentAreaTo = buildingSearchRequestDTO.getRentAreaTo();
        if (rentAreaFrom != null || rentAreaTo != null) {
            sql.append(" AND EXISTS (SELECT * FROM rentarea ra WHERE b.id = ra.buildingid ");
            if (rentAreaFrom != null) {
                sql.append(" AND ra.value >= ").append(rentAreaFrom);
            }
            if (rentAreaTo != null) {
                sql.append(" AND ra.value <= ").append(rentAreaTo);
            }
            sql.append(") ");
        }

        // rentPrice
        Long rentPriceFrom = buildingSearchRequestDTO.getRentPriceFrom();
        Long rentPriceTo = buildingSearchRequestDTO.getRentPriceTo();
        if (rentPriceFrom != null) {
            sql.append(" AND b.rentprice >= ").append(rentPriceFrom);
        }
        if (rentPriceTo != null) {
            sql.append(" AND b.rentprice <= ").append(rentPriceTo);
        }

        // typeCode
        List<String> typeCode = buildingSearchRequestDTO.getTypeCode();
        if (typeCode != null && !typeCode.isEmpty()) {
            sql.append(" AND b.type REGEXP '").append(String.join("|", typeCode)).append("' ");
        }
    }


    @Override
    public List<BuildingEntity> searchBuildings(BuildingSearchRequestDTO buildingSearchRequestDTO, int page, int maxResult) {
        StringBuilder sql = new StringBuilder("SELECT b.* FROM building b");
        buildJoin(buildingSearchRequestDTO, sql);
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        buildWhere(whereSql, buildingSearchRequestDTO);
        sql.append(whereSql);
        sql.append(" GROUP BY b.id ");
        sql.append(" ORDER BY b.name ASC ");
        
        // Add Pagination
        if (page > 0 && maxResult > 0) {
            sql.append(" LIMIT ").append(maxResult).append(" OFFSET ").append((page - 1) * maxResult);
        }

        log.info("Final SQL Query: {}", sql.toString());

        try {
            Query queryObject = entityManager.createNativeQuery(sql.toString(), BuildingEntity.class);
            return queryObject.getResultList();
        } catch (Exception e) {
            log.error("Error when executing search query: ", e);
            throw e;
        }
    }

    @Override
    public int countAll(BuildingSearchRequestDTO buildingSearchRequestDTO) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT b.id) FROM building b");
        buildJoin(buildingSearchRequestDTO, sql);
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        buildWhere(whereSql, buildingSearchRequestDTO);
        sql.append(whereSql);

        log.info("Final Count SQL Query: {}", sql.toString());

        try {
            Query queryObject = entityManager.createNativeQuery(sql.toString());
            return ((Number) queryObject.getSingleResult()).intValue();
        } catch (Exception e) {
            log.error("Error when executing count query: ", e);
            throw e;
        }
    }

}
