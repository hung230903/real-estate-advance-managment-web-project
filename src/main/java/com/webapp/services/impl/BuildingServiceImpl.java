package com.webapp.services.impl;

import com.webapp.converter.BuildingConverter;
import com.webapp.converter.BuildingRequestConverter;
import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.entities.UserEntity;
import com.webapp.exceptions.InvalidEntityException;
import com.webapp.models.dtos.AssignmentBuildingDTO;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.dtos.StaffResponseDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.repositories.BuildingRepository;
import com.webapp.repositories.UserRepository;
import com.webapp.services.BuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    UserRepository userRepository;

    private static List<StaffResponseDTO> getStaffResponseDTOS(List<UserEntity> staffList, Set<Long> assignedStaffIds) {
        List<StaffResponseDTO> staffResponses = new ArrayList<>();

        for (UserEntity user : staffList) {
            StaffResponseDTO staffResponseDTO = new StaffResponseDTO();
            staffResponseDTO.setId(user.getId());
            staffResponseDTO.setUserName(user.getUserName());
            staffResponseDTO.setChecked("");

            if (assignedStaffIds.contains(user.getId())) {
                staffResponseDTO.setChecked("checked");
            }

            staffResponses.add(staffResponseDTO);
        }

        return staffResponses;
    }

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
    public BuildingDTO findById(Long id) {
        BuildingEntity buildingEntity = buildingRepository.findById(id).get();
        return buildingConverter.toBuildingDTO(buildingEntity);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        buildingRepository.deleteAllById(ids);
    }

    @Override
    public BuildingEntity create(BuildingDTO buildingDTO) {
        log.info("Request to create building: {}", buildingDTO);
        BuildingEntity buildingEntity = buildingConverter.toBuildingEntity(buildingDTO);
        return buildingRepository.save(buildingEntity);
    }

    @Override
    public BuildingEntity update(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = buildingRepository.findById(buildingDTO.getId())
                .orElseThrow(() -> new RuntimeException("Building not found"));
        buildingEntity.getRentAreaEntities().clear();

        BuildingEntity updatedBuilding = buildingConverter.toBuildingEntity(buildingDTO);
        List<RentAreaEntity> newRentAreas = updatedBuilding.getRentAreaEntities();
        for (RentAreaEntity r : newRentAreas) {
            r.setBuilding(buildingEntity);
            buildingEntity.getRentAreaEntities().add(r);
        }

        return buildingRepository.save(buildingEntity);
    }

    @Override
    public ResponseDTO loadStaffsByBuildingId(Long buildingId) {
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new InvalidEntityException("Building not found"));
        // Tìm toàn bộ user là STAFF và active
        List<UserEntity> staffList = userRepository.findByActiveAndUserRole(true, "ROLE_" + UserEntity.ROLE_EMPLOYEE);
        // Tìm toàn bộ staff đã được gán các building trong bảng assignmentbuilding
        Set<Long> assignedStaffIds = building.getUserEntities()
                .stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet());
        //
        List<StaffResponseDTO> staffResponses = getStaffResponseDTOS(staffList, assignedStaffIds);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(staffResponses);
        responseDTO.setMessage("Load Staffs successfully");
        return responseDTO;
    }

    @Override
    public ResponseDTO updateAssignmentBuilding(AssignmentBuildingDTO assignmentBuildingDTO) {
        BuildingEntity building = buildingRepository.findById(assignmentBuildingDTO.getBuildingId())
                .orElseThrow(() -> new InvalidEntityException("Building not found"));

        building.getUserEntities().clear();
        for (Long staffId : assignmentBuildingDTO.getStaffIds()) {
            UserEntity staff = userRepository.getReferenceById(staffId);
            building.getUserEntities().add(staff);
        }

        buildingRepository.save(building);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Assign success");
        return responseDTO;
    }
}
