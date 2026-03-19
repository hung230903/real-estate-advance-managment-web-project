package com.webapp.repositories;

import com.webapp.entities.RentAreaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentAreaRepostiory extends JpaRepository<RentAreaEntity, Long> {
    void deleteAllByBuilding_IdIn(List<Long> buildingIds);

    void deleteByBuilding_IdIn(List<Long> buildingIds);
}
