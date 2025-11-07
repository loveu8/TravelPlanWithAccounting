package com.travelPlanWithAccounting.service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelPlanWithAccounting.service.entity.TravelFav;

@Repository
public interface TravelFavRepository extends JpaRepository<TravelFav, UUID> {
    @EntityGraph(attributePaths = "travelMain")
    Page<TravelFav> findByMember_IdAndTravelMain_IsPrivateFalse(UUID memberId, Pageable pageable);
}
