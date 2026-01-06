package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.exception.EmailSendException;
import com.travelPlanWithAccounting.service.model.OtpPurpose;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  /** SMTP 寄件帳號（from） */
  @Value("${spring.mail.username:}")
  private String username;

  /** OTP 有效時間（分鐘），避免把 10 分鐘寫死 */
  @Value("${otp.expiry-minutes:10}")
  private int otpExpiryMinutes;

  /** （可選）寄件名稱顯示 */
  @Value("${app.mail.from-name:Travel Mate}")
  private String fromName;

  public void sendOtp(String email, String code, OtpPurpose purpose) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      // 若想顯示寄件者名稱，可使用 "name <address>" 形式
      String from =
          (fromName != null && !fromName.isBlank())
              ? String.format("%s <%s>", fromName, username)
              : username;

      helper.setFrom(from);
      helper.setTo(email);
      helper.setSubject(getOtpSubject(purpose));
      helper.setText(getOtpEmailBody(code, purpose, otpExpiryMinutes), true);

      mailSender.send(message);
    } catch (Exception e) {
      throw new EmailSendException(e);
    }
  }

  public void sendMagicLink(String email, String magicLink) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      String from =
          (fromName != null && !fromName.isBlank())
              ? String.format("%s <%s>", fromName, username)
              : username;

      helper.setFrom(from);
      helper.setTo(email);
      helper.setSubject("Login to Your App");
      helper.setText(getMagicLinkEmailBody(magicLink), true);

      mailSender.send(message);
    } catch (Exception e) {
      throw new EmailSendException(e);
    }
  }

  private String getOtpSubject(OtpPurpose purpose) {
    switch (purpose) {
      case REGISTRATION:
        return "Complete Your Registration";
      case LOGIN:
        return "Login Verification Code";
      case GUEST_LOGIN:
        return "Guest Login Verification Code";
      case EMAIL_CHANGE:
        return "Verify Email Change";
      case IDENTITY_VERIFICATION:
        return "Identity Verification";
      default:
        return "Your Verification Code";
    }
  }

  /** HTML 內容將顯示對應用途與有效時間（分鐘） */
  private String getOtpEmailBody(String code, OtpPurpose purpose, int expiryMinutes) {
    String purposeText = getPurposeDisplay(purpose); // 顯示用途字串
    return String.format(
        """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2>Your Verification Code</h2>
          <p>Here is your verification code for <strong>%s</strong>:</p>
          <div style="background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 24px; letter-spacing: 5px;">
            <strong>%s</strong>
          </div>
          <p>This code will expire in %d minutes.</p>
          <p>If you didn't request this code, please ignore this email.</p>
        </div>
        """,
        purposeText, code, expiryMinutes);
  }

  private String getMagicLinkEmailBody(String magicLink) {
    return String.format(
        """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
          <h2>Login to Your Account</h2>
          <p>Click the button below to login to your account:</p>
          <div style="text-align: center; margin: 30px 0;">
            <a href="%s" style="background-color: #4CAF50; color: white; padding: 12px 24px; text-decoration: none; border-radius: 4px;">
              Login Now
            </a>
          </div>
          <p>Or copy and paste this link in your browser:</p>
          <p style="word-break: break-all;"><small>%s</small></p>
          <p>This link will expire in 15 minutes.</p>
          <p>If you didn't request this login link, please ignore this email.</p>
        </div>
        """,
        magicLink, magicLink);
  }

  /** 讓 email 內顯示用途更友善（之後可加 i18n） */
  private String getPurposeDisplay(OtpPurpose purpose) {
    switch (purpose) {
      case REGISTRATION:
        return "registration";
      case LOGIN:
        return "login";
      case GUEST_LOGIN:
        return "guest login";
      case EMAIL_CHANGE:
        return "email change";
      case IDENTITY_VERIFICATION:
        return "identity verification";
      default:
        return "verification";
    }
  }
}
