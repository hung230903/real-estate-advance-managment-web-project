package com.webapp.converter;

import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.models.dtos.BuildingDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RentAreaConverter {
  public List<RentAreaEntity> toRentAreaEntities(BuildingDTO buildingDTO, BuildingEntity buildingEntity) {
    List<RentAreaEntity> rentAreaEntities = new ArrayList<>();
    if (buildingDTO.getRentArea() != null && !buildingDTO.getRentArea().isEmpty()) {
      String[] rentAreas = buildingDTO.getRentArea().split(",");
      for (String r : rentAreas) {
        RentAreaEntity rentAreaEntity = new RentAreaEntity();
        rentAreaEntity.setValue(Long.parseLong(r.trim()));
        rentAreaEntity.setBuilding(buildingEntity);
        rentAreaEntities.add(rentAreaEntity);
      }
    }

    return rentAreaEntities;

  }
}
