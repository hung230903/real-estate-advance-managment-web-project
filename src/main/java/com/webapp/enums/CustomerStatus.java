package com.webapp.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum CustomerStatus {
  DANG_XU_LY("Đang xử lý"),
  HOAN_THANH("Hoàn thành"),
  CHUA_XU_LY("Chưa xử lý");

  private final String name;

  CustomerStatus(String name) {
    this.name = name;
  }

  public static Map<String, String> getCustomerStatus() {
    Map<String, String> statuses = new HashMap<>();
    for (CustomerStatus status : CustomerStatus.values()) {
      statuses.put(status.name(), status.name);
    }
    return statuses;
  }
}
