package com.travelPlanWithAccounting.service.exception;

public class ApiException extends RuntimeException {
  private final String desc;
  public ApiException(String desc) {super(desc); this.desc = desc;}
  public String getDesc(){return desc;}
}
