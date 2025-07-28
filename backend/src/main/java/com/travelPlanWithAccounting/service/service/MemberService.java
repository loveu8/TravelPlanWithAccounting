package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.member.MemberRegisterRequest;
import com.travelPlanWithAccounting.service.dto.member.MemberResponse;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.exception.MemberException;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.util.EmailValidatorUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 會員服務，處理會員註冊、登入等相關業務邏輯。 Registration / Login requires purpose-aware OTP token verification. */
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final OtpCacheService otpCacheService;

  /** 會員註冊。 流程：Email 驗證 → 檢查重複 → 驗證 OTP（purpose=REGISTRATION）→ 建立會員 → 使 OTP token 失效 → 回傳結果 */
  @Transactional
  public MemberResponse register(MemberRegisterRequest req) {
    validateEmail(req.getEmail());
    checkEmailDuplicate(req.getEmail());

    // 驗證註冊用 OTP（001）
    validateOtpToken(req.getOtpToken(), req.getEmail(), OtpPurpose.REGISTRATION);

    Member member =
        Member.builder()
            .givenName(req.getGivenName())
            .familyName(req.getFamilyName())
            .nickName(req.getNickName())
            .birthday(req.getBirthday())
            .email(req.getEmail())
            .subscribe(false)
            .status(Short.valueOf("1"))
            .build();

    Member saved = memberRepository.save(member);

    // 用過即失效（避免重放）
    otpCacheService.evictOtpVerifiedToken(req.getOtpToken(), OtpPurpose.REGISTRATION);

    String jwt = generateJwt(saved); // TODO: 後續整合 JwtUtil + RefreshTokenService，回傳 PRD 規格
    return new MemberResponse(saved.getEmail(), jwt);
  }

  /** 驗證 email 是否為空/格式 */
  private void validateEmail(String email) {
    if (email == null || email.isEmpty()) {
      throw new MemberException.EmailRequired();
    }
    if (!EmailValidatorUtil.isValid(email)) {
      throw new MemberException.EmailFormatInvalid();
    }
  }

  /** 檢查 email 是否已存在 */
  private void checkEmailDuplicate(String email) {
    if (memberRepository.existsByEmail(email)) {
      throw new MemberException.EmailAlreadyExists();
    }
  }

  /** 驗證 OTP token（purpose-aware）。 僅當 token 綁定的 email 與傳入 email 相同，且 purpose 一致時才視為有效。 */
  private void validateOtpToken(String otpToken, String email, OtpPurpose purpose) {
    String verifiedEmail = otpCacheService.getOtpVerifiedEmailByToken(otpToken, purpose);
    if (verifiedEmail == null || !verifiedEmail.equals(email)) {
      throw new MemberException.OtpTokenInvalid();
    }
  }

  /** 會員登入。 流程：Email 驗證 → 驗證 OTP（purpose=LOGIN）→ 查會員 → 使 OTP token 失效 → 回傳結果 */
  @Transactional(readOnly = true)
  public MemberResponse login(String email, String otpToken) {
    validateEmail(email);

    // 驗證登入用 OTP（002）
    validateOtpToken(otpToken, email, OtpPurpose.LOGIN);

    Member member =
        memberRepository.findByEmail(email).orElseThrow(MemberException.EmailNotFound::new);

    // 用過即失效（避免重放）
    otpCacheService.evictOtpVerifiedToken(otpToken, OtpPurpose.LOGIN);

    String jwt = generateJwt(member); // TODO: 後續整合 JwtUtil + RefreshTokenService，回傳 PRD 規格
    return new MemberResponse(member.getEmail(), jwt);
  }

  // 範例 JWT 產生方法（請依實際專案替換）
  private String generateJwt(Member member) {
    // TODO: 串接 JWT 工具類別（之後回傳 AuthEnvelope + cookies）
    return "mock-jwt-token";
  }

  /** 驗證：body.memberId (若不為 null) 必須與 authMemberId 相同；會員必須存在且為 active。 */
  public Member assertActiveMember(UUID authMemberId, String bodyMemberIdOpt) {
    if (bodyMemberIdOpt != null) {
      UUID bodyId;
      try {
        bodyId = UUID.fromString(bodyMemberIdOpt);
      } catch (Exception ex) {
        throw new MemberException.MemberIdInvalid();
      }
      if (!bodyId.equals(authMemberId)) {
        throw new MemberException.MemberIdMismatch();
      }
    }
    return memberRepository
        .findStatusById(authMemberId)
        .orElseThrow(MemberException.MemberNotFound::new);
  }

  // ---------------- Backward-compatible Helper（可留可移除） ----------------

  /**
   * @deprecated 舊版不帶 purpose 的驗證，預設視為 LOGIN。 為降低改動面積暫留；新程式請改用帶 purpose 的 validateOtpToken。
   */
  @Deprecated
  @SuppressWarnings("unused")
  private void validateOtpToken(String otpToken, String email) {
    validateOtpToken(otpToken, email, OtpPurpose.LOGIN);
  }
}
