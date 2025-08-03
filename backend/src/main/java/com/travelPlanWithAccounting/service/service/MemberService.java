package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.exception.InvalidOtpException;
import com.travelPlanWithAccounting.service.exception.MemberException;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.service.RefreshTokenService;
import com.travelPlanWithAccounting.service.util.EmailValidatorUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 會員相關服務。
 *
 * <p>參考 {@link com.travelPlanWithAccounting.service.service.SearchService} 的錯誤處理模式， 以一致的方式拋出對應的
 * {@link MemberException} 或 {@link InvalidOtpException}。
 */
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final OtpService otpService;
  private final RefreshTokenService refreshTokenService;

  /** 判斷 purpose 並發送 OTP */
  @Transactional
  public PreAuthFlowResponse preAuthFlow(PreAuthFlowRequest req) {
    validatePreAuthReq(req);
    String email = req.getEmail();
    boolean exists = memberRepository.existsByEmail(email);
    OtpPurpose purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;
    otpService.generateOtp(email, purpose);
    return new PreAuthFlowResponse(email, exists, purpose.name(), purpose.actionCode());
  }

  /** 驗證 OTP → 登入或註冊 → 回 cookies envelope（AT/RT） */
  @Transactional
  public AuthResponse authFlow(AuthFlowRequest req) {
    validateAuthReq(req);
    String email = req.getEmail();

    boolean exists = memberRepository.existsByEmail(email);
    OtpPurpose purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;

    try {
      otpService.verifyOtp(email, req.getOtpCode(), purpose);
    } catch (InvalidOtpException ex) {
      throw new MemberException.OtpTokenInvalid();
    }

    Member member =
        exists
            ? memberRepository.findByEmail(email).orElseThrow(MemberException.EmailNotFound::new)
            : memberRepository.save(
                Member.builder()
                    .email(email)
                    .status(Short.valueOf("1"))
                    .subscribe(false)
                    .givenName(req.getGivenName())
                    .familyName(req.getFamilyName())
                    .nickName(req.getNickName())
                    .birthday(req.getBirthday())
                    .build());

    String clientId = resolveClientId(req.getClientId());
    return refreshTokenService.issueForMember(member.getId(), clientId, req.getIp(), req.getUa());
  }

  private String resolveClientId(String clientId) {
    return (clientId == null || clientId.isBlank()) ? "web" : clientId;
  }

  private void validateEmail(String email) {
    if (email == null || email.isEmpty()) throw new MemberException.EmailRequired();
    if (!EmailValidatorUtil.isValid(email)) throw new MemberException.EmailFormatInvalid();
  }

  private void validatePreAuthReq(PreAuthFlowRequest req) {
    if (req == null) throw new MemberException.EmailRequired();
    validateEmail(req.getEmail());
  }

  private void validateAuthReq(AuthFlowRequest req) {
    if (req == null) throw new MemberException.EmailRequired();
    validateEmail(req.getEmail());
    if (req.getOtpCode() == null || req.getOtpCode().isBlank()) {
      throw new MemberException.OtpTokenInvalid();
    }
  }

  /** 仍保留原有方法（若其他地方共用） */
  @Transactional(readOnly = true)
  public Member assertActiveMember(UUID memberId) {
    return memberRepository
        .findStatusById(memberId)
        .orElseThrow(MemberException.MemberNotFound::new);
  }
}
