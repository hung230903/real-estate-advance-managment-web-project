package com.webapp.controller.building;

import com.webapp.enums.District;
import com.webapp.enums.RentType;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.models.response.BuildingSearchResponseDTO;
import com.webapp.services.BuildingService;
import com.webapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/buildings")
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String buildingList(@ModelAttribute BuildingSearchRequestDTO buildingSearchRequestDTO,
                               @RequestParam Map<String, String> params,
                               @RequestParam(name = "typeCode", required = false) List<String> typeCode,
                               Model model) {

        model.addAttribute("modelSearch", buildingSearchRequestDTO);
        model.addAttribute("districts", District.getDistrictCode());
        model.addAttribute("typeCodes", RentType.getRentType());
        model.addAttribute("staffs", userService.getAllStaff());
        // Controller -> Service: trả response theo request param
        List<BuildingSearchResponseDTO> buildingSearchResponses = buildingService.findAll(params, typeCode);
        // Bind data ra UI
        model.addAttribute("buildingSearchResponses", buildingSearchResponses);
        return "admin_dashboard/buildingList";

    }

    @GetMapping("/edit")
    public String createbuilding(Model model) {
        model.addAttribute("districts", District.getDistrictCode());
        model.addAttribute("typeCodes", RentType.getRentType());
        model.addAttribute("buildings", new BuildingDTO());
        return "admin_dashboard/buildingEdit";
    }

    @GetMapping("/update/{id}")
    public String updateBuilding(@PathVariable Long id, Model model) {
        BuildingDTO buildingDTO = buildingService.findById(id);
        model.addAttribute("districts", District.getDistrictCode());
        model.addAttribute("typeCodes", RentType.getRentType());
        model.addAttribute("buildings", buildingDTO);
        return "admin_dashboard/buildingEdit";
    }

}
