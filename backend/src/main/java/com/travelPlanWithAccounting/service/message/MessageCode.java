package com.travelPlanWithAccounting.service.message;

import java.io.Serializable;
import org.springframework.http.HttpStatus;

public interface MessageCode extends Serializable {
  String getMessage();

  String getMessage(Object[] args);

  String getCode();

  String name();

  HttpStatus getHttpStatus();
}
