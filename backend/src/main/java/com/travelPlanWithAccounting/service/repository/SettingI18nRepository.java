package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.SettingI18n;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingI18nRepository extends JpaRepository<SettingI18n, UUID> {}

