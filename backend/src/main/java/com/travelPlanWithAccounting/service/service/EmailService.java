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

  @Value("${spring.mail.username:}")
  private String username;

  public void sendOtp(String email, String code, OtpPurpose purpose) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(username);
      helper.setTo(email);
      helper.setSubject(getOtpSubject(purpose));
      helper.setText(getOtpEmailBody(code, purpose), true);

      mailSender.send(message);
    } catch (Exception e) {
      throw new EmailSendException(e);
    }
  }

  public void sendMagicLink(String email, String magicLink) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(username);
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
      case EMAIL_CHANGE:
        return "Verify Email Change";
      case IDENTITY_VERIFICATION:
        return "Identity Verification";
      default:
        return "Your Verification Code";
    }
  }

  private String getOtpEmailBody(String code, OtpPurpose purpose) {
    return String.format(
        """
        <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
            <h2>Your Verification Code</h2>
            <p>Here is your verification code for %s:</p>
            <div style="background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 24px; letter-spacing: 5px;">
                <strong>%s</strong>
            </div>
            <p>This code will expire in 10 minutes.</p>
            <p>If you didn't request this code, please ignore this email.</p>
        </div>
        """,
        purpose.toString().toLowerCase().replace("_", " "), code);
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
}
