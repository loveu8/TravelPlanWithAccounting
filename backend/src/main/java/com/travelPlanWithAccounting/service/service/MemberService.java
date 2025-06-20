package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.member.MemberRegisterRequest;
import com.travelPlanWithAccounting.service.dto.member.MemberRegisterResponse;
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.exception.MemberException;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 會員服務，處理會員註冊等相關業務邏輯。<br>
 * Service for member operations, including registration.<br>
 *
 * <p>註冊需經過 OTP 驗證，並檢查 email 是否重複。<br>
 * Registration requires OTP verification and email duplication check.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

  private final MemberRepository memberRepository;
  private final OtpCacheService otpCacheService;

  /**
   * 會員註冊。<br>
   * Register a new member.<br>
   *
   * <p>必須先通過 OTP 驗證並取得驗證 token，註冊時 email、givenName、familyName、nickName、birthday 為可填欄位。<br>
   * Must pass OTP verification and provide the token. Only required fields are included.
   *
   * @param req 會員註冊請求 (Member registration request)
   * @return 註冊成功的會員資料與預留 JWT 欄位 (Registered member and reserved JWT field)
   * @throws MemberException.EmailRequired 若 email 未填 (if email is missing)
   * @throws MemberException.EmailAlreadyExists 若 email 已存在 (if email already exists)
   * @throws MemberException.OtpTokenInvalid 若 OTP token 驗證失敗 (if OTP token is invalid)
   */
  @Transactional
  public MemberRegisterResponse register(MemberRegisterRequest req) {
    validateEmail(req.getEmail());
    checkEmailDuplicate(req.getEmail());
    validateOtpToken(req.getOtpToken(), req.getEmail());
    Member member =
        Member.builder()
            .givenName(req.getGivenName())
            .familyName(req.getFamilyName())
            .nickName(req.getNickName())
            .birthday(req.getBirthday())
            .email(req.getEmail())
            .subscribe(false) // 預設不訂閱 (default not subscribed)
            .status(Short.valueOf("1")) // 預設狀態為啟用 (default status is enabled)
            .build();
    Member saved = memberRepository.save(member);
    otpCacheService.evictOtpVerifiedToken(req.getOtpToken());
    String jwt = null; // TODO: 產生 JWT 並回傳
    return new MemberRegisterResponse(saved.getEmail(), jwt);
  }

  /**
   * 驗證 email 是否為空。<br>
   * Validate that email is not empty.<br>
   *
   * @param email 電子郵件 (email)
   */
  private void validateEmail(String email) {
    if (email == null || email.isEmpty()) {
      throw new MemberException.EmailRequired();
    }
  }

  /**
   * 檢查 email 是否已存在。<br>
   * Check if email already exists.<br>
   *
   * @param email 電子郵件 (email)
   */
  private void checkEmailDuplicate(String email) {
    if (memberRepository.existsByEmail(email)) {
      throw new MemberException.EmailAlreadyExists();
    }
  }

  /**
   * 驗證 OTP token 是否有效。<br>
   * Validate OTP token.<br>
   *
   * @param otpToken OTP 驗證 token (OTP verification token)
   * @param email 電子郵件 (email)
   */
  private void validateOtpToken(String otpToken, String email) {
    String verifiedEmail = otpCacheService.getOtpVerifiedEmailByToken(otpToken);
    if (verifiedEmail == null || !verifiedEmail.equals(email)) {
      throw new MemberException.OtpTokenInvalid();
    }
  }
}
