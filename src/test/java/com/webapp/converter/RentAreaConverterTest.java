package com.webapp.converter;

import com.webapp.entities.BuildingEntity;
import com.webapp.entities.RentAreaEntity;
import com.webapp.models.dtos.BuildingDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RentAreaConverter Unit Tests")
class RentAreaConverterTest {

    private final RentAreaConverter rentAreaConverter = new RentAreaConverter();

    @Test
    @DisplayName("toRentAreaEntities should parse comma separated rent areas")
    void toRentAreaEntities_validInput() {
        BuildingDTO dto = new BuildingDTO();
        dto.setRentArea("100, 200, 300");
        BuildingEntity entity = new BuildingEntity();

        List<RentAreaEntity> result = rentAreaConverter.toRentAreaEntities(dto, entity);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getValue()).isEqualTo(100L);
        assertThat(result.get(0).getBuilding()).isEqualTo(entity);
        assertThat(result.get(1).getValue()).isEqualTo(200L);
        assertThat(result.get(2).getValue()).isEqualTo(300L);
    }

    @Test
    @DisplayName("toRentAreaEntities should return empty list when rent area is null")
    void toRentAreaEntities_nullInput() {
        BuildingDTO dto = new BuildingDTO();
        dto.setRentArea(null);
        BuildingEntity entity = new BuildingEntity();

        List<RentAreaEntity> result = rentAreaConverter.toRentAreaEntities(dto, entity);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toRentAreaEntities should return empty list when rent area is empty")
    void toRentAreaEntities_emptyInput() {
        BuildingDTO dto = new BuildingDTO();
        dto.setRentArea("");
        BuildingEntity entity = new BuildingEntity();

        List<RentAreaEntity> result = rentAreaConverter.toRentAreaEntities(dto, entity);

        assertThat(result).isEmpty();
    }
}
