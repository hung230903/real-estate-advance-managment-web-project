package com.webapp.enums;

import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public enum District {
  QUAN_1("Quan 1"),
  QUAN_2("Quan 2"),
  QUAN_4("Quan 4");

  private final String districtName;

  District(String districtName) {
    this.districtName = districtName;
  }

  public static Map<String, String> getDistrictCode() {
    Map<String, String> district = new LinkedHashMap<>();
    for (District d : District.values()) {
      district.put(d.toString(), d.districtName);
    }

    return district;
  }

  public static String getDistrictName(String code) {
    if (code == null)
      return null;
    try {
      return District.valueOf(code).getDistrictName();
    } catch (IllegalArgumentException e) {
      return code;
    }
  }
}
