package com.travelPlanWithAccounting.service.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class OtpData {
  private String otpCode;
  private String email;
  private LocalDateTime expiryTime;
  private UUID token;
  private int attemptCount;
  private boolean verified;
  private LocalDateTime lastSentTime;

  public OtpData(String otpCode, String email, LocalDateTime expiryTime, UUID token) {
    this.otpCode = otpCode;
    this.email = email;
    this.expiryTime = expiryTime;
    this.token = token;
    this.attemptCount = 0;
    this.verified = false;
    this.lastSentTime = null;
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

  public UUID getToken() {
    return token;
  }

  public void setToken(UUID token) {
    this.token = token;
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

  public LocalDateTime getLastSentTime() {
    return lastSentTime;
  }

  public void setLastSentTime(LocalDateTime lastSentTime) {
    this.lastSentTime = lastSentTime;
  }

  public void incrementAttemptCount() {
    this.attemptCount++;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiryTime);
  }
}
