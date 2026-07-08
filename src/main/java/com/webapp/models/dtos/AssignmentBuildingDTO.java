package com.webapp.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentBuildingDTO {
  @NotNull(message = "Building id is required")
  Long buildingId;

  @Size(min = 1, message = "")
  @JsonProperty("staffs")
  List<Long> staffIds;
}
