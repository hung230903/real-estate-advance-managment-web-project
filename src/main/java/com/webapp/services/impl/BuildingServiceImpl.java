package com.webapp.services.impl;

import com.webapp.converter.BuildingConverter;
import com.webapp.converter.RentAreaConverter;
import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.entities.UserEntity;
import com.webapp.constant.SystemConstant;
import com.webapp.exceptions.InvalidEntityException;
import com.webapp.models.dtos.AssignmentBuildingDTO;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.dtos.StaffResponseDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.pagination.PaginationResult;
import com.webapp.repositories.BuildingRepository;
import com.webapp.repositories.UserRepository;
import com.webapp.services.BuildingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final BuildingConverter buildingConverter;
    private final RentAreaConverter rentAreaConverter;

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

    @Override
    public PaginationResult<BuildingSearchResponseDTO> searchBuildings(BuildingSearchRequestDTO searchRequest, int page, int maxResult, int maxNavigationPage) {
        int totalRecords = buildingRepository.countAll(searchRequest);

        // Tính toán lại số trang để tránh trường hợp page truyền vào vượt quá số trang hiện có
        int totalPages = (int) Math.ceil((double) totalRecords / maxResult);
        int actualPage = (page > 1 && page > totalPages) ? 1 : Math.max(page, 1);

        List<BuildingEntity> buildingEntities = buildingRepository.searchBuildings(searchRequest, actualPage, maxResult);

        List<BuildingSearchResponseDTO> responses = new ArrayList<>();
        for (BuildingEntity buildingEntity : buildingEntities) {
            responses.add(buildingConverter.toBuildingSearchResponseDTO(buildingEntity));
        }

        return new PaginationResult<>(responses, totalRecords, page, maxResult, maxNavigationPage);
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

        buildingConverter.updateEntity(buildingDTO, buildingEntity);

        buildingEntity.getRentAreaEntities().clear();
        List<RentAreaEntity> newRentAreas = rentAreaConverter.toRentAreaEntities(buildingDTO, buildingEntity);
        buildingEntity.getRentAreaEntities().addAll(newRentAreas);

        return buildingRepository.save(buildingEntity);
    }

    @Override
    public ResponseDTO loadStaffsByBuildingId(Long buildingId) {
        BuildingEntity building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new InvalidEntityException("Building not found"));
        // Tìm toàn bộ user là STAFF và active
        List<UserEntity> staffList = userRepository.findByActiveAndUserRole(true, SystemConstant.STAFF_ROLE);
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

    @Override
    public byte[] getImage(Long id) {
        BuildingEntity buildingEntity = buildingRepository.findById(id).orElse(null);
        return (buildingEntity != null) ? buildingEntity.getImage() : null;
    }
}
