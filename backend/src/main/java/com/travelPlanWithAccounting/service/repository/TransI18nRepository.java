package com.travelPlanWithAccounting.service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travelPlanWithAccounting.service.entity.TransI18n;

@Repository
public interface TransI18nRepository extends JpaRepository<TransI18n, UUID> {
    void deleteByStartDetailIdOrEndDetailId(UUID startDetailId, UUID endDetailId);

    List<TransI18n> findByStartDetailIdInOrEndDetailIdIn(List<UUID> startDetailIds, List<UUID> endDetailIds);
}
