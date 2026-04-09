package com.webapp.converter;

import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.enums.District;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BuildingConverter {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RentAreaConverter rentAreaConverter;

    public BuildingSearchResponseDTO toBuildingSearchResponseDTO(BuildingEntity buildingEntity) {
        BuildingSearchResponseDTO buildingSearchResponseDTO = modelMapper.map(buildingEntity, BuildingSearchResponseDTO.class);

        // Address
        buildingSearchResponseDTO.setAddress(buildingEntity.getStreet()
                + ", "
                + District.getDistrictName(buildingEntity.getDistrict()));

        // Rent area
        List<RentAreaEntity> rentAreas = buildingEntity.getRentAreaEntities();
        if (rentAreas != null && !rentAreas.isEmpty()) {
            String rentAreasString = rentAreas.stream()
                    .map(it -> it.getValue().toString())
                    .collect(Collectors.joining(", "));
            buildingSearchResponseDTO.setRentArea(rentAreasString);
        }
        return buildingSearchResponseDTO;
    }

    public BuildingEntity toBuildingEntity(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity = modelMapper.map(buildingDTO, BuildingEntity.class);

        // Type code
        String typeCode = buildingDTO.getTypeCode().stream()
                .map(it -> it.toString())
                .collect(Collectors.joining(", "));
        buildingEntity.setTypeCode(typeCode);

        // Rent area
        buildingEntity.setRentAreaEntities(rentAreaConverter.toRentAreaEntities(buildingDTO, buildingEntity));


        return buildingEntity;
    }

    public BuildingDTO toBuildingDTO(BuildingEntity buildingEntity) {
        BuildingDTO buildingDTO = modelMapper.map(buildingEntity, BuildingDTO.class);
        buildingDTO.setAddress(buildingEntity.getStreet() + ", " + buildingEntity.getWard() + ", " + buildingEntity.getDistrict());
        List<RentAreaEntity> rentAreas = buildingEntity.getRentAreaEntities();
        List<String> typeCodes = new ArrayList<>();
        if (buildingEntity.getTypeCode() != null) {
            String[] typeCode = buildingEntity.getTypeCode().split(",");
            typeCodes.addAll(Arrays.asList(typeCode));
        }
        buildingDTO.setTypeCode(typeCodes);
        if (rentAreas != null) {
            String rentArea = rentAreas.stream().map(it -> it.getValue().toString()).collect(Collectors.joining(", "));
            buildingDTO.setRentArea(rentArea);
        }
        return buildingDTO;
    }


}
