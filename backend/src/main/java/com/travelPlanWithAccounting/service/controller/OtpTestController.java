package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpRequest;
import com.travelPlanWithAccounting.service.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 僅於 dev 環境啟用的 OTP 測試 API，不會發送 email，直接回傳驗證碼。 OTP test API for dev only, returns OTP code directly
 * without sending email.
 */
@RestController
@RequestMapping("/api/auth/otps-test")
@RequiredArgsConstructor
@Tag(
    name = "OTP 驗證 (dev)",
    description = "dev 專用：產生 OTP（不寄信）。請在 body 帶 email 與 purpose（REGISTRATION｜LOGIN｜GUEST_LOGIN）。")
@Profile("dev")
public class OtpTestController {

  private final OtpService otpService;

  /**
   * 測試產生 OTP 並直接回傳驗證碼（不發送 Email）。
   *
   * <p>範例請求： { "email": "user@example.com", "purpose": "REGISTRATION" }
   *
   * @return 產生的 OTP 驗證碼（僅 dev 使用，請勿在正式環境開放）
   */
  @PostMapping("/")
  @Operation(
      summary = "產生 OTP 驗證碼並直接回傳（不發送 Email；dev 專用）",
      description = "Body 需包含 email 與 purpose（REGISTRATION/LOGIN/GUEST_LOGIN）。")
  public String generateOtpForTest(@Valid @RequestBody OtpRequest request) {
    OtpData otpData = otpService.generateOtpWithoutMail(request);
    return otpData.getOtpCode();
  }
}
