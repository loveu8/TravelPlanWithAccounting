package com.travelPlanWithAccounting.service.util;

import com.travelPlanWithAccounting.service.dto.system.ApiException;
import com.travelPlanWithAccounting.service.dto.system.PageMeta;
import com.travelPlanWithAccounting.service.dto.system.RestResponse;
import com.travelPlanWithAccounting.service.message.MessageCode;
import java.io.Serializable;
import org.springframework.data.domain.Page;

/**
 * {@code RestResponseUtils} 提供快速建立標準化 API 回應物件的方法。<br>
 * {@code RestResponseUtils} provides utility methods to quickly build standardized API responses.
 *
 * <p>支援成功與失敗回應，並確保資料型別符合 {@code Serializable}。<br>
 * Supports success and error responses, ensuring payloads are {@code Serializable}.
 *
 * @author
 */
public class RestResponseUtils {

  private RestResponseUtils() {
    // 工具類不允許建立實例 / Utility class, prevent instantiation
  }

  /**
   * 建立成功回應，只含資料。<br>
   * Builds a success response with data only.
   *
   * @param data 資料內容 / The main payload
   * @return 標準化成功回應 / Standard success response
   * @param <D> 資料型別 / Type of data
   */
  public static RestResponse<Serializable, Serializable> success(Serializable data) {
    return RestResponse.builder().data(data).build();
  }

  /**
   * 建立成功回應，含資料與輔助資訊。<br>
   * Builds a success response with data and meta information.
   *
   * @param data 資料內容 / The main payload
   * @param meta 輔助資訊 / Meta information
   * @return 標準化成功回應 / Standard success response
   * @param <D> 資料型別 / Type of data
   * @param <M> Meta型別 / Type of meta
   */
  public static <D extends Serializable, M extends Serializable> RestResponse<D, M> successWithMeta(
      D data, M meta) {
    return RestResponse.<D, M>builder().data(data).meta(meta).build();
  }

  /**
   * 專門支援 Spring Data {@link Page} 分頁結果。<br>
   * Build a success response from Spring Data {@link Page} result.
   *
   * @param page 分頁結果物件 / Page result
   * @param <T> 頁面中的資料型別 / Type of elements inside page
   * @return 標準化成功回應（包含資料與分頁 meta） / Standard success response with page meta
   */
  public static <T extends Serializable> RestResponse<Serializable, Serializable> successWithPage(
      Page<T> page) {
    if (page == null) {
      throw new IllegalArgumentException("Page must not be null");
    }

    PageMeta pageMeta =
        PageMeta.builder()
            .page(page.getNumber())
            .size(page.getSize())
            .totalPages(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .build();

    return RestResponse.<Serializable, Serializable>builder()
        .data((Serializable) page.getContent())
        .meta(pageMeta)
        .build();
  }

  /**
   * 由 {@link ApiException} 建立錯誤回應。<br>
   * Builds an error response from {@link ApiException}.
   *
   * @param ex 捕捉到的例外 / The ApiException caught
   * @return 標準化錯誤回應 / Standard error response
   */
  public static RestResponse<Serializable, Serializable> error(ApiException ex) {
    return new RestResponse<>(ex);
  }

  /**
   * 由 {@link MessageCode} 建立錯誤回應（無原始例外）。<br>
   * Builds an error response from {@link MessageCode} without original exception.
   *
   * @param messageCode 錯誤代碼 / Error message code
   * @return 標準化錯誤回應 / Standard error response
   */
  public static RestResponse<Serializable, Serializable> error(MessageCode messageCode) {
    return new RestResponse<>(new ApiException(messageCode));
  }

  /**
   * 由 {@link MessageCode} 及參數建立錯誤回應。<br>
   * Builds an error response from {@link MessageCode} with substitution arguments.
   *
   * @param messageCode 錯誤代碼 / Error message code
   * @param args 替換訊息模板的參數 / Arguments for message formatting
   * @return 標準化錯誤回應 / Standard error response
   */
  public static RestResponse<Serializable, Serializable> error(
      MessageCode messageCode, Serializable[] args) {
    return new RestResponse<>(new ApiException(messageCode, args));
  }

  /**
   * 由 {@link MessageCode} 及原始例外建立錯誤回應。<br>
   * Builds an error response from {@link MessageCode} with original exception.
   *
   * @param messageCode 錯誤代碼 / Error message code
   * @param args 替換訊息模板的參數 / Arguments for message formatting
   * @param originalException 原始例外 / Original exception
   * @return 標準化錯誤回應 / Standard error response
   */
  public static RestResponse<Serializable, Serializable> error(
      MessageCode messageCode, Serializable[] args, Exception originalException) {
    return new RestResponse<>(
        ApiException.builder()
            .messageCode(messageCode)
            .args(args)
            .originalException(originalException)
            .build());
  }
}
