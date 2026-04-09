package com.webapp.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Building name can not be blank")
    String name;
    @NotBlank(message = "District can not be blank")
    String district;
    String ward;
    String street;
    String structure;
    String address;
    Long numberOfBasement;
    Long floorArea;
    String direction;
    String level;
    @NotBlank(message = "Rent area can not be blank")
    String rentArea;
    @NonNull
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
    @Size(min = 10, message = "Phone number must have 10 digits at least")
    String managerPhone;
    Double brokerageFee;
    String note;
    @Size(min = 1, message = "Choose at least 1 building type")
    List<String> typeCode;
}
