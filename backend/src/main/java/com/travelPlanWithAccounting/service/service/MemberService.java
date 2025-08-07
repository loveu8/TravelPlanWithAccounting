package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.member.AuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.EmailChangeOtpRequest;
import com.travelPlanWithAccounting.service.dto.member.EmailChangeRequest;
import com.travelPlanWithAccounting.service.dto.member.EmailChangeResponse;
import com.travelPlanWithAccounting.service.dto.member.IdentityOtpVerifyRequest;
import com.travelPlanWithAccounting.service.dto.member.IdentityOtpVerifyResponse;
import com.travelPlanWithAccounting.service.dto.member.MemberProfileResponse;
import com.travelPlanWithAccounting.service.dto.member.MemberProfileUpdateRequest;
import com.travelPlanWithAccounting.service.dto.member.OtpTokenResponse;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowRequest;
import com.travelPlanWithAccounting.service.dto.member.PreAuthFlowResponse;
import com.travelPlanWithAccounting.service.entity.AuthInfo;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.entity.Setting;
import com.travelPlanWithAccounting.service.exception.InvalidOtpException;
import com.travelPlanWithAccounting.service.exception.MemberException;
import com.travelPlanWithAccounting.service.model.OtpData;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import com.travelPlanWithAccounting.service.repository.AuthInfoRepository;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import com.travelPlanWithAccounting.service.repository.SettingRepository;
import com.travelPlanWithAccounting.service.security.JwtUtil;
import com.travelPlanWithAccounting.service.util.EmailValidatorUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
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
  private final AuthInfoRepository authInfoRepository;
  private final MessageSource messageSource;

  /** 判斷 purpose 並發送 OTP */
  @Transactional
  public PreAuthFlowResponse preAuthFlow(PreAuthFlowRequest req) {
    validatePreAuthReq(req);
    String email = req.getEmail();
    boolean exists = memberRepository.existsByEmail(email);
    OtpPurpose purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;
    OtpData otpData = otpService.generateOtp(email, purpose);
    return new PreAuthFlowResponse(email, exists, purpose.name(), purpose.actionCode(), otpData.getToken().toString());
  }

  /** 驗證 OTP → 登入或註冊 → 回 cookies envelope（AT/RT） */
  @Transactional
  public AuthResponse authFlow(AuthFlowRequest req) {
    validateAuthReq(req);
    String email = req.getEmail();

    boolean exists = memberRepository.existsByEmail(email);
    OtpPurpose purpose = exists ? OtpPurpose.LOGIN : OtpPurpose.REGISTRATION;

    AuthInfo authInfo;
    Member member;
    if (exists) {
      try {
        authInfo = otpService.verifyOtp(req.getToken(), req.getOtpCode(), purpose);
      } catch (InvalidOtpException ex) {
        throw new MemberException.OtpTokenInvalid();
      }
      if (!authInfo.getEmail().equals(email)) {
        throw new MemberException.OtpTokenInvalid();
      }
      member =
          memberRepository.findByEmail(email).orElseThrow(MemberException.EmailNotFound::new);
    } else {
      member =
          Member.builder()
              .email(email)
              .status(Short.valueOf("1"))
              .subscribe(false)
              .build();
      MemberProfileUpdateRequest profileReq = new MemberProfileUpdateRequest();
      profileReq.setGivenName(req.getGivenName());
      profileReq.setFamilyName(req.getFamilyName());
      profileReq.setNickName(req.getNickName());
      profileReq.setBirthday(req.getBirthday());
      profileReq.setLangType(req.getLangType());
      validateAndApplyProfile(member, profileReq);
      try {
        authInfo = otpService.verifyOtp(req.getToken(), req.getOtpCode(), purpose);
      } catch (InvalidOtpException ex) {
        throw new MemberException.OtpTokenInvalid();
      }
      if (!authInfo.getEmail().equals(email)) {
        throw new MemberException.OtpTokenInvalid();
      }
      memberRepository.save(member);
    }
    
    String clientId = resolveClientId(req.getClientId());
    return refreshTokenService.issueForMember(member.getId(), clientId, req.getIp(), req.getUa());
  }

  /** 發送舊信箱 OTP */
  @Transactional
  public OtpTokenResponse sendIdentityOtp(String authHeader) {
    UUID memberId = resolveMemberId(authHeader);
    Member member =
        memberRepository.findById(memberId).orElseThrow(MemberException.MemberNotFound::new);
    if (member.getStatus() == null || member.getStatus() != 1) {
      throw new MemberException.MemberNotActive();
    }
    OtpData otpData = otpService.generateOtp(member.getEmail(), OtpPurpose.IDENTITY_VERIFICATION);
    return new OtpTokenResponse(
        otpData.getToken().toString(), otpData.getExpiryTime().atOffset(ZoneOffset.UTC));
  }

  /** 驗證舊信箱 OTP，回傳 identity token */
  @Transactional
  public IdentityOtpVerifyResponse verifyIdentityOtp(
      String authHeader, IdentityOtpVerifyRequest req) {
    UUID memberId = resolveMemberId(authHeader);
    Member member =
        memberRepository.findById(memberId).orElseThrow(MemberException.MemberNotFound::new);
    if (member.getStatus() == null || member.getStatus() != 1) {
      throw new MemberException.MemberNotActive();
    }
    AuthInfo authInfo;
    try {
      authInfo =
          otpService.verifyOtp(
              req.getOtpToken(), req.getOtpCode(), OtpPurpose.IDENTITY_VERIFICATION);
    } catch (InvalidOtpException ex) {
      throw new MemberException.OtpTokenInvalid();
    }
    if (!authInfo.getMemberId().equals(member.getId())) {
      throw new MemberException.OtpTokenInvalid();
    }
    return new IdentityOtpVerifyResponse(req.getOtpToken(), authInfo.getExpireAt(), true);
  }

  /** 發送新信箱 OTP */
  @Transactional
  public OtpTokenResponse sendEmailChangeOtp(
      String authHeader, EmailChangeOtpRequest req) {
    UUID memberId = resolveMemberId(authHeader);
    Member member =
        memberRepository.findById(memberId).orElseThrow(MemberException.MemberNotFound::new);
    if (member.getStatus() == null || member.getStatus() != 1) {
      throw new MemberException.MemberNotActive();
    }
    validateEmail(req.getEmail());
    if (memberRepository.existsByEmail(req.getEmail())) {
      throw new MemberException.EmailAlreadyExists();
    }
    UUID identityId;
    try {
      identityId = UUID.fromString(req.getIdentityOtpToken());
    } catch (IllegalArgumentException e) {
      throw new MemberException.OtpTokenInvalid();
    }
    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    AuthInfo identityAuth =
        authInfoRepository
            .findByIdAndActionAndValidationTrueAndExpireAtAfter(
                identityId, OtpPurpose.IDENTITY_VERIFICATION.actionCode(), nowUtc)
            .orElseThrow(MemberException.OtpTokenInvalid::new);
    if (!identityAuth.getMemberId().equals(member.getId())
        || identityAuth.getUpdatedAt() == null
        || !identityAuth.getUpdatedAt().plusMinutes(10).isAfter(nowUtc)) {
      throw new MemberException.OtpTokenInvalid();
    }
    OtpData otpData = otpService.generateOtp(req.getEmail(), OtpPurpose.EMAIL_CHANGE);
    return new OtpTokenResponse(
        otpData.getToken().toString(), otpData.getExpiryTime().atOffset(ZoneOffset.UTC));
  }

  /** 更新信箱 */
  @Transactional
  public EmailChangeResponse changeEmail(String authHeader, EmailChangeRequest req) {
    UUID memberId = resolveMemberId(authHeader);
    Member member =
        memberRepository.findById(memberId).orElseThrow(MemberException.MemberNotFound::new);
    if (member.getStatus() == null || member.getStatus() != 1) {
      throw new MemberException.MemberNotActive();
    }
    UUID identityId;
    try {
      identityId = UUID.fromString(req.getIdentityOtpToken());
    } catch (IllegalArgumentException e) {
      throw new MemberException.OtpTokenInvalid();
    }
    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    AuthInfo identityAuth =
        authInfoRepository
            .findByIdAndActionAndValidationTrueAndExpireAtAfter(
                identityId, OtpPurpose.IDENTITY_VERIFICATION.actionCode(), nowUtc)
            .orElseThrow(MemberException.OtpTokenInvalid::new);
    if (!identityAuth.getMemberId().equals(member.getId())
        || identityAuth.getUpdatedAt() == null
        || !identityAuth.getUpdatedAt().plusMinutes(10).isAfter(nowUtc)) {
      throw new MemberException.OtpTokenInvalid();
    }
    AuthInfo emailAuth;
    try {
      emailAuth =
          otpService.verifyOtp(req.getOtpToken(), req.getOtpCode(), OtpPurpose.EMAIL_CHANGE);
    } catch (InvalidOtpException ex) {
      throw new MemberException.OtpTokenInvalid();
    }
    String newEmail = emailAuth.getEmail();
    if (memberRepository.existsByEmail(newEmail)) {
      throw new MemberException.EmailAlreadyExists();
    }
    member.setEmail(newEmail);
    memberRepository.save(member);
    identityAuth.setExpireAt(nowUtc);
    emailAuth.setExpireAt(nowUtc);
    authInfoRepository.save(identityAuth);
    authInfoRepository.save(emailAuth);
    return new EmailChangeResponse(true);
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
    if (req.getOtpCode() == null
        || req.getOtpCode().isBlank()
        || req.getToken() == null
        || req.getToken().isBlank()) {
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
    validateAndApplyProfile(member, req);
    memberRepository.save(member);
    return toProfileResponse(member);
  }

  private void validateAndApplyProfile(Member member, MemberProfileUpdateRequest req) {
    String reqLang = req.getLangType();
    Locale locale = Locale.forLanguageTag(reqLang == null ? "zh-TW" : reqLang);
    if (locale.getLanguage().isEmpty()) {
      locale = Locale.forLanguageTag("zh-TW");
    }
    Locale prevLocale = LocaleContextHolder.getLocale();
    LocaleContextHolder.setLocale(locale);
    try {
      Map<String, String> fieldErrors = validateProfile(req, locale);
      if (!fieldErrors.isEmpty()) {
        throw new MemberException.ProfileFieldsInvalid(fieldErrors);
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
      } else if (member.getLangType() == null) {
        member.setLangType("001");
      }
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
    String code = member.getLangType() == null ? "001" : member.getLangType();
    String langType =
        settingRepository
            .findByCategoryAndCodeName("LANG_TYPE", code)
            .map(Setting::getName)
            .orElse(code);
    return new MemberProfileResponse(
        member.getGivenName(),
        member.getFamilyName(),
        member.getNickName(),
        member.getBirthday(),
        member.getSubscribe(),
        langType,
        member.getEmail());
  }

  private Map<String, String> validateProfile(MemberProfileUpdateRequest req, Locale locale) {
    Map<String, String> errors = new LinkedHashMap<>();
    Pattern namePattern = Pattern.compile("^[\\p{L}0-9\\s]+$");

    if (req.getGivenName() != null) {
      List<String> fieldErrors = new ArrayList<>();
      if (req.getGivenName().length() > 30)
        fieldErrors.add(
            messageSource.getMessage("member.profile.givenName.length", null, locale));
      if (!namePattern.matcher(req.getGivenName()).matches())
        fieldErrors.add(
            messageSource.getMessage("member.profile.givenName.invalid", null, locale));
      if (!fieldErrors.isEmpty()) errors.put("givenName", String.join("; ", fieldErrors));
    }

    if (req.getFamilyName() != null) {
      List<String> fieldErrors = new ArrayList<>();
      if (req.getFamilyName().length() > 30)
        fieldErrors.add(
            messageSource.getMessage("member.profile.familyName.length", null, locale));
      if (!namePattern.matcher(req.getFamilyName()).matches())
        fieldErrors.add(
            messageSource.getMessage("member.profile.familyName.invalid", null, locale));
      if (!fieldErrors.isEmpty()) errors.put("familyName", String.join("; ", fieldErrors));
    }

    if (req.getNickName() != null) {
      List<String> fieldErrors = new ArrayList<>();
      if (req.getNickName().length() > 30)
        fieldErrors.add(
            messageSource.getMessage("member.profile.nickName.length", null, locale));
      if (!namePattern.matcher(req.getNickName()).matches())
        fieldErrors.add(
            messageSource.getMessage("member.profile.nickName.invalid", null, locale));
      if (!fieldErrors.isEmpty()) errors.put("nickName", String.join("; ", fieldErrors));
    }

    if (req.getLangType() != null) {
      List<String> fieldErrors = new ArrayList<>();
      if (req.getLangType().length() > 10) {
        fieldErrors.add(
            messageSource.getMessage("member.profile.langType.length", null, locale));
      } else if (settingRepository
          .findByCategoryAndName("LANG_TYPE", req.getLangType())
          .isEmpty()) {
        fieldErrors.add(
            messageSource.getMessage("member.profile.langType.invalid", null, locale));
      }
      if (!fieldErrors.isEmpty()) errors.put("langType", String.join("; ", fieldErrors));
    }

    return errors;
  }
}
