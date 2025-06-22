package com.travelPlanWithAccounting.service.dto.otp;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpStatusResponse {
  private boolean exists;
  private Boolean expired;
  private Integer attemptCount;
  private Boolean verified;
  private LocalDateTime expiryTime;
}
