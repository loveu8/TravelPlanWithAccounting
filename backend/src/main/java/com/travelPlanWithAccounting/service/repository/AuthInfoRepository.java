package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.AuthInfo;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthInfoRepository extends JpaRepository<AuthInfo, UUID> {

  Optional<AuthInfo> findByIdAndActionAndValidationFalseAndExpireAtAfter(
      UUID id, String action, OffsetDateTime now);

  Optional<AuthInfo> findByIdAndActionAndValidationTrueAndExpireAtAfter(
      UUID id, String action, OffsetDateTime now);

  Optional<AuthInfo> findByIdAndActionAndValidationTrue(UUID id, String action);
}
