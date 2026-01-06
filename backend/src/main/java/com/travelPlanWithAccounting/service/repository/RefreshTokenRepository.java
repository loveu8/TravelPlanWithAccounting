package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.RefreshToken;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

  Optional<RefreshToken> findByTokenHash(String tokenHash);

  long countByOwnerIdAndClientIdAndRevokedFalse(UUID ownerId, String clientId);

  List<RefreshToken> findAllByOwnerIdAndClientIdAndRevokedFalse(UUID ownerId, String clientId);

  long deleteByExpiresAtBefore(OffsetDateTime threshold);
}
