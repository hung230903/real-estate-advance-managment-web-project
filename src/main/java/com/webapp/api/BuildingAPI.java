package com.webapp.api;

import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.services.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/buildings")
public class BuildingAPI {

    @Autowired
    private BuildingService buildingService;

    @GetMapping()
    public ResponseEntity<ResponseDTO> getBuilding(@RequestParam Map<String, String> params,
                                                   @RequestParam(name = "typeCode", required = false) List<String> typeCode) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            List<BuildingSearchResponseDTO> result = buildingService.findAll(params, typeCode);
            responseDTO.setData(result);
            responseDTO.setMessage("Success");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Find Building Failed");
            responseDTO.setErrorDetails(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @DeleteMapping("/{ids}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable List<Long> ids) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            buildingService.delete(ids);
            responseDTO.setMessage("Successfully deleted buildings");
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Delete buildings failed");
            responseDTO.setErrorDetails(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO> create(@RequestBody BuildingDTO buildingDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            buildingService.create(buildingDTO);
            responseDTO.setMessage("Created building successfully!!!");
            responseDTO.setData(buildingDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Could not create building!!!");
            responseDTO.setErrorDetails(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PutMapping()
    public ResponseEntity<ResponseDTO> update(@RequestBody BuildingDTO buildingDTO) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            buildingService.update(buildingDTO);
            responseDTO.setMessage("Updated building successfully!!!");
            responseDTO.setData(buildingDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            responseDTO.setMessage("Update failed");
            responseDTO.setErrorDetails(e.getMessage());
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

}

