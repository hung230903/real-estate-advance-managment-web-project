package com.webapp.repositories.impl;

import com.webapp.entities.BuildingEntity;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.repositories.custom.BuildingRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.List;

@Repository
@Slf4j
public class BuildingRepositoryImpl implements BuildingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private void buildJoin(BuildingSearchRequestDTO buildingSearchRequestDTO, StringBuilder sql) {
        // staffId
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
                    if (value != null) {
                        if (field.getType().getName().equals("java.lang.Long")
                                || field.getType().getName().equals("java.lang.Integer")) {
                            sql.append(" And b.").append(fieldName.toLowerCase()).append(" = ").append(value);
                        } else if (field.getType().getName().equals("java.lang.String")) {
                            sql.append(" AND b.").append(fieldName.toLowerCase()).append(" LIKE '%").append(value).append("%'");
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Reflection error in buildWhere: ", e);
        }


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
                sql.append(" AND ra.value >= " + rentAreaFrom);
            }
            if (rentAreaTo != null) {
                sql.append(" AND ra.value <= " + rentAreaTo);
            }
            sql.append(") ");
        }
        // rentPrice
        Long rentPriceFrom = buildingSearchRequestDTO.getRentPriceFrom();
        Long rentPriceTo = buildingSearchRequestDTO.getRentPriceTo();

        if (rentPriceFrom != null) {
            sql.append(" AND b.rentprice >= " + rentPriceFrom);
        }
        if (rentPriceTo != null) {
            sql.append(" AND b.rentprice <= " + rentPriceTo);
        }

        // typeCode: Java 8 (stream)
        List<String> typeCode = buildingSearchRequestDTO.getTypeCode();
        if (typeCode != null && !typeCode.isEmpty()) {
            sql.append(" AND b.type REGEXP '")
                    .append(String.join("|", typeCode))
                    .append("' ");
        }
    }

    @Override
    public List<BuildingEntity> findAll(BuildingSearchRequestDTO buildingSearchRequestDTO) {
        StringBuilder sql = new StringBuilder("SELECT * FROM building b");

        // Build Join
        buildJoin(buildingSearchRequestDTO, sql);

        // Build Where
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        buildWhere(whereSql, buildingSearchRequestDTO);
        sql.append(whereSql);
        sql.append(" GROUP BY b.id ");
        sql.append(" ORDER BY b.name ");

        try {
            Query queryObject = entityManager.createNativeQuery(sql.toString(), BuildingEntity.class);
            return queryObject.getResultList();
        } catch (Exception e) {
            log.error("Error when executing search query: ", e);
            throw e;
        }
    }
}
