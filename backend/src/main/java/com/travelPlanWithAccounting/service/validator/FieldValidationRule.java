package com.travelPlanWithAccounting.service.validator;

public record FieldValidationRule(
    boolean nullable,
    FieldType type,
    Integer minLength,
    Integer maxLength,
    boolean securityCheck) {
  public FieldValidationRule {
    if (type == null) {
      throw new IllegalArgumentException("type is required");
    }
  }
}
