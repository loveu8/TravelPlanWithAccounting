package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.dto.otp.OtpSendResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpStatusResponse;
import com.travelPlanWithAccounting.service.dto.otp.OtpVerifyResponse;
import com.travelPlanWithAccounting.service.model.OtpRequest;
import com.travelPlanWithAccounting.service.model.OtpVerificationRequest;
import com.travelPlanWithAccounting.service.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/otps")
@RequiredArgsConstructor
@Tag(name = "OTP 驗證", description = "OTP 驗證與查詢 API")
public class OtpController {
  private final OtpService otpService;

  @PostMapping("/")
  @Operation(summary = "發送 OTP 驗證碼")
  public OtpSendResponse sendOtp(@RequestBody OtpRequest request) {
    return otpService.sendOtp(request);
  }

  @PostMapping("/verification")
  @Operation(summary = "驗證 OTP 驗證碼")
  public OtpVerifyResponse verifyOtp(@RequestBody OtpVerificationRequest request) {
    return otpService.verifyOtpResponse(request);
  }

  @GetMapping("/{email}/status")
  @Operation(summary = "查詢 OTP 狀態")
  public OtpStatusResponse getOtpStatus(@PathVariable String email) {
    return otpService.getOtpStatusResponse(email);
  }
}
