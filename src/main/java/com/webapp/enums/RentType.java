package com.webapp.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public enum RentType {
  TANG_TRET("Tang tret"),
  NGUYEN_CAN("Nguyen can"),
  NOI_THAT("Noi that");

  private final String name;

  RentType(String name) {
    this.name = name;
  }

  public static Map<String, String> getRentType() {
    Map<String, String> rentType = new LinkedHashMap<>();
    for (RentType r : RentType.values()) {
      rentType.put(r.toString(), r.name);
    }
    return rentType;

  }
}
