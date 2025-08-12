package com.travelPlanWithAccounting.service.security;

import com.travelPlanWithAccounting.service.controller.AuthContext;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AuthContextImpl implements AuthContext {
  private static final ThreadLocal<UUID> HOLDER = new ThreadLocal<>();

  @Override
  public UUID getCurrentMemberId() {
    return HOLDER.get();
  }

  @Override
  public void setCurrentMemberId(UUID id) {
    HOLDER.set(id);
  }

  @Override
  public void clear() {
    HOLDER.remove();
  }
}
