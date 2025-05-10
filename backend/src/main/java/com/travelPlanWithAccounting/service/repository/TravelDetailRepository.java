package com.travelPlanWithAccounting.service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelPlanWithAccounting.service.entity.TravelDetail;

@Repository
public interface TravelDetailRepository extends JpaRepository<TravelDetail, UUID> {
    List<TravelDetail> findByTravelMainId(UUID travelMainId);
    List<TravelDetail> findByTravelDateId(UUID travelDateId);
}
