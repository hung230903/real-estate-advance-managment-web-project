package com.webapp.repositories.impl;

import com.webapp.entities.UserEntity;
import com.webapp.repositories.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<UserEntity> findUsers(String key, int page, int maxResult) {
        StringBuilder sql = new StringBuilder("SELECT NEW " + UserEntity.class.getName() + "(u.id, u.userName, u.active, u.userRole, u.fullName, u.phone) " + "FROM " + UserEntity.class.getName() + " u ");
        sql.append("WHERE u.active = true ");

        if (key != null && !key.trim().isEmpty()) {
            sql.append("AND (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
        }

        sql.append("ORDER BY u.userName DESC");

        TypedQuery<UserEntity> query = entityManager.createQuery(sql.toString(), UserEntity.class);

        if (key != null && !key.trim().isEmpty()) {
            String searchKey = "%" + key.toLowerCase() + "%";
            query.setParameter("key", searchKey);
        }

        if (page > 0 && maxResult > 0) {
            query.setFirstResult((page - 1) * maxResult);
            query.setMaxResults(maxResult);
        }

        return query.getResultList();
    }

    @Override
    public int countUsers(String key) {
        StringBuilder countSql = new StringBuilder("SELECT COUNT(u.id) FROM " + UserEntity.class.getName() + " u ");
        countSql.append("WHERE u.active = true ");

        if (key != null && !key.trim().isEmpty()) {
            countSql.append("AND (LOWER(u.userName) LIKE :key OR LOWER(u.fullName) LIKE :key OR LOWER(u.phone) LIKE :key) ");
        }

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql.toString(), Long.class);

        if (key != null && !key.trim().isEmpty()) {
            String searchKey = "%" + key.toLowerCase() + "%";
            countQuery.setParameter("key", searchKey);
        }

        return countQuery.getSingleResult().intValue();
    }
}
