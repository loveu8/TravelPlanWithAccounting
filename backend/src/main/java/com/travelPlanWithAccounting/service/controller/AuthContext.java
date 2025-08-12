package com.travelPlanWithAccounting.service.controller;

import java.util.UUID;

/**
 * 從安全框架 (JWT / session) 取得登入會員。
 * 由 AOP 在驗證 Access Token 後寫入 ThreadLocal。
 */
public interface AuthContext {
  UUID getCurrentMemberId();

  void setCurrentMemberId(UUID id);

  void clear();
}