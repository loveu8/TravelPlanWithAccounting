package com.travelPlanWithAccounting.service.controller;

import java.util.UUID;

/**
 * 從安全框架 (JWT / session) 取得登入會員。
 * 實作例：AuthContextImpl 於 SecurityConfig 透過 SecurityContextHolder 解析。
 */
public interface AuthContext {
  UUID getCurrentMemberId();
}