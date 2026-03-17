package com.webapp.api;

import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.services.BuildingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/buildings")
@Slf4j
public class BuildingAPI {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private ResponseDTO responseDTO;

    @GetMapping()
    public List<BuildingSearchResponseDTO> getBuilding(@RequestParam Map<String, String> params,
                                                       @RequestParam(name = "typeCode", required = false) List<String> typeCode) {
        log.info("API: Received request to fetch buildings.");
        return buildingService.findAll(params, typeCode);
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<ResponseDTO> deleteBuildings(@PathVariable List<Long> ids) {
        log.info("API: Received request to delete buildings with IDs: {}", ids);
        buildingService.deleteAllById(ids);
        responseDTO.setMessage("Successfully deleted buildings");
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO> create(@RequestBody BuildingDTO buildingDTO) {
        log.info("API: Received request to create building: {}", buildingDTO);
        buildingService.create(buildingDTO);
        responseDTO.setMessage("Created building successfully!!!");
        responseDTO.setData(buildingDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping()
    public ResponseEntity<ResponseDTO> update(@RequestBody BuildingDTO buildingDTO) {
        log.info("API: Received request to update building: {}", buildingDTO);
        buildingService.update(buildingDTO);
        responseDTO.setMessage("Updated building successfully!!!");
        responseDTO.setData(buildingDTO);
        return ResponseEntity.ok(responseDTO);
    }
}

