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
                        && !fieldName.equals("district")) {
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
    }


    @Override
    public List<BuildingEntity> findAll(BuildingSearchRequestDTO buildingSearchRequestDTO) {
        StringBuilder sql = new StringBuilder("SELECT * FROM building b");

        // Build Where
        StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
        buildWhere(whereSql, buildingSearchRequestDTO);
        sql.append(whereSql);

        log.info("Final SQL Query: {}", sql.toString());

        try {
            Query queryObject = entityManager.createNativeQuery(sql.toString(), BuildingEntity.class);
            return queryObject.getResultList();
        } catch (Exception e) {
            log.error("Error when executing search query: ", e);
            throw e;
        }
    }
}
