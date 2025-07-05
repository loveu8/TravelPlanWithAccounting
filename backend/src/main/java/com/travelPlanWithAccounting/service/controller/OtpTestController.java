package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpRequest;
import com.travelPlanWithAccounting.service.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 僅於 dev 環境啟用的 OTP 測試 API，不會發送 email，直接回傳驗證碼。<br>
 * OTP test API for dev only, returns OTP code directly without sending email.
 */
@RestController
@RequestMapping("/api/auth/otps-test")
@RequiredArgsConstructor
@Tag(name = "OTP 驗證", description = "OTP 驗證與查詢 API")
@Profile("dev")
public class OtpTestController {
  private final OtpService otpService;

  /**
   * 測試產生 OTP 並直接回傳驗證碼。<br>
   * Generate OTP and return code directly for testing.<br>
   *
   * @param email 用戶電子郵件 (User email)
   * @return 產生的 OTP 驗證碼 (Generated OTP code)
   */
  @PostMapping("/")
  @Operation(summary = "產生 OTP 驗證碼並直接回傳不發送EMAIL(for test)")
  public String generateOtpForTest(@RequestBody OtpRequest request) {
    OtpData otpData = otpService.generateOtpWithoutMail(request);
    return otpData.getOtpCode();
  }
}
