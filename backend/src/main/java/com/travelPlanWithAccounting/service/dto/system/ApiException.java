package com.travelPlanWithAccounting.service.dto.system;

import com.travelPlanWithAccounting.service.message.MessageCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * {@code ApiException} 是自定義的 {@link RuntimeException}，用來封裝應用程式內部的錯誤資訊。<br>
 * {@code ApiException} is a custom {@link RuntimeException} used to encapsulate
 * application-specific errors.
 *
 * <p>此類別包含標準化的錯誤代碼 {@link MessageCode}、可選的額外資料（data）、輔助資訊（meta）、用於訊息格式化的參數（args）、 對應的 {@link
 * HttpStatus} 狀態碼，以及原始例外（originalException）。<br>
 * This class contains standardized {@link MessageCode}, optional additional {@code data}, metadata
 * {@code meta}, parameters for message formatting {@code args}, corresponding {@link HttpStatus},
 * and the original cause {@link Exception}.
 *
 * <p>本類別支援序列化，適合統一錯誤處理與分散式系統使用。<br>
 * This class is serializable and intended for consistent error handling and distributed system
 * usage.
 *
 * @author
 */
@Getter
@Builder
@AllArgsConstructor
public class ApiException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * 錯誤代碼，用於標識特定的錯誤類型。<br>
   * Standardized error code identifying the type of error.
   */
  private final MessageCode messageCode;

  /**
   * 與錯誤相關的額外資料，可為任何物件。（可選）<br>
   * Additional data related to the error, any object. (optional)
   */
  private final transient Object data;

  /**
   * 輔助資訊，例如分頁、API版本、請求識別碼等。（可選）<br>
   * Meta information, such as pagination info, API version, request ID, etc. (optional)
   */
  private final transient Object meta;

  /**
   * 用於替換訊息模板中參數的資料陣列。（可選）<br>
   * Array of arguments used for placeholder substitution in the message. (optional)
   */
  private final transient Object[] args;

  /**
   * 此錯誤對應的 HTTP 狀態碼。<br>
   * HTTP status associated with this error.
   */
  private final HttpStatus httpStatus;

  /**
   * 觸發此錯誤的原始例外物件。（可選）<br>
   * The original exception that caused this error. (optional)
   */
  private final Exception originalException;

  /**
   * 使用指定的 {@link MessageCode} 建立新的 {@code ApiException}。<br>
   * Constructs a new {@code ApiException} with the specified {@link MessageCode}.
   *
   * @param messageCode 描述錯誤內容的訊息代碼 / The message code describing the error
   */
  public ApiException(MessageCode messageCode) {
    super(messageCode.getMessage());
    this.messageCode = messageCode;
    this.args = null;
    this.httpStatus = messageCode.getHttpStatus();
    this.originalException = null;
    this.data = null;
    this.meta = null;
  }

  /**
   * 使用指定的 {@link MessageCode} 與參數陣列建立新的 {@code ApiException}。<br>
   * Constructs a new {@code ApiException} with the specified {@link MessageCode} and arguments.
   *
   * @param messageCode 描述錯誤內容的訊息代碼 / The message code describing the error
   * @param args 用於訊息模板參數替換的資料 / Arguments used for message formatting
   */
  public ApiException(MessageCode messageCode, Object[] args) {
    super(messageCode.getMessage(args));
    this.messageCode = messageCode;
    this.args = args;
    this.httpStatus = messageCode.getHttpStatus();
    this.originalException = null;
    this.data = null;
    this.meta = null;
  }

  /**
   * 使用指定的 {@link MessageCode} 與原始例外建立新的 {@code ApiException}。<br>
   * Constructs a new {@code ApiException} with the specified {@link MessageCode} and original
   * exception.
   *
   * @param messageCode 描述錯誤內容的訊息代碼 / The message code describing the error
   * @param originalException 原始觸發的例外 / The original cause exception
   */
  public ApiException(MessageCode messageCode, Exception originalException) {
    super(messageCode.getMessage());
    this.messageCode = messageCode;
    this.args = null;
    this.httpStatus = messageCode.getHttpStatus();
    this.originalException = originalException;
    this.data = null;
    this.meta = null;
  }

  /**
   * 回傳此例外的字串描述，包括錯誤代碼、格式化後的訊息、HTTP 狀態碼以及原始例外訊息（若有）。<br>
   * Returns a string representation of this exception, including code, formatted message, HTTP
   * status, and original exception message if available.
   *
   * @return {@code ApiException} 的字串表示 / String representation of the ApiException
   */
  @Override
  public String toString() {
    return "ApiException{"
        + "messageCode="
        + (messageCode != null ? messageCode.getCode() : null)
        + ", message="
        + (messageCode != null ? messageCode.getMessage(args) : null)
        + ", httpStatus="
        + (httpStatus != null ? httpStatus.value() : null)
        + ", originalException="
        + (originalException != null ? originalException.getMessage() : null)
        + '}';
  }
}
