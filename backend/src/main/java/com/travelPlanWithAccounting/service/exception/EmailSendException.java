package com.travelPlanWithAccounting.service.exception;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.message.SystemMessageCode;

public class EmailSendException extends ApiException {
  public EmailSendException() {
    super(SystemMessageCode.EMAIL_SEND_ERROR);
  }

  public EmailSendException(Exception originalException) {
    super(SystemMessageCode.EMAIL_SEND_ERROR, originalException);
  }
}
