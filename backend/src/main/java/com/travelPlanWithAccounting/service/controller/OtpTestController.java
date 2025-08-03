package com.travelPlanWithAccounting.service.controller;

import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.model.OtpRequest;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

/** dev 專用：產生 OTP（不寄信），同時寫入 auth_info(validation=false)；僅供本機/測試環境使用。 */
@RestController
@RequestMapping("/api/auth/otps-test")
@RequiredArgsConstructor
@Tag(
    name = "OTP 驗證 (dev)",
    description = "dev 專用：產生 OTP（不寄信）。未帶 purpose 時，會依 email 是否存在自動判斷 REGISTRATION/LOGIN。")
@Profile("dev")
public class OtpTestController {

  private final OtpService otpService;
  private final MemberRepository memberRepository;

  /**
   * 產生 OTP 並直接回傳驗證碼（不發送 Email）。
   *
   * <p>範例請求：
   *
   * <pre>
   * { "email": "user@example.com", "purpose": "REGISTRATION" }
   * // 或不帶 purpose，系統將自動判斷
   * { "email": "user@example.com" }
   * </pre>
   *
   * @return Dev 友善的 JSON：email、purpose、actionCode、otpCode、expireAt
   */
  @PostMapping
  @Operation(
      summary = "產生 OTP（不發送 Email；dev 專用）",
      description = "Body 可包含 email 與 purpose（REGISTRATION/LOGIN/GUEST_LOGIN）。未帶 purpose 時會自動判斷。")
  public DevOtpResponse generateOtpForTest(@Valid @RequestBody OtpRequest request) {

    OtpPurpose purpose = request.getPurpose();
    if (purpose == null) {
      boolean exists = memberRepository.existsByEmail(request.getEmail());
      purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;
      request.setPurpose(purpose);
    }

    OtpData otpData = otpService.generateOtpWithoutMail(request);

    return new DevOtpResponse(
        request.getEmail(),
        purpose,
        purpose.actionCode(),
        otpData.getOtpCode(),
        otpData.getExpiryTime(),
        otpData.getToken());
  }

  // ---- 僅供 dev 回傳 JSON 的資料結構 ----
  @Data
  @AllArgsConstructor
  static class DevOtpResponse {
    private String email;
    private OtpPurpose purpose;
    private String actionCode;
    private String otpCode; // dev/本機調試才回；勿在 prod 使用
    private LocalDateTime expireAt; // local time；DB 寫入為 UTC
    private java.util.UUID token;
  }
}
