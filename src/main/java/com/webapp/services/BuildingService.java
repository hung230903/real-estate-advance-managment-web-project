package com.webapp.services;

import com.webapp.entities.BuildingEntity;
import com.webapp.models.dtos.AssignmentBuildingDTO;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.pagination.PaginationResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BuildingService {
    PaginationResult<BuildingSearchResponseDTO> searchBuildings(BuildingSearchRequestDTO searchRequest, int page, int maxResult, int maxNavigationPage);

    void deleteAllById(List<Long> ids);

    BuildingEntity create(BuildingDTO buildingDTO);

    BuildingEntity update(BuildingDTO buildingDTO);

    BuildingDTO findById(Long id);

    ResponseDTO loadStaffsByBuildingId(Long id);

    ResponseDTO updateAssignmentBuilding(AssignmentBuildingDTO assignmentBuildingDTO);

    byte[] getImage(Long id);
}
