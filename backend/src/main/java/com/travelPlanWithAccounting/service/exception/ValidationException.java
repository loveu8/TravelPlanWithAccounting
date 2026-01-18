package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.ValidationMessageCode;

public class ValidationException extends ApiException {
    public ValidationException(ValidationMessageCode code) {
    super(code);
  }

  public ValidationException(ValidationMessageCode code, Object[] args) {
    super(code, args);
  }

  public static class RequiredField extends ValidationException {
    public RequiredField(String fieldName) {
      super(ValidationMessageCode.VALIDATION_REQUIRED, new Object[] {fieldName});
    }
  }

  public static class TypeMismatch extends ValidationException {
    public TypeMismatch(String fieldName, String expectedType) {
      super(ValidationMessageCode.VALIDATION_TYPE_MISMATCH, new Object[] {fieldName, expectedType});
    }
  }

  public static class LengthExceeded extends ValidationException {
    public LengthExceeded(String fieldName, int maxLength) {
      super(ValidationMessageCode.VALIDATION_LENGTH_EXCEEDED, new Object[] {fieldName, maxLength});
    }
  }

  public static class LengthTooShort extends ValidationException {
    public LengthTooShort(String fieldName, int minLength) {
      super(ValidationMessageCode.VALIDATION_LENGTH_TOO_SHORT, new Object[] {fieldName, minLength});
    }
  }

  public static class UnsafeInput extends ValidationException {
    public UnsafeInput(String fieldName) {
      super(ValidationMessageCode.VALIDATION_UNSAFE_INPUT, new Object[] {fieldName});
    }
  }

  public static class DateRangeInvalid extends ValidationException {
    public DateRangeInvalid(String startField, String endField) {
      super(ValidationMessageCode.VALIDATION_DATE_RANGE_INVALID, new Object[] {startField, endField});
    }
  }

  public static class DateRangeTooShort extends ValidationException {
    public DateRangeTooShort(String startField, String endField, int minDays) {
      super(
          ValidationMessageCode.VALIDATION_DATE_RANGE_TOO_SHORT,
          new Object[] {startField, endField, minDays});
    }
  }

  public static class DateRangeTooLong extends ValidationException {
    public DateRangeTooLong(String startField, String endField, int maxDays) {
      super(
          ValidationMessageCode.VALIDATION_DATE_RANGE_TOO_LONG,
          new Object[] {startField, endField, maxDays});
    }
  }
}
