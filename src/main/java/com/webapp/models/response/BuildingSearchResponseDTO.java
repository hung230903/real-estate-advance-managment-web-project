package com.webapp.models.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingSearchResponseDTO {
  Long id;
  String name;
  String managerName;
  String managerPhone;
  String address;
  Long numberOfBasement;
  Long floorArea;
  Double rentPrice;
  String rentArea;
  String emptyArea;
  Double brokerageFee;
  String serviceFee;
}
