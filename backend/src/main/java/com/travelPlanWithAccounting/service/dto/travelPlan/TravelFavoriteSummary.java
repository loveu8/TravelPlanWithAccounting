package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelFavoriteSummary {
    private UUID travelMainId;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPrivate;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant favoritedAt;
    private UUID ownerMemberId;
}
