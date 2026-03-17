package com.webapp.services.impl;

import com.webapp.converter.BuildingConverter;
import com.webapp.converter.BuildingRequestConverter;
import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.repositories.BuildingRepository;
import com.webapp.services.BuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class BuildingServiceImpl implements BuildingService {

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    BuildingRequestConverter buildingSearchConverter;

    @Autowired
    BuildingConverter buildingConverter;

    public List<BuildingSearchResponseDTO> findAll(Map<String, String> params, List<String> typeCode) {
        log.info("Request to search buildings with params: {} and typeCode: {}", params, typeCode);
        BuildingSearchRequestDTO buildingSearchRequestDTO = buildingSearchConverter.toBuildingBuilderDTO(params, typeCode);

        List<BuildingEntity> buildingEntities = buildingRepository.findAll(buildingSearchRequestDTO);

        List<BuildingSearchResponseDTO> responses = new ArrayList<>();

        for (BuildingEntity buildingEntity : buildingEntities) {
            responses.add(buildingConverter.toBuildingSearchResponseDTO(buildingEntity));
        }


        log.info("Found {} result(s).", responses.size());
        return responses;
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        buildingRepository.deleteAllById(ids);
    }

    @Override
    public void create(BuildingDTO buildingDTO) {
        log.info("Request to create building: {}", buildingDTO);
        BuildingEntity buildingEntity = buildingConverter.toBuildingEntity(buildingDTO);
        buildingRepository.save(buildingEntity);
    }

    @Override
    public void update(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = buildingRepository.findById(buildingDTO.getId())
                .orElseThrow(() -> new RuntimeException("Building not found"));
        buildingEntity.getRentAreaEntities().clear();

        BuildingEntity updatedBuilding = buildingConverter.toBuildingEntity(buildingDTO);
        List<RentAreaEntity> newRentAreas = updatedBuilding.getRentAreaEntities();
        for (RentAreaEntity r : newRentAreas) {
            r.setBuilding(buildingEntity);
            buildingEntity.getRentAreaEntities().add(r);
        }

        buildingEntity = buildingConverter.toBuildingEntity(buildingDTO);
        buildingRepository.save(buildingEntity);
    }
}
