package com.webapp.repositories;

import com.webapp.entities.AssignmentBuildingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentBuildingRepository extends JpaRepository<AssignmentBuildingEntity, Long> {
    void deleteByBuilding_IdIn(List<Long> buildingIds);

    void deleteByBuilding_Id(Long buildingId);

    List<AssignmentBuildingEntity> findByBuilding_Id(Long buildingId);
}
