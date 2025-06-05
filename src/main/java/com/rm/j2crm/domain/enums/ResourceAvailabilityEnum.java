package com.rm.j2crm.domain.enums;

import com.rm.j2crm.domain.exception.InputDataException;
import com.rm.j2crm.domain.util.ConstantsUtil;

import java.util.Arrays;

public enum ResourceAvailabilityEnum {
  AVAILABLE, UNAVAILABLE;

  public static ResourceAvailabilityEnum getAvailability(String value) {
    return Arrays.stream(values())
      .filter(f -> f.name().equals(value))
      .findFirst()
      .orElseThrow(() -> new InputDataException(ConstantsUtil.ERROR_INVALID.formatted("availability")));
  }
}
