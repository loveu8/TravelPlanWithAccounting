package com.travelPlanWithAccounting.service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelPlanWithAccounting.service.entity.TravelDate;

@Repository
public interface TravelDateRepository extends JpaRepository<TravelDate, UUID> {
    List<TravelDate> findByTravelMainId(UUID travelMainId);

    
}
