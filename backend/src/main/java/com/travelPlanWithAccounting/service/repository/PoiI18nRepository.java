package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.PoiI18n;
import java.util.Optional;import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoiI18nRepository extends JpaRepository<PoiI18n,UUID> {
  Optional<PoiI18n> findByPoi_IdAndLangType(UUID poiId, String langType);
  boolean existsByPoi_IdAndLangType(UUID poiId, String langType);
}