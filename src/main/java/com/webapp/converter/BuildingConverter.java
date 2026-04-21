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
import java.util.Base64;
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

        byte[] image = extractImage(buildingDTO);
        if (image != null && image.length > 0) {
            buildingEntity.setImage(image);
        }

        return buildingEntity;
    }

    public void updateEntity(BuildingDTO buildingDTO, BuildingEntity buildingEntity) {
        modelMapper.map(buildingDTO, buildingEntity);

        // Type code
        if (buildingDTO.getTypeCode() != null) {
            String typeCode = buildingDTO.getTypeCode().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            buildingEntity.setTypeCode(typeCode);
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
        return null;
    }

    public BuildingDTO toBuildingDTO(BuildingEntity buildingEntity) {
        BuildingDTO buildingDTO = modelMapper.map(buildingEntity, BuildingDTO.class);
        buildingDTO.setAddress(buildingEntity.getStreet() + ", " + buildingEntity.getWard() + ", " + buildingEntity.getDistrict());
        List<RentAreaEntity> rentAreas = buildingEntity.getRentAreaEntities();
        List<String> typeCodes = new ArrayList<>();
        if (buildingEntity.getTypeCode() != null) {
            String[] typeCodeArray = buildingEntity.getTypeCode().split(",");
            for (String s : typeCodeArray) {
                typeCodes.add(s.trim());
            }
        }
        buildingDTO.setTypeCode(typeCodes);
        if (rentAreas != null) {
            String rentArea = rentAreas.stream().map(it -> it.getValue().toString()).collect(Collectors.joining(", "));
            buildingDTO.setRentArea(rentArea);
        }
        return buildingDTO;
    }
}
