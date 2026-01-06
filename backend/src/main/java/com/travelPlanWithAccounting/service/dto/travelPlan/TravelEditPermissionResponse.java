package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.util.UUID;

import com.travelPlanWithAccounting.service.dto.travelPlan.enums.PermissionGrantedBy;
import com.travelPlanWithAccounting.service.dto.travelPlan.enums.PermissionReason;

public record TravelEditPermissionResponse(
    UUID travelMainId,
    boolean canEdit,
    PermissionReason reason,
    PermissionGrantedBy grantedBy
) {

}
