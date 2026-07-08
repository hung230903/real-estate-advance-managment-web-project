package com.webapp.controller.admin.building;

import com.webapp.constant.SystemConstant;
import com.webapp.entities.UserEntity;
import com.webapp.enums.District;
import com.webapp.enums.RentType;
import com.webapp.models.dtos.BuildingDTO;
import com.webapp.models.request.BuildingSearchRequestDTO;
import com.webapp.services.BuildingService;
import com.webapp.services.UserService;
import com.webapp.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static com.webapp.constant.SystemConstant.MAX_NAVIGATION_PAGE;
import static com.webapp.constant.SystemConstant.MAX_RESULT;

@Controller
@RequestMapping("/admin/buildings")
@RequiredArgsConstructor
public class BuildingController {

  private final BuildingService buildingService;

  private final UserService userService;

  @GetMapping("/list")
  public String buildingList(@ModelAttribute BuildingSearchRequestDTO buildingSearchRequestDTO,
      @RequestParam(value = "page", defaultValue = "1") int page,
      Model model) {

    List<String> authorities = SecurityUtils.getAuthorities();
    if (authorities.contains(SystemConstant.STAFF_ROLE)) {
      Long staffId = Objects.requireNonNull(SecurityUtils.getPrincipal()).getId();
      buildingSearchRequestDTO.setStaffId(staffId);
    }

    model.addAttribute("modelSearch", buildingSearchRequestDTO);
    model.addAttribute("districts", District.getDistrictCode());
    model.addAttribute("typeCodes", RentType.getRentType());

    if (authorities.contains(SystemConstant.MANAGER_ROLE)) {
      model.addAttribute("staffs", userService.getAllStaff());
    }

    // Controller -> Service: trả PaginationResult
    model.addAttribute("model",
        buildingService.searchBuildings(buildingSearchRequestDTO, page, MAX_RESULT, MAX_NAVIGATION_PAGE));

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
    if (SecurityUtils.getAuthorities().contains(SystemConstant.STAFF_ROLE)) {
      UserEntity userEntity = userService
          .getUserByUserName(Objects.requireNonNull(SecurityUtils.getPrincipal()).getUsername());
      if (userEntity.getBuildingEntities().stream().noneMatch(b -> b.getId().equals(id))) {
        return "redirect:/403";
      }
    }
    BuildingDTO buildingDTO = buildingService.findById(id);
    model.addAttribute("districts", District.getDistrictCode());
    model.addAttribute("typeCodes", RentType.getRentType());
    model.addAttribute("buildings", buildingDTO);
    return "admin/building/buildingEdit";
  }

  @GetMapping("/buildingImage")
  @ResponseBody
  public byte[] buildingImage(@RequestParam("id") Long id) {
    return buildingService.getImage(id);
  }
}
