package com.webapp.models.dtos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingDTO {
    Long id;
    String name;
    String district;
    String ward;
    String street;
    String structure;
    String address;
    Long numberOfBasement;
    Long floorArea;
    String direction;
    String level;
    String rentArea;
    Double rentPrice;
    String rentPriceDescription;
    String serviceFee;
    String carFee;
    String waterFee;
    String motoFee;
    String overtimeFee;
    String electricityFee;
    String deposit;
    String payment;
    String rentTime;
    String decorationTime;
    String managerName;
    String managerPhone;
    Double brokerageFee;
    String note;
    List<String> typeCode;
}
