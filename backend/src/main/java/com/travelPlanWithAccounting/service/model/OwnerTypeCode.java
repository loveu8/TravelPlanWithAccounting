package com.travelPlanWithAccounting.service.model;

public enum OwnerTypeCode {
  MEMBER("001"),
  GUEST("002");

  private final String code;

  OwnerTypeCode(String code) { this.code = code; }

  public String code() { return code; }
}
