package com.travelPlanWithAccounting.service.dto.otp;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpStatusResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private boolean exists;
  private Boolean expired;
  private Integer attemptCount;
  private Boolean verified;
  private LocalDateTime expiryTime;
}
