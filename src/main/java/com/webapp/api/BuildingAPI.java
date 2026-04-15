package com.webapp.api;

import com.webapp.entities.BuildingEntity;
import com.webapp.models.dtos.AssignmentBuildingDTO;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.services.BuildingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api/buildings")
@Slf4j
public class BuildingAPI {

    @Autowired
    private BuildingService buildingService;

    @GetMapping()
    public List<BuildingSearchResponseDTO> getBuilding(@RequestParam Map<String, String> params,
                                                       @RequestParam(name = "typeCode", required = false) List<String> typeCode) {
        log.info("API: Received request to fetch buildings.");
        return buildingService.findAll(params, typeCode);
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<ResponseDTO> deleteBuildings(@PathVariable List<Long> ids) {
        log.info("API: Received request to delete buildings with IDs: {}", ids);
        ResponseDTO responseDTO = new ResponseDTO();
        buildingService.deleteAllById(ids);
        responseDTO.setMessage("Successfully deleted buildings");
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO> create(@ModelAttribute @Valid BuildingDTO buildingDTO,
                                              BindingResult bindingResult) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            // Validation data
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                responseDTO.setErrorDetails(errorMessages);
                responseDTO.setMessage("Validation failed");
                return ResponseEntity.badRequest().body(responseDTO);
            }

            // Service
            BuildingEntity buildingEntity = buildingService.create(buildingDTO);
            responseDTO.setData(buildingEntity.getId());
            responseDTO.setMessage("Create building successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PutMapping()
    public ResponseEntity<ResponseDTO> update(@ModelAttribute @Valid BuildingDTO buildingDTO,
                                              BindingResult bindingResult) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            // Validation data
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
                responseDTO.setErrorDetails(errorMessages);
                responseDTO.setMessage("Validation failed");
                return ResponseEntity.badRequest().body(responseDTO);
            }
            if (buildingDTO.getId() == null) {
                responseDTO.setMessage("ID is required");
                return ResponseEntity.badRequest().body(responseDTO);
            }

            // Service
            BuildingEntity buildingEntity = buildingService.update(buildingDTO);
            responseDTO.setData(buildingEntity.getId());
            responseDTO.setMessage("Update building successfully");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @GetMapping("/{id}/staff")
    public ResponseEntity<ResponseDTO> getStaff(@PathVariable("id") Long staffId) {
        ResponseDTO responseDTO = buildingService.loadStaffsByBuildingId(staffId);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PutMapping("/assign")
    public ResponseEntity<ResponseDTO> updateAssignmentBuilding(@RequestBody AssignmentBuildingDTO assignmentBuildingDTO) {
        return ResponseEntity.ok(buildingService.updateAssignmentBuilding(assignmentBuildingDTO));
    }

}

