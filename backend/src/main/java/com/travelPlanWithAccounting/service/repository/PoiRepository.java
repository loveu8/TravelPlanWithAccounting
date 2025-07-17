package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.Poi;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;import jakarta.persistence.LockModeType;

@Repository
public interface PoiRepository extends JpaRepository<Poi,UUID> {

  Optional<Poi> findByExternalId(String externalId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Poi p where p.externalId=:ext")
  Optional<Poi> lockByExternalId(@Param("ext") String externalId);
}