package com.webapp.services.impl;

import com.webapp.constant.SystemConstant;
import com.webapp.converter.BuildingConverter;
import com.webapp.converter.RentAreaConverter;
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
import com.webapp.pagination.PaginationResult;
import com.webapp.repositories.BuildingRepository;
import com.webapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingServiceImpl Unit Tests")
class BuildingServiceImplTest {

  @Mock
  private BuildingRepository buildingRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BuildingConverter buildingConverter;

  @Mock
  private RentAreaConverter rentAreaConverter;

  @InjectMocks
  private BuildingServiceImpl buildingService;

  private BuildingEntity buildingEntity;
  private BuildingDTO buildingDTO;
  private BuildingSearchRequestDTO searchRequest;

  @BeforeEach
  void setUp() {
    buildingEntity = new BuildingEntity();
    buildingEntity.setId(1L);
    buildingEntity.setName("Test Building");
    buildingEntity.setStreet("123 Test St");
    buildingEntity.setWard("Ward 1");
    buildingEntity.setDistrict("QUAN_1");
    buildingEntity.setRentPrice(1000.0);

    buildingDTO = new BuildingDTO();
    buildingDTO.setId(1L);
    buildingDTO.setName("Test Building");
    buildingDTO.setDistrict("QUAN_1");
    buildingDTO.setRentPrice(1000.0);
    buildingDTO.setRentArea("100, 200");
    buildingDTO.setTypeCode(List.of("TANG_TRET", "NGUYEN_CAN"));

    searchRequest = new BuildingSearchRequestDTO();
    searchRequest.setName("Test");
  }

  @Nested
  @DisplayName("searchBuildings()")
  class SearchBuildingsTests {

    @Test
    @DisplayName("Should return paginated results when buildings exist")
    void searchBuildings_withResults_returnsPaginatedResult() {
      BuildingSearchResponseDTO responseDTO = new BuildingSearchResponseDTO();
      responseDTO.setId(1L);
      responseDTO.setName("Test Building");

      when(buildingRepository.countAll(searchRequest)).thenReturn(1);
      when(buildingRepository.searchBuildings(searchRequest, 1, 3)).thenReturn(List.of(buildingEntity));
      when(buildingConverter.toBuildingSearchResponseDTO(buildingEntity)).thenReturn(responseDTO);

      PaginationResult<BuildingSearchResponseDTO> result = buildingService.searchBuildings(searchRequest, 1, 3, 3);

      assertThat(result).isNotNull();
      assertThat(result.getEntityList()).hasSize(1);
      assertThat(result.getEntityList().get(0).getName()).isEqualTo("Test Building");
      assertThat(result.getTotalRecords()).isEqualTo(1);

      verify(buildingRepository).countAll(searchRequest);
      verify(buildingRepository).searchBuildings(searchRequest, 1, 3);
      verify(buildingConverter).toBuildingSearchResponseDTO(buildingEntity);
    }

    @Test
    @DisplayName("Should return empty list when no buildings found")
    void searchBuildings_noResults_returnsEmptyList() {
      when(buildingRepository.countAll(searchRequest)).thenReturn(0);
      when(buildingRepository.searchBuildings(searchRequest, 1, 3)).thenReturn(Collections.emptyList());

      PaginationResult<BuildingSearchResponseDTO> result = buildingService.searchBuildings(searchRequest, 1, 3, 3);

      assertThat(result.getEntityList()).isEmpty();
      assertThat(result.getTotalRecords()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should reset to page 1 when page exceeds total pages")
    void searchBuildings_pageExceedsTotalPages_resetsToPage1() {
      when(buildingRepository.countAll(searchRequest)).thenReturn(3);
      when(buildingRepository.searchBuildings(eq(searchRequest), eq(1), eq(3))).thenReturn(List.of(buildingEntity));
      when(buildingConverter.toBuildingSearchResponseDTO(any())).thenReturn(new BuildingSearchResponseDTO());

      PaginationResult<BuildingSearchResponseDTO> result = buildingService.searchBuildings(searchRequest, 999, 3, 3);

      assertThat(result).isNotNull();
      verify(buildingRepository).searchBuildings(searchRequest, 1, 3);
    }
  }

  @Nested
  @DisplayName("findById()")
  class FindByIdTests {

    @Test
    @DisplayName("Should return BuildingDTO when building exists")
    void findById_existingId_returnsBuildingDTO() {
      when(buildingRepository.findById(1L)).thenReturn(Optional.of(buildingEntity));
      when(buildingConverter.toBuildingDTO(buildingEntity)).thenReturn(buildingDTO);

      BuildingDTO result = buildingService.findById(1L);

      assertThat(result).isNotNull();
      assertThat(result.getName()).isEqualTo("Test Building");
      verify(buildingRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when building not found")
    void findById_nonExistingId_throwsException() {
      when(buildingRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> buildingService.findById(999L))
          .isInstanceOf(NoSuchElementException.class);
    }
  }

  @Nested
  @DisplayName("deleteAllById()")
  class DeleteAllByIdTests {

    @Test
    @DisplayName("Should delegate deletion to repository")
    void deleteAllById_validIds_delegatesToRepository() {
      List<Long> ids = List.of(1L, 2L, 3L);

      buildingService.deleteAllById(ids);

      verify(buildingRepository).deleteAllById(ids);
    }
  }

  @Nested
  @DisplayName("create()")
  class CreateTests {

    @Test
    @DisplayName("Should create and return new building entity")
    void create_validDTO_returnsSavedEntity() {
      BuildingEntity savedEntity = new BuildingEntity();
      savedEntity.setId(1L);
      savedEntity.setName("Test Building");

      when(buildingConverter.toBuildingEntity(buildingDTO)).thenReturn(buildingEntity);
      when(buildingRepository.save(buildingEntity)).thenReturn(savedEntity);

      BuildingEntity result = buildingService.create(buildingDTO);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(1L);
      verify(buildingConverter).toBuildingEntity(buildingDTO);
      verify(buildingRepository).save(buildingEntity);
    }
  }

  @Nested
  @DisplayName("update()")
  class UpdateTests {

    @Test
    @DisplayName("Should update existing building successfully")
    void update_existingBuilding_returnsUpdatedEntity() {
      buildingEntity.setRentAreaEntities(new ArrayList<>());
      List<RentAreaEntity> newRentAreas = List.of(new RentAreaEntity());

      when(buildingRepository.findById(1L)).thenReturn(Optional.of(buildingEntity));
      doNothing().when(buildingConverter).updateEntity(buildingDTO, buildingEntity);
      when(rentAreaConverter.toRentAreaEntities(buildingDTO, buildingEntity)).thenReturn(newRentAreas);
      when(buildingRepository.save(buildingEntity)).thenReturn(buildingEntity);

      BuildingEntity result = buildingService.update(buildingDTO);

      assertThat(result).isNotNull();
      verify(buildingConverter).updateEntity(buildingDTO, buildingEntity);
      verify(rentAreaConverter).toRentAreaEntities(buildingDTO, buildingEntity);
      verify(buildingRepository).save(buildingEntity);
    }

    @Test
    @DisplayName("Should throw exception when building to update not found")
    void update_nonExistingBuilding_throwsException() {
      buildingDTO.setId(999L);
      when(buildingRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> buildingService.update(buildingDTO))
          .isInstanceOf(RuntimeException.class)
          .hasMessage("Building not found");
    }
  }

  @Nested
  @DisplayName("loadStaffsByBuildingId()")
  class LoadStaffsByBuildingIdTests {

    @Test
    @DisplayName("Should return staff list with checked status for assigned staff")
    void loadStaffs_withAssignedStaff_returnsCheckedStatus() {
      UserEntity staff1 = new UserEntity(1L, "staff1", true, SystemConstant.STAFF_ROLE, "Staff One", "0123456789");
      UserEntity staff2 = new UserEntity(2L, "staff2", true, SystemConstant.STAFF_ROLE, "Staff Two", "0987654321");

      buildingEntity.setUserEntities(List.of(staff1));

      when(buildingRepository.findById(1L)).thenReturn(Optional.of(buildingEntity));
      when(userRepository.findByActiveAndUserRole(true, SystemConstant.STAFF_ROLE))
          .thenReturn(List.of(staff1, staff2));

      ResponseDTO result = buildingService.loadStaffsByBuildingId(1L);

      assertThat(result).isNotNull();
      assertThat(result.getMessage()).isEqualTo("Load Staffs successfully");

      @SuppressWarnings("unchecked")
      List<StaffResponseDTO> staffList = (List<StaffResponseDTO>) result.getData();
      assertThat(staffList).hasSize(2);

      StaffResponseDTO assignedStaff = staffList.stream()
          .filter(s -> s.getId().equals(1L)).findFirst().orElse(null);
      assertThat(assignedStaff).isNotNull();
      assertThat(assignedStaff.getChecked()).isEqualTo("checked");

      StaffResponseDTO unassignedStaff = staffList.stream()
          .filter(s -> s.getId().equals(2L)).findFirst().orElse(null);
      assertThat(unassignedStaff).isNotNull();
      assertThat(unassignedStaff.getChecked()).isEmpty();
    }

    @Test
    @DisplayName("Should throw InvalidEntityException when building not found")
    void loadStaffs_buildingNotFound_throwsException() {
      when(buildingRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> buildingService.loadStaffsByBuildingId(999L))
          .isInstanceOf(InvalidEntityException.class)
          .hasMessage("Building not found");
    }
  }

  @Nested
  @DisplayName("updateAssignmentBuilding()")
  class UpdateAssignmentBuildingTests {

    @Test
    @DisplayName("Should assign staff to building successfully")
    void updateAssignment_validData_assignsStaff() {
      AssignmentBuildingDTO dto = new AssignmentBuildingDTO(1L, List.of(1L, 2L));

      UserEntity staff1 = new UserEntity(1L, "staff1", true, "ROLE_STAFF", "Staff 1", "0123456789");
      UserEntity staff2 = new UserEntity(2L, "staff2", true, "ROLE_STAFF", "Staff 2", "0987654321");

      when(buildingRepository.findById(1L)).thenReturn(Optional.of(buildingEntity));
      when(userRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(staff1, staff2));
      when(buildingRepository.save(buildingEntity)).thenReturn(buildingEntity);

      ResponseDTO result = buildingService.updateAssignmentBuilding(dto);

      assertThat(result.getMessage()).isEqualTo("Assign success");
      assertThat(buildingEntity.getUserEntities()).hasSize(2);
      verify(buildingRepository).save(buildingEntity);
    }

    @Test
    @DisplayName("Should throw exception when building not found for assignment")
    void updateAssignment_buildingNotFound_throwsException() {
      AssignmentBuildingDTO dto = new AssignmentBuildingDTO(999L, List.of(1L));

      when(buildingRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> buildingService.updateAssignmentBuilding(dto))
          .isInstanceOf(InvalidEntityException.class);
    }
  }

  @Nested
  @DisplayName("getImage()")
  class GetImageTests {

    @Test
    @DisplayName("Should return image bytes when building has image")
    void getImage_buildingWithImage_returnsBytes() {
      byte[] imageBytes = "test-image".getBytes();
      buildingEntity.setImage(imageBytes);
      when(buildingRepository.findById(1L)).thenReturn(Optional.of(buildingEntity));

      byte[] result = buildingService.getImage(1L);

      assertThat(result).isEqualTo(imageBytes);
    }

    @Test
    @DisplayName("Should return null when building not found")
    void getImage_buildingNotFound_returnsNull() {
      when(buildingRepository.findById(999L)).thenReturn(Optional.empty());

      byte[] result = buildingService.getImage(999L);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should return null when building has no image")
    void getImage_buildingWithoutImage_returnsNull() {
      buildingEntity.setImage(null);
      when(buildingRepository.findById(1L)).thenReturn(Optional.of(buildingEntity));

      byte[] result = buildingService.getImage(1L);

      assertThat(result).isNull();
    }
  }
}
