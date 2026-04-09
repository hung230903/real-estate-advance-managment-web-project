package com.webapp.converter;

import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.utils.ModelMapperUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BuildingRequestConverter {
    public BuildingSearchRequestDTO toBuildingBuilderDTO(Map<String, String> params, List<String> typeCode) {
        return BuildingSearchRequestDTO.builder()
                .name(ModelMapperUtils.getObject(params, "name", String.class))
                .floorArea(ModelMapperUtils.getObject(params, "floorArea", Long.class))
                .numberOfBasement(ModelMapperUtils.getObject(params, "numberOfBasement", Long.class))
                .ward(ModelMapperUtils.getObject(params, "ward", String.class))
                .district(ModelMapperUtils.getObject(params, "district", String.class))
                .street(ModelMapperUtils.getObject(params, "street", String.class))
                .managerName(ModelMapperUtils.getObject(params, "managerName", String.class))
                .managerPhone(ModelMapperUtils.getObject(params, "managerPhone", String.class))
                .direction(ModelMapperUtils.getObject(params, "direction", String.class))
                .level(ModelMapperUtils.getObject(params, "level", String.class))
                .rentPriceFrom(ModelMapperUtils.getObject(params, "rentPriceFrom", Long.class))
                .rentPriceTo(ModelMapperUtils.getObject(params, "rentPriceTo", Long.class))
                .rentAreaFrom(ModelMapperUtils.getObject(params, "rentAreaFrom", Long.class))
                .rentAreaTo(ModelMapperUtils.getObject(params, "rentAreaTo", Long.class))
                .staffId(ModelMapperUtils.getObject(params, "staffId", Long.class))
                .typeCode(typeCode)
                .build();
    }
}

