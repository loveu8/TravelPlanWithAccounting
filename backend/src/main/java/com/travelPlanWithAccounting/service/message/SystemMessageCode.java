package com.travelPlanWithAccounting.service.message;

import java.text.MessageFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SystemMessageCode implements MessageCode {

  // 400 BAD_REQUEST
  INVALID_PARAMETER("400001", "{0} 參數錯誤", HttpStatus.BAD_REQUEST),
  MISSING_REQUIRED_PARAMETER("400002", "缺少必要參數：{0}", HttpStatus.BAD_REQUEST),
  REQUEST_NOT_READABLE("400003", "無法讀取請求資料", HttpStatus.BAD_REQUEST),

  // 401 UNAUTHORIZED
  UNAUTHORIZED_ACCESS("401001", "未授權的存取", HttpStatus.UNAUTHORIZED),

  // 403 FORBIDDEN
  FORBIDDEN_OPERATION("403001", "禁止的操作", HttpStatus.FORBIDDEN),

  // 404 NOT_FOUND
  RESOURCE_NOT_FOUND("404001", "{0} 資源不存在", HttpStatus.NOT_FOUND),

  // 409 CONFLICT
  DUPLICATE_RESOURCE("409001", "{0} 已存在", HttpStatus.CONFLICT),
  RESOURCE_CONFLICT("409002", "{0} 發生衝突", HttpStatus.CONFLICT),

  // 500 INTERNAL_SERVER_ERROR
  INTERNAL_SERVER_ERROR("500001", "系統內部錯誤，請稍後再試", HttpStatus.INTERNAL_SERVER_ERROR),
  SERVICE_UNAVAILABLE("500002", "服務暫時不可用", HttpStatus.SERVICE_UNAVAILABLE),
  UNEXPECTED_ERROR("500003", "發生非預期的系統錯誤，請聯絡系統管理員", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;

  @Override
  public String getMessage(Object[] args) {
    return MessageFormat.format(message, args);
  }
}
