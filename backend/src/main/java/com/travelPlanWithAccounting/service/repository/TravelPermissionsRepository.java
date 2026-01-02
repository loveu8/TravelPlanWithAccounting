package com.travelPlanWithAccounting.service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelPlanWithAccounting.service.entity.TravelPermissions;

public interface TravelPermissionsRepository extends JpaRepository<TravelPermissions, UUID> {
    boolean existsByTravelMainIdAndMemberIdAndPermissionsTrue(UUID travelMainId, UUID memberId);
}
