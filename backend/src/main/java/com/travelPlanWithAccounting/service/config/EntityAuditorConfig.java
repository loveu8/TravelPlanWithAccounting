package com.travelPlanWithAccounting.service.config;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityAuditorConfig implements AuditorAware<String> {

  final HttpServletRequest request;

  @Override
  public Optional<String> getCurrentAuditor() {
    // TODO: implement logic to get the current auditor
    return Optional.of("system");
  }
}
