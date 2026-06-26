package com.webapp.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.models.dtos.AssignmentBuildingDTO;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.dtos.ResponseDTO;
import com.webapp.services.BuildingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import com.webapp.components.JwtTokenUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuildingAPI.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple unit test
@DisplayName("BuildingAPI Unit Tests")
class BuildingAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BuildingService buildingService;

  @MockBean
  private UserDetailsService userDetailsService;

  @MockBean
  private JwtTokenUtils jwtTokenUtils;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("DELETE /{ids} should delete buildings and return success response")
  void deleteBuildings() throws Exception {
    doNothing().when(buildingService).deleteAllById(any());

    mockMvc.perform(delete("/admin/api/buildings/1,2,3"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Successfully deleted buildings"));

    verify(buildingService).deleteAllById(List.of(1L, 2L, 3L));
  }

  @Test
  @DisplayName("GET /{id}/staff should return staff list for building")
  void getStaff() throws Exception {
    ResponseDTO response = new ResponseDTO();
    response.setMessage("Load Staffs successfully");
    when(buildingService.loadStaffsByBuildingId(1L)).thenReturn(response);

    mockMvc.perform(get("/admin/api/buildings/1/staff"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Load Staffs successfully"));
  }

  @Test
  @DisplayName("PUT /assign should assign staff to building")
  void updateAssignmentBuilding() throws Exception {
    AssignmentBuildingDTO dto = new AssignmentBuildingDTO(1L, List.of(1L, 2L));

    ResponseDTO response = new ResponseDTO();
    response.setMessage("Assign success");

    when(buildingService.updateAssignmentBuilding(any(AssignmentBuildingDTO.class))).thenReturn(response);

    mockMvc.perform(put("/admin/api/buildings/assign")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Assign success"));
  }
}
