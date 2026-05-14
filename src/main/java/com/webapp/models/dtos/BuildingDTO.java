package com.webapp.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingDTO {
    Long id;

    @NotBlank(message = "Please enter building name")
    String name;

    @NotBlank(message = "Please select district")
    String district;

    String ward;
    String street;
    String structure;
    String address;
    Long numberOfBasement;
    Long floorArea;
    String direction;
    String level;

    @NotBlank(message = "Please enter rent area")
    String rentArea;

    @NotNull(message = "Please enter rent price")
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

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    String managerPhone;

    Double brokerageFee;
    String note;

    @NotEmpty(message = "Please select at least 1 building type")
    List<String> typeCode;

    MultipartFile fileData;
    String base64Image;
    String imageName;
}
