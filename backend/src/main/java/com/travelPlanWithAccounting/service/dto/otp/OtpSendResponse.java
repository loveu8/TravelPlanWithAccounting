package com.travelPlanWithAccounting.service.dto.otp;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpSendResponse implements Serializable {
  private static final long serialVersionUID = 1L;
  private String email;
}
