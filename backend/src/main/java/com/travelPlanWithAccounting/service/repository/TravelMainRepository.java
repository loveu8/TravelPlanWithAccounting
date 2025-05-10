package com.travelPlanWithAccounting.service.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelPlanWithAccounting.service.entity.TravelMain;

@Repository
public interface TravelMainRepository extends JpaRepository<TravelMain, UUID>  {

}
