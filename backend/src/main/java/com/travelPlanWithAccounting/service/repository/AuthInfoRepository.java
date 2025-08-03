package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.AuthInfo;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AuthInfoRepository extends JpaRepository<AuthInfo, UUID> {

  Optional<AuthInfo> findFirstByEmailAndCodeAndActionAndExpireAtAfterOrderByCreatedAtDesc(
      String email, String code, String action, OffsetDateTime now);

  Optional<AuthInfo> findByIdAndCodeAndActionAndExpireAtAfter(
      UUID id, String code, String action, OffsetDateTime now);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("update AuthInfo a set a.validation = true where a.id = :id")
  int markValidated(@Param("id") UUID id);
}
