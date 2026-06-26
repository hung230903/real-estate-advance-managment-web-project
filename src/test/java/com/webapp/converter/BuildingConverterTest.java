package com.webapp.converter;

import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingConverter Unit Tests")
class BuildingConverterTest {

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private RentAreaConverter rentAreaConverter;

  @InjectMocks
  private BuildingConverter buildingConverter;

  private BuildingEntity buildingEntity;
  private BuildingDTO buildingDTO;

  @BeforeEach
  void setUp() {
    buildingEntity = new BuildingEntity();
    buildingEntity.setId(1L);
    buildingEntity.setStreet("Test Street");
    buildingEntity.setDistrict("QUAN_1");
    buildingEntity.setTypeCode("TANG_TRET, NGUYEN_CAN");

    RentAreaEntity area1 = new RentAreaEntity();
    area1.setValue(100L);
    RentAreaEntity area2 = new RentAreaEntity();
    area2.setValue(200L);
    buildingEntity.setRentAreaEntities(List.of(area1, area2));

    buildingDTO = new BuildingDTO();
    buildingDTO.setId(1L);
    buildingDTO.setTypeCode(List.of("TANG_TRET", "NGUYEN_CAN"));
    buildingDTO.setRentArea("100, 200");
  }

  @Test
  @DisplayName("Should convert to BuildingSearchResponseDTO")
  void toBuildingSearchResponseDTO() {
    BuildingSearchResponseDTO mockResponse = new BuildingSearchResponseDTO();
    when(modelMapper.map(buildingEntity, BuildingSearchResponseDTO.class)).thenReturn(mockResponse);

    BuildingSearchResponseDTO result = buildingConverter.toBuildingSearchResponseDTO(buildingEntity);

    assertThat(result.getAddress()).isEqualTo("Test Street, Quan 1");
    assertThat(result.getRentArea()).isEqualTo("100, 200");
  }

  @Test
  @DisplayName("Should convert to BuildingEntity")
  void toBuildingEntity() {
    BuildingEntity mockEntity = new BuildingEntity();
    when(modelMapper.map(buildingDTO, BuildingEntity.class)).thenReturn(mockEntity);

    RentAreaEntity area = new RentAreaEntity();
    when(rentAreaConverter.toRentAreaEntities(eq(buildingDTO), any(BuildingEntity.class))).thenReturn(List.of(area));

    BuildingEntity result = buildingConverter.toBuildingEntity(buildingDTO);

    assertThat(result.getTypeCode()).isEqualTo("TANG_TRET, NGUYEN_CAN");
    verify(rentAreaConverter).toRentAreaEntities(eq(buildingDTO), any());
  }

  @Test
  @DisplayName("Should convert to BuildingDTO")
  void toBuildingDTO() {
    BuildingDTO mockDTO = new BuildingDTO();
    when(modelMapper.map(buildingEntity, BuildingDTO.class)).thenReturn(mockDTO);
    BuildingDTO result = buildingConverter.toBuildingDTO(buildingEntity);

    assertThat(result.getRentArea()).isEqualTo("100, 200");
    assertThat(result.getTypeCode()).containsExactly("TANG_TRET", "NGUYEN_CAN");
    assertThat(result.getAddress()).contains("Test Street");
  }
}
