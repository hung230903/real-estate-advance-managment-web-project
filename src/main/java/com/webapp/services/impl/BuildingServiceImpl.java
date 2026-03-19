package com.webapp.services.impl;

import com.webapp.converter.BuildingConverter;
import com.webapp.converter.BuildingRequestConverter;
import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.repositories.AssignmentBuildingRepository;
import com.webapp.repositories.BuildingRepository;
import com.webapp.repositories.RentAreaRepostiory;
import com.webapp.services.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BuildingServiceImpl implements BuildingService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private BuildingRequestConverter buildingSearchConverter;

    @Autowired
    private BuildingConverter buildingConverter;

    @Autowired
    private RentAreaRepostiory rentAreaRepository;

    @Autowired
    private AssignmentBuildingRepository assignmentBuildingRepository;

    public List<BuildingSearchResponseDTO> findAll(Map<String, String> params, List<String> typeCode) {
        BuildingSearchRequestDTO buildingSearchRequestDTO = buildingSearchConverter.toBuildingBuilderDTO(params, typeCode);

        List<BuildingEntity> buildingEntities = buildingRepository.findAll(buildingSearchRequestDTO);

        List<BuildingSearchResponseDTO> responses = new ArrayList<>();

        for (BuildingEntity buildingEntity : buildingEntities) {
            responses.add(buildingConverter.toBuildingSearchResponseDTO(buildingEntity));
        }

        return responses;
    }

    @Override
    public void delete(List<Long> buildingIds) {
        assignmentBuildingRepository.deleteByBuilding_IdIn(buildingIds);
        rentAreaRepository.deleteByBuilding_IdIn(buildingIds);
        buildingRepository.deleteAllById(buildingIds);
    }

    @Override
    public void create(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = buildingConverter.toBuildingEntity(buildingDTO);
        buildingRepository.save(buildingEntity);

        List<RentAreaEntity> rentAreaEntities = buildingEntity.getRentAreaEntities();
        rentAreaRepository.saveAll(rentAreaEntities);
    }

    @Override
    public void update(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = buildingConverter.toBuildingEntity(buildingDTO);
        buildingRepository.save(buildingEntity);
        rentAreaRepository.deleteAllByBuilding_IdIn(Collections.singletonList(buildingDTO.getId()));
        if (buildingEntity.getRentAreaEntities() != null && !buildingEntity.getRentAreaEntities().isEmpty()) {
            rentAreaRepository.saveAll(buildingEntity.getRentAreaEntities());
        }
    }
}
