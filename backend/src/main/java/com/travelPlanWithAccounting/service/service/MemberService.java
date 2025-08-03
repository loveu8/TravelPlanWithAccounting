package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.auth.VerifyTokenResponse;
import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.dto.member.VerifyTokenRequest;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.entity.RefreshToken;
import com.travelPlanWithAccounting.service.exception.InvalidOtpException;
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
  private final JwtUtil jwtUtil;
  private final RefreshTokenRepository refreshTokenRepository;

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

  @Transactional(readOnly = true)
  public VerifyTokenResponse verifyToken(VerifyTokenRequest req) {
    String tokenTypeStr =
        req != null && req.getTokenType() != null ? String.valueOf(req.getTokenType()) : null;
    if (req == null
        || req.getTokenType() == null
        || req.getToken() == null
        || req.getToken().isBlank()) {
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType(tokenTypeStr)
          .reason("REQ_INVALID")
          .build();
    }
    return switch (req.getTokenType()) {
      case ACCESS -> verifyAccessToken(req.getToken());
      case REFRESH -> verifyRefreshToken(req.getToken(), req.getClientId());
      default ->
          VerifyTokenResponse.builder()
              .valid(false)
              .tokenType(String.valueOf(req.getTokenType()))
              .reason("UNSUPPORTED_TYPE")
              .build();
    };
  }

  private VerifyTokenResponse verifyAccessToken(String token) {
    try {
      Jws<Claims> jws = jwtUtil.verify(token);
      Claims c = jws.getPayload();
      return VerifyTokenResponse.builder()
          .valid(true)
          .tokenType("ACCESS")
          .sub(UUID.fromString(c.getSubject()))
          .role(c.get("role", String.class))
          .exp(c.getExpiration().toInstant().getEpochSecond())
          .build();
    } catch (Exception e) {
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType("ACCESS")
          .reason("JWT_INVALID: " + e.getClass().getSimpleName())
          .build();
    }
  }

  private VerifyTokenResponse verifyRefreshToken(String token, String clientId) {
    String cId = resolveClientId(clientId);
    String hash = TokenUtil.sha256Base64Url(token);
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
      return VerifyTokenResponse.builder().valid(false).tokenType("REFRESH").reason("USED").build();
    if (rt.getExpiresAt().isBefore(now))
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType("REFRESH")
          .reason("EXPIRED")
          .build();
    if (clientId != null && !clientId.isBlank() && !rt.getClientId().equals(clientId))
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
  }
}
