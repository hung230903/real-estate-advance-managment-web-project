package com.webapp.controller.admin.building;

import com.webapp.enums.District;
import com.webapp.enums.RentType;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
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
                               @RequestParam(value = "page", defaultValue = "1") int page,
                               Model model) {

        model.addAttribute("modelSearch", buildingSearchRequestDTO);
        model.addAttribute("districts", District.getDistrictCode());
        model.addAttribute("typeCodes", RentType.getRentType());
        model.addAttribute("staffs", userService.getAllStaff());

        final int MAX_RESULT = 3;
        final int MAX_NAVIGATION_PAGE = 3;

        // Controller -> Service: trả PaginationResult
        model.addAttribute("model",
                buildingService.searchBuildings(params, typeCode, page, MAX_RESULT, MAX_NAVIGATION_PAGE));

        return "admin/building/buildingList";
    }

    @GetMapping("/edit")
    public String createbuilding(Model model) {
        model.addAttribute("districts", District.getDistrictCode());
        model.addAttribute("typeCodes", RentType.getRentType());
        model.addAttribute("buildings", new BuildingDTO());
        return "admin/building/buildingEdit";
    }

    @GetMapping("/update/{id}")
    public String updateBuilding(@PathVariable Long id, Model model) {
        BuildingDTO buildingDTO = buildingService.findById(id);
        model.addAttribute("districts", District.getDistrictCode());
        model.addAttribute("typeCodes", RentType.getRentType());
        model.addAttribute("buildings", buildingDTO);
        return "admin/building/buildingEdit";
    }

}
