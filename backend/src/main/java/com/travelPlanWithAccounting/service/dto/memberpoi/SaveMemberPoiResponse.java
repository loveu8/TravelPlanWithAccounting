package com.travelPlanWithAccounting.service.dto.memberpoi;

import java.util.UUID;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SaveMemberPoiResponse {
  private int code;              // 1 success, 0 fail
  private String desc;           // message
  private UUID poiId;            // nullable if fail
  private boolean poiCreated;    // true if new poi inserted
  private boolean langInserted;  // true if new i18n inserted
  private boolean alreadySaved;  // true if member_poi existed before
}