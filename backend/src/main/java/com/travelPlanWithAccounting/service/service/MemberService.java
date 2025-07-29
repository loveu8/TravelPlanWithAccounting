package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.MemberResponse;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.exception.MemberException;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.util.EmailValidatorUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 會員服務：整併 preAuthFlow（判斷 + 寄送 OTP）與 authFlow（驗證 OTP 後完成登入/註冊）。 回傳沿用 MemberResponse；未來切 PRD cookies
 * 結構時，只需改此處回傳。
 */
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final OtpService otpService; // purpose-aware，且已寫入/核銷 auth_info

  // ---------- preAuthFlow：判斷 purpose 並立即寄送 OTP ----------

  @Transactional
  public PreAuthFlowResponse preAuthFlow(String email) {
    validateEmail(email);

    boolean exists = memberRepository.existsByEmail(email);
    OtpPurpose purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;

    // 直接觸發 OTP 寄送（依用途）；OtpService 會記錄 auth_info(validation=false)
    otpService.generateOtp(email, purpose);

    return new PreAuthFlowResponse(email, exists, purpose, purpose.actionCode());
  }

  // ---------- authFlow：驗證 OTP，完成登入或註冊 ----------

  @Transactional
  public MemberResponse authFlow(AuthFlowRequest req) {
    validateEmail(req.getEmail());

    String email = req.getEmail();
    boolean exists = memberRepository.existsByEmail(email);
    OtpPurpose purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;

    // 驗證 OTP；成功會核銷 cache 與將 auth_info.validation=true
    boolean ok = otpService.verifyOtp(email, req.getOtpCode(), purpose);
    if (!ok) {
      throw new MemberException.OtpTokenInvalid(); // 可細分為 OTP_INVALID/EXPIRED 等
    }

    if (exists) {
      // ---- 登入 ----
      Member member =
          memberRepository.findByEmail(email).orElseThrow(MemberException.EmailNotFound::new);
      String jwt = generateJwt(member); // TODO：切換 JwtUtil + RefreshTokenService 後改回傳 PRD envelope
      return new MemberResponse(member.getEmail(), jwt);

    } else {
      // ---- 註冊 ----（double-check 避免 race）
      checkEmailDuplicate(email);

      Member member =
          Member.builder()
              .givenName(req.getGivenName())
              .familyName(req.getFamilyName())
              .nickName(req.getNickName())
              .birthday(req.getBirthday())
              .email(email)
              .subscribe(false)
              .status(Short.valueOf("1"))
              .build();

      Member saved = memberRepository.save(member);
      String jwt = generateJwt(saved); // TODO：改回傳 PRD envelope（含 cookies）
      return new MemberResponse(saved.getEmail(), jwt);
    }
  }

  // ---------- 私有工具 ----------

  private void validateEmail(String email) {
    if (email == null || email.isEmpty()) throw new MemberException.EmailRequired();
    if (!EmailValidatorUtil.isValid(email)) throw new MemberException.EmailFormatInvalid();
  }

  private void checkEmailDuplicate(String email) {
    if (memberRepository.existsByEmail(email)) throw new MemberException.EmailAlreadyExists();
  }

  // 範例 JWT 產生（之後換 JwtUtil + RefreshTokenService + 回傳 AuthEnvelope）
  private String generateJwt(Member member) {
    return "mock-jwt-token";
  }

  /** 仍保留原有方法（若其他地方共用） */
  public Member assertActiveMember(UUID authMemberId, String bodyMemberIdOpt) {
    if (bodyMemberIdOpt != null) {
      UUID bodyId;
      try {
        bodyId = UUID.fromString(bodyMemberIdOpt);
      } catch (Exception ex) {
        throw new MemberException.MemberIdInvalid();
      }
      if (!bodyId.equals(authMemberId)) throw new MemberException.MemberIdMismatch();
    }
    return memberRepository
        .findStatusById(authMemberId)
        .orElseThrow(MemberException.MemberNotFound::new);
  }
}
