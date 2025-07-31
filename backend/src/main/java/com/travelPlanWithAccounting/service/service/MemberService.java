package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.auth.VerifyTokenResponse;
import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.dto.member.VerifyTokenRequest;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.entity.RefreshToken;
import com.travelPlanWithAccounting.service.exception.MemberException;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.repository.RefreshTokenRepository;
import com.travelPlanWithAccounting.service.security.JwtUtil;
import com.travelPlanWithAccounting.service.util.EmailValidatorUtil;
import com.travelPlanWithAccounting.service.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final OtpService otpService; // 你已有
  private final RefreshTokenService refreshTokenService;
  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;

  /** 判斷 purpose 並發送 OTP */
  @Transactional
  public PreAuthFlowResponse preAuthFlow(PreAuthFlowRequest req) {
    String email = req.getEmail();
    validateEmail(email);
    boolean exists = memberRepository.existsByEmail(email);
    OtpPurpose purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;
    otpService.generateOtp(email, purpose); // 會寫 auth_info + 寄信
    return new PreAuthFlowResponse(email, exists, purpose.name(), purpose.actionCode());
  }

  /** 驗證 OTP → 登入或註冊 → 回 cookies envelope（AT/RT） */
  @Transactional
  public AuthResponse authFlow(AuthFlowRequest req) {
    String email = req.getEmail();
    validateEmail(email);

    boolean exists = memberRepository.existsByEmail(email);
    OtpPurpose purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;

    boolean ok = otpService.verifyOtp(email, req.getOtpCode(), purpose);
    if (!ok) throw new MemberException.OtpTokenInvalid();

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

    String clientId =
        (req.getClientId() == null || req.getClientId().isBlank()) ? "web" : req.getClientId();

    return refreshTokenService.issueForMember(member.getId(), clientId, req.getIp(), req.getUa());
  }

  private void validateEmail(String email) {
    if (email == null || email.isEmpty()) throw new MemberException.EmailRequired();
    if (!EmailValidatorUtil.isValid(email)) throw new MemberException.EmailFormatInvalid();
  }

  // 可選：提供保留的 login/register 舊 API 呼叫 authFlow 包裝（略）
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

  @Transactional(readOnly = true)
  public VerifyTokenResponse verifyToken(VerifyTokenRequest req) {
    switch (req.getTokenType()) {
      case ACCESS:
        try {
          Jws<Claims> jws = jwtUtil.verify(req.getToken());
          Claims c = jws.getPayload(); // 0.12.x: getPayload()
          UUID sub = UUID.fromString(c.getSubject());
          String role = c.get("role", String.class);
          Long exp = c.getExpiration().toInstant().getEpochSecond();
          return VerifyTokenResponse.builder()
              .valid(true)
              .tokenType("ACCESS")
              .sub(sub)
              .role(role)
              .exp(exp)
              .build();
        } catch (Exception e) {
          return VerifyTokenResponse.builder()
              .valid(false)
              .tokenType("ACCESS")
              .reason("JWT_INVALID: " + e.getClass().getSimpleName())
              .build();
        }

      case REFRESH:
        String clientId =
            (req.getClientId() == null || req.getClientId().isBlank()) ? "web" : req.getClientId();

        String hash = TokenUtil.sha256Base64Url(req.getToken());
        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHash(hash);
        if (opt.isEmpty()) {
          return VerifyTokenResponse.builder()
              .valid(false)
              .tokenType("REFRESH")
              .reason("NOT_FOUND")
              .build();
        }
        RefreshToken rt = opt.get();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        if (rt.isRevoked())
          return VerifyTokenResponse.builder()
              .valid(false)
              .tokenType("REFRESH")
              .reason("REVOKED")
              .build();
        if (rt.isUsed())
          return VerifyTokenResponse.builder()
              .valid(false)
              .tokenType("REFRESH")
              .reason("USED")
              .build();
        if (rt.getExpiresAt().isBefore(now))
          return VerifyTokenResponse.builder()
              .valid(false)
              .tokenType("REFRESH")
              .reason("EXPIRED")
              .build();
        if (!rt.getClientId().equals(clientId))
          return VerifyTokenResponse.builder()
              .valid(false)
              .tokenType("REFRESH")
              .reason("CLIENT_MISMATCH")
              .build();

        return VerifyTokenResponse.builder()
            .valid(true)
            .tokenType("REFRESH")
            .sub(rt.getOwnerId())
            .build();

      default:
        return VerifyTokenResponse.builder()
            .valid(false)
            .tokenType(String.valueOf(req.getTokenType()))
            .reason("UNSUPPORTED_TYPE")
            .build();
    }
  }
}
