package com.webapp.services.impl;

import com.webapp.converter.BuildingConverter;
import com.webapp.converter.BuildingRequestConverter;
import com.webapp.entities.AssignmentBuildingEntity;
import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.entities.UserEntity;
import com.webapp.models.dtos.AssignmentBuildingDTO;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.dtos.StaffResponseDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.repositories.AssignmentBuildingRepository;
import com.webapp.repositories.BuildingRepository;
import com.webapp.repositories.RentAreaRepostiory;
import com.webapp.repositories.UserRepository;
import com.webapp.services.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private UserRepository userRepository;

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
        BuildingSearchRequestDTO buildingSearchRequestDTO = buildingSearchConverter.toBuildingBuilderDTO(params, typeCode);

        List<BuildingEntity> buildingEntities = buildingRepository.findAll(buildingSearchRequestDTO);

        List<BuildingSearchResponseDTO> responses = new ArrayList<>();

        for (BuildingEntity buildingEntity : buildingEntities) {
            responses.add(buildingConverter.toBuildingSearchResponseDTO(buildingEntity));
        }

        return responses;
    }

    @Override
    public BuildingDTO findById(Long id) {
        BuildingEntity buildingEntity = buildingRepository.findById(id).get();
        return buildingConverter.toBuildingDTO(buildingEntity);
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

    @Override
    public ResponseDTO loadStaffsByBuildingId(Long buildingId) {
        // Tìm toàn bộ user là STAFF và active
        List<UserEntity> staffList = userRepository.findByActiveAndUserRole(true, "ROLE_" + UserEntity.ROLE_EMPLOYEE);
        // Tìm toàn bộ staff đã được gán các building trong bảng assignmentbuilding
        Set<Long> assignedStaffIds = assignmentBuildingRepository.findByBuilding_Id(buildingId)
                .stream()
                .map(it -> it.getStaff().getId())
                .collect(Collectors.toSet());
        // Đánh dấu các staff được gán là "checked"ngược lại là ""
        List<StaffResponseDTO> staffResponses = getStaffResponseDTOS(staffList, assignedStaffIds);

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(staffResponses);
        responseDTO.setMessage("Load Staffs successfully");
        return responseDTO;
    }

    @Override
    public ResponseDTO updateAssignmentBuilding(AssignmentBuildingDTO assignmentBuildingDTO) {
        Long buildingId = assignmentBuildingDTO.getBuildingId();
        List<Long> staffIds = assignmentBuildingDTO.getStaffIds();
        // Xóa các record cũ có building và staff được gán
        assignmentBuildingRepository.deleteByBuilding_Id(buildingId);

        // Update các staff mới
        for (Long staffId : staffIds) {
            AssignmentBuildingEntity assignmentBuildingEntity = new AssignmentBuildingEntity();
            assignmentBuildingEntity.setBuilding(buildingRepository.getReferenceById(buildingId));
            assignmentBuildingEntity.setStaff(userRepository.getReferenceById(staffId));
            assignmentBuildingRepository.save(assignmentBuildingEntity);
        }

        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("Assign success");
        return responseDTO;
    }

}
