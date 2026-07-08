package com.webapp.models.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingSearchRequestDTO {
  String name;
  Long floorArea;
  Long numberOfBasement;
  String district;
  String ward;
  String street;
  String managerName;
  String managerPhone;
  String direction;
  String level;
  List<String> typeCode;
  Long rentPriceFrom;
  Long rentPriceTo;
  Long rentAreaFrom;
  Long rentAreaTo;
  Long staffId;
}
