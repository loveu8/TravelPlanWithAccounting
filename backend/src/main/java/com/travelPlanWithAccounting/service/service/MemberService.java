package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.MemberProfileResponse;
import com.travelPlanWithAccounting.service.dto.member.MemberProfileUpdateRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.entity.Setting;
import com.travelPlanWithAccounting.service.exception.InvalidOtpException;
import com.travelPlanWithAccounting.service.exception.MemberException;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.repository.SettingRepository;
import com.travelPlanWithAccounting.service.service.RefreshTokenService;
import com.travelPlanWithAccounting.service.util.EmailValidatorUtil;
import com.travelPlanWithAccounting.service.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
  private final SettingRepository settingRepository;
  private final MessageSource messageSource;

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

  /* ==================== 新增會員資料相關 API ==================== */

  /**
   * 查詢會員資料。
   */
  @Transactional(readOnly = true)
  public MemberProfileResponse getProfile(String authHeader) {
    UUID memberId = resolveMemberId(authHeader);
    Member member =
        memberRepository.findById(memberId).orElseThrow(MemberException.MemberNotFound::new);
    if (member.getStatus() == null || member.getStatus() != 1) {
      throw new MemberException.MemberNotActive();
    }
    return toProfileResponse(member);
  }

  /**
   * 修改會員資料。
   */
  @Transactional
  public MemberProfileResponse updateProfile(
      String authHeader, MemberProfileUpdateRequest req) {
    UUID memberId = resolveMemberId(authHeader);
    Member member =
        memberRepository.findById(memberId).orElseThrow(MemberException.MemberNotFound::new);
    if (member.getStatus() == null || member.getStatus() != 1) {
      throw new MemberException.MemberNotActive();
    }
    Locale locale =
        Locale.forLanguageTag(req.getLangType() == null ? "zh-TW" : req.getLangType());
    Locale prevLocale = LocaleContextHolder.getLocale();
    LocaleContextHolder.setLocale(locale);
    try {
      String errorMsg = validateProfile(req, locale);
      if (!errorMsg.isEmpty()) {
        throw new MemberException.ProfileFieldsInvalid(errorMsg);
      }

      if (req.getGivenName() != null) member.setGivenName(req.getGivenName());
      if (req.getFamilyName() != null) member.setFamilyName(req.getFamilyName());
      if (req.getNickName() != null) member.setNickName(req.getNickName());
      if (req.getBirthday() != null) member.setBirthday(req.getBirthday());
      if (req.getSubscribe() != null) member.setSubscribe(req.getSubscribe());
      if (req.getLangType() != null) {
        String code =
            settingRepository
                .findByCategoryAndName("LANG_TYPE", req.getLangType())
                .map(Setting::getCodeName)
                .orElse(member.getLangType());
        member.setLangType(code);
      }

      memberRepository.save(member);
      return toProfileResponse(member);
    } finally {
      LocaleContextHolder.setLocale(prevLocale);
    }
  }

  private UUID resolveMemberId(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new MemberException.AccessTokenInvalid();
    }
    String token = authHeader.substring(7);
    try {
      var jws = jwtUtil.verify(token);
      return UUID.fromString(jws.getPayload().getSubject());
    } catch (ExpiredJwtException ex) {
      throw new MemberException.AccessTokenExpired();
    } catch (JwtException | IllegalArgumentException ex) {
      throw new MemberException.AccessTokenInvalid();
    }
  }

  private MemberProfileResponse toProfileResponse(Member member) {
    String langType =
        settingRepository
            .findByCategoryAndCodeName("LANG_TYPE", member.getLangType())
            .map(Setting::getName)
            .orElse(member.getLangType());
    return new MemberProfileResponse(
        member.getGivenName(),
        member.getFamilyName(),
        member.getNickName(),
        member.getBirthday(),
        member.getSubscribe(),
        langType,
        member.getEmail());
  }

  private String validateProfile(MemberProfileUpdateRequest req, Locale locale) {
    List<String> errors = new ArrayList<>();
    Pattern namePattern = Pattern.compile("^[\\p{L}0-9\\s_-]{0,255}$");
    if (req.getGivenName() != null) {
      if (req.getGivenName().length() > 255)
        errors.add(messageSource.getMessage("member.profile.givenName.length", null, locale));
      if (!namePattern.matcher(req.getGivenName()).matches())
        errors.add(messageSource.getMessage("member.profile.givenName.invalid", null, locale));
    }
    if (req.getFamilyName() != null) {
      if (req.getFamilyName().length() > 255)
        errors.add(messageSource.getMessage("member.profile.familyName.length", null, locale));
      if (!namePattern.matcher(req.getFamilyName()).matches())
        errors.add(messageSource.getMessage("member.profile.familyName.invalid", null, locale));
    }
    if (req.getNickName() != null) {
      if (req.getNickName().length() > 255)
        errors.add(messageSource.getMessage("member.profile.nickName.length", null, locale));
      if (!namePattern.matcher(req.getNickName()).matches())
        errors.add(messageSource.getMessage("member.profile.nickName.invalid", null, locale));
    }
    if (req.getLangType() != null) {
      if (req.getLangType().length() > 10) {
        errors.add(messageSource.getMessage("member.profile.langType.length", null, locale));
      } else if (settingRepository.findByCategoryAndName("LANG_TYPE", req.getLangType()).isEmpty()) {
        errors.add(messageSource.getMessage("member.profile.langType.invalid", null, locale));
      }
    }
    return String.join("; ", errors);
  }
}
