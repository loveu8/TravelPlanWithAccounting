package com.travelPlanWithAccounting.service.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityAuditorConfig implements AuditorAware<UUID> {

  final HttpServletRequest request;

  final UUID systemId = UUID.fromString("00000000-0000-0000-0000-000000000000");

  @Override
  public Optional<UUID> getCurrentAuditor() {
    // TODO: implement logic to get the current auditor
    return Optional.of(systemId);
  }
}
