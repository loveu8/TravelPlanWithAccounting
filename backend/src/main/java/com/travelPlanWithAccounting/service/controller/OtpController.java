package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.otp.OtpSendResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpStatusResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpVerifyResponse;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.model.OtpRequest;
import com.travelPlanWithAccounting.service.model.OtpVerificationRequest;
import com.travelPlanWithAccounting.service.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/otps")
@RequiredArgsConstructor
@Tag(name = "OTP 驗證", description = "OTP 發送/驗證/狀態 API（purpose: REGISTRATION | LOGIN | GUEST_LOGIN）")
public class OtpController {

  private final OtpService otpService;

  @PostMapping("/")
  @Operation(
      summary = "發送 OTP 驗證碼",
      description = "Body 需包含 email 與 purpose（REGISTRATION/LOGIN/GUEST_LOGIN）。")
  public OtpSendResponse sendOtp(@Valid @RequestBody OtpRequest request) {
    return otpService.sendOtp(request);
  }

  @PostMapping("/verification")
  @Operation(
      summary = "驗證 OTP 驗證碼",
      description = "Body 需包含 email、otpCode 與 purpose，purpose 必須與發送時一致。")
  public OtpVerifyResponse verifyOtp(@Valid @RequestBody OtpVerificationRequest request) {
    return otpService.verifyOtpResponse(request);
  }

  @GetMapping("/{email}/status")
  @Operation(summary = "查詢 OTP 狀態", description = "以 Query String 指定 purpose；未傳則預設為 LOGIN。")
  public OtpStatusResponse getOtpStatus(
      @PathVariable String email,
      @RequestParam(name = "purpose", required = false) OtpPurpose purpose) {
    return otpService.getOtpStatusResponse(email, purpose);
  }
}
