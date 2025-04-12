package com.travelPlanWithAccounting.service.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

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
