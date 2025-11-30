package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record TravelEditPermissionRequest(@NotNull UUID travelMainId) {

}
