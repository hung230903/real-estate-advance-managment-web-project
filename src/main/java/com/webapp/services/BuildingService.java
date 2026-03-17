package com.webapp.services;

import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface BuildingService {
    List<BuildingSearchResponseDTO> findAll(Map<String, String> params, List<String> typeCode);

    void deleteAllById(List<Long> ids);

    void create(BuildingDTO buildingDTO);

    void update(BuildingDTO buildingDTO);
}
