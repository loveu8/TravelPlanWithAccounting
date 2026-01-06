package com.travelPlanWithAccounting.service.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.travelPlanWithAccounting.service.dto.travelPlan.TravelEditPermissionRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelEditPermissionResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.enums.PermissionGrantedBy;
import com.travelPlanWithAccounting.service.dto.travelPlan.enums.PermissionReason;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.exception.TravelException;
import com.travelPlanWithAccounting.service.repository.TravelMainRepository;
import com.travelPlanWithAccounting.service.repository.TravelPermissionsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelPermissionService {
    private final TravelMainRepository travelMainRepository;
    private final TravelPermissionsRepository travelPermissionsRepository;

    public TravelEditPermissionResponse checkEditPermission(
      TravelEditPermissionRequest request, UUID memberId){
        if (request == null || request.travelMainId() == null) {
            throw new TravelException.TravelMainIdRequired();
        }

        UUID travelMainId = request.travelMainId();
        TravelMain travelMain =
        travelMainRepository
            .findById(travelMainId)
            .orElseThrow(TravelException.TravelMainNotFound::new);

        if (memberId == null) {
            return new TravelEditPermissionResponse(
          travelMainId, false, PermissionReason.NO_AUTH, PermissionGrantedBy.NONE);
        }

        if (memberId.equals(travelMain.getCreatedBy())) {
            return new TravelEditPermissionResponse(
            travelMainId, true, PermissionReason.ALLOWED, PermissionGrantedBy.OWNER);
        }

        boolean hasPermission =
            travelPermissionsRepository.existsByTravelMainIdAndMemberIdAndPermissionsTrue(
                travelMainId, memberId);

        if (hasPermission) {
            return new TravelEditPermissionResponse(
            travelMainId, true, PermissionReason.ALLOWED, PermissionGrantedBy.PERMISSION);
        }

        return new TravelEditPermissionResponse(
            travelMainId, false, PermissionReason.NOT_ALLOWED, PermissionGrantedBy.NONE);
    }
}

