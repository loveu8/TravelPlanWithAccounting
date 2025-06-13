package com.travelPlanWithAccounting.service.model;

import java.time.LocalDateTime;

public class OtpData {
  private String otpCode;
  private String email;
  private LocalDateTime expiryTime;
  private int attemptCount;
  private boolean verified;

  public OtpData(String otpCode, String email, LocalDateTime expiryTime) {
    this.otpCode = otpCode;
    this.email = email;
    this.expiryTime = expiryTime;
    this.attemptCount = 0;
    this.verified = false;
  }

  public String getOtpCode() {
    return otpCode;
  }

  public void setOtpCode(String otpCode) {
    this.otpCode = otpCode;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDateTime getExpiryTime() {
    return expiryTime;
  }

  public void setExpiryTime(LocalDateTime expiryTime) {
    this.expiryTime = expiryTime;
  }

  public int getAttemptCount() {
    return attemptCount;
  }

  public void setAttemptCount(int attemptCount) {
    this.attemptCount = attemptCount;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public void incrementAttemptCount() {
    this.attemptCount++;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiryTime);
  }
}
