package com.webapp.repositories.custom;

import com.webapp.entities.BuildingEntity;
import com.webapp.models.request.BuildingSearchRequestDTO;

import java.util.List;

public interface BuildingRepositoryCustom {
    List<BuildingEntity> searchBuildings(BuildingSearchRequestDTO buildingSearchRequestDTO, int page, int maxResult);

    List<BuildingEntity> findAll(BuildingSearchRequestDTO buildingSearchRequestDTO);

    int countAll(BuildingSearchRequestDTO buildingSearchRequestDTO);
}
