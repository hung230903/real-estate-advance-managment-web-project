package com.webapp.converter;

import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.enums.District;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BuildingConverter {
    private final ModelMapper modelMapper;

    private final RentAreaConverter rentAreaConverter;

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
        buildingEntity.setTypeCode(joinTypeCodes(buildingDTO.getTypeCode()));

        // Rent area
        buildingEntity.setRentAreaEntities(rentAreaConverter.toRentAreaEntities(buildingDTO, buildingEntity));

        byte[] image = extractImage(buildingDTO);
        if (image != null && image.length > 0) {
            buildingEntity.setImage(image);
        }

        return buildingEntity;
    }

    private String joinTypeCodes(List<String> typeCodes) {
        if (typeCodes == null) return "";
        return typeCodes.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public void updateEntity(BuildingDTO buildingDTO, BuildingEntity buildingEntity) {
        modelMapper.map(buildingDTO, buildingEntity);

        // Type code
        if (buildingDTO.getTypeCode() != null) {
            buildingEntity.setTypeCode(joinTypeCodes(buildingDTO.getTypeCode()));
        }

        byte[] image = extractImage(buildingDTO);
        if (image != null && image.length > 0) {
            buildingEntity.setImage(image);
        }
    }

    private byte[] extractImage(BuildingDTO buildingDTO) {
        try {
            if (buildingDTO.getFileData() != null && !buildingDTO.getFileData().isEmpty()) {
                return buildingDTO.getFileData().getBytes();
            }

            if (buildingDTO.getBase64Image() != null && !buildingDTO.getBase64Image().isEmpty()) {
                String base64 = buildingDTO.getBase64Image();
                if (base64.contains(",")) {
                    base64 = base64.split(",")[1];
                }
                return Base64.getDecoder().decode(base64);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid image data", e);
        }

        return new byte[0];
    }

    public BuildingDTO toBuildingDTO(BuildingEntity buildingEntity) {
        BuildingDTO buildingDTO = modelMapper.map(buildingEntity, BuildingDTO.class);
        buildingDTO.setAddress(buildingEntity.getStreet() + ", " + buildingEntity.getWard() + ", " + buildingEntity.getDistrict());
        List<RentAreaEntity> rentAreas = buildingEntity.getRentAreaEntities();
        if (buildingEntity.getTypeCode() != null) {
            buildingDTO.setTypeCode(java.util.Arrays.stream(buildingEntity.getTypeCode().split(","))
                    .map(String::trim)
                    .toList());
        }
        if (rentAreas != null) {
            String rentArea = rentAreas.stream().map(it -> it.getValue().toString()).collect(Collectors.joining(", "));
            buildingDTO.setRentArea(rentArea);
        }
        return buildingDTO;
    }
}
