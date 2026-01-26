package com.travelPlanWithAccounting.service.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.travelPlanWithAccounting.service.exception.ValidationException;

@Component
public class FieldValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final List<Pattern> UNSAFE_PATTERNS =
      List.of(
          Pattern.compile("(?i)<\\s*script"),
          Pattern.compile("(?i)javascript:"),
          Pattern.compile("(?i)onerror\\s*="),
          Pattern.compile("(?i)onload\\s*="),
          Pattern.compile("--"),
          Pattern.compile("/\\*"),
          Pattern.compile("\\*/"),
          Pattern.compile("(?i)\\b(drop|truncate|alter|create)\\b"));

  public void validate(String fieldName, Object value, FieldValidationRule rule) {
    Objects.requireNonNull(rule, "rule is required");
    if (value == null) {
      if (!rule.nullable()) {
        throw new ValidationException.RequiredField(fieldName);
      }
      return;
    }

    switch (rule.type()) {
      case VARCHAR, TEXT -> validateText(fieldName, value, rule);
      case DATE -> validateDate(fieldName, value);
      case STRING_LIST -> validateStringList(fieldName, value, rule);
    }
  }

  public void validateDateRange(
      String startField, LocalDate start, String endField, LocalDate end, DateRangeRule rule) {
    Objects.requireNonNull(rule, "rule is required");
    if (start == null || end == null) {
      if (rule.allowNull()) {
        return;
      }
      if (start == null) {
        throw new ValidationException.RequiredField(startField);
      }
      throw new ValidationException.RequiredField(endField);
    }

    if (start.isAfter(end)) {
      throw new ValidationException.DateRangeInvalid(startField, endField);
    }

    long daysBetween = ChronoUnit.DAYS.between(start, end);
    Integer minDays = rule.minDays();
    Integer maxDays = rule.maxDays();
    if (minDays != null && daysBetween < minDays) {
      throw new ValidationException.DateRangeTooShort(startField, endField, minDays);
    }
    if (maxDays != null && daysBetween > maxDays) {
      throw new ValidationException.DateRangeTooLong(startField, endField, maxDays);
    }
  }

  private void validateText(String fieldName, Object value, FieldValidationRule rule) {
    if (!(value instanceof String textValue)) {
      throw new ValidationException.TypeMismatch(fieldName, rule.type().name());
    }
    String trimmed = textValue.trim();
    if (!rule.nullable() && trimmed.isEmpty()) {
      throw new ValidationException.RequiredField(fieldName);
    }

    Integer minLength = rule.minLength();
    if (minLength != null && trimmed.length() < minLength) {
      throw new ValidationException.LengthTooShort(fieldName, minLength);
    }
    Integer maxLength = rule.maxLength();
    if (maxLength != null && trimmed.length() > maxLength) {
      throw new ValidationException.LengthExceeded(fieldName, maxLength);
    }

    if (rule.securityCheck() && containsUnsafePattern(trimmed)) {
      throw new ValidationException.UnsafeInput(fieldName);
    }
  }

  private void validateDate(String fieldName, Object value) {
    if (value instanceof LocalDate) {
      return;
    }
    if (value instanceof String dateText) {
      try {
        LocalDate.parse(dateText, DATE_FORMATTER);
        return;
      } catch (DateTimeParseException ignored) {
        // handled below
      }
    }
    throw new ValidationException.TypeMismatch(fieldName, FieldType.DATE.name());
  }

  private void validateStringList(String fieldName, Object value, FieldValidationRule rule) {
    if (!(value instanceof List<?> listValue)) {
      throw new ValidationException.TypeMismatch(fieldName, FieldType.STRING_LIST.name());
    }
    if (!rule.nullable() && listValue.isEmpty()) {
      throw new ValidationException.RequiredField(fieldName);
    }
    for (Object element : listValue) {
      if (!(element instanceof String textValue)) {
        throw new ValidationException.TypeMismatch(fieldName, FieldType.STRING_LIST.name());
      }
      String trimmed = textValue.trim();
      if (rule.securityCheck() && !trimmed.isEmpty() && containsUnsafePattern(trimmed)) {
        throw new ValidationException.UnsafeInput(fieldName);
      }
    }
  }

  private boolean containsUnsafePattern(String value) {
    String normalized = value.toLowerCase(Locale.ROOT);
    return UNSAFE_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(normalized).find());
  }
}
