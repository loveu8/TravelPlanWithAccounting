package com.travelPlanWithAccounting.service.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpInfo implements Serializable {
  private String code;
  private OtpPurpose purpose;
  private LocalDateTime expiresAt;
  private int attempts;
}
