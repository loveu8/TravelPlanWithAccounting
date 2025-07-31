package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.auth.AuthResponse.TokenNode;
import com.travelPlanWithAccounting.service.entity.RefreshToken;
import com.travelPlanWithAccounting.service.model.OwnerTypeCode;
import com.travelPlanWithAccounting.service.repository.RefreshTokenRepository;
import com.travelPlanWithAccounting.service.security.JwtUtil;
import com.travelPlanWithAccounting.service.util.TokenUtil;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 會員的 RT 發放 / 輪轉 / 撤銷 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;

  @Value("${auth.refresh.mem-ttl-seconds:1209600}") // 14d
  private long memRtTtlSeconds;

  @Value("${auth.refresh.max-per-client:5}")
  private int maxPerClient;

  /**
   * 發放：回「單層」 AuthResponse（access_token / refresh_token）
   */
  @Transactional
  public AuthResponse issueForMember(UUID memberId, String clientId, String ip, String ua) {

    // 1) 產生 RT 明文 + 雜湊（只存雜湊）
    String rtPlain = TokenUtil.randomRefreshToken();
    String rtHash = TokenUtil.sha256Base64Url(rtPlain);

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime exp = now.plusSeconds(memRtTtlSeconds);

    // TODO（可選）：控制單 clientId 上限 maxPerClient，超過則撤銷最舊一顆

    // 2) 儲存 RT row
    RefreshToken row =
        RefreshToken.builder()
            .tokenHash(rtHash)
            .ownerId(memberId)
            .ownerType(OwnerTypeCode.MEM.code()) // "001"
            .clientId(clientId)
            .issuedAt(now)
            .expiresAt(exp)
            .used(false)
            .revoked(false)
            .ip(ip)
            .ua(ua)
            .build();
    refreshTokenRepository.save(row);

    // 3) 簽 Access Token（RS256）
    String jti = UUID.randomUUID().toString();
    String at = jwtUtil.signAccessToken(memberId, OwnerTypeCode.MEM, jti);

    // 4) 組回傳（單層 data -> 由全域回包器再包外層 data）
    Map<String, TokenNode> cookies = new HashMap<>();
    cookies.put("access_token", TokenNode.builder()
        .code(at)
        .time(jwtUtil.accessTtlSeconds())
        .build());
    cookies.put("refresh_token", TokenNode.builder()
        .code(rtPlain)
        .time(memRtTtlSeconds)
        .build());

    return AuthResponse.builder()
        .id(memberId)
        .role(OwnerTypeCode.MEM.code()) // "001"
        .cookies(cookies)
        .build();
  }

  /**
   * 輪轉：驗舊 RT → 發新 AT/RT；若偵測 reuse，依需求可撤銷該 clientId 全部 RT（簡化版：直接丟 401/IllegalStateException）
   */
  @Transactional
  public AuthResponse rotateForMember(String oldRtPlain, String clientId, String ip, String ua) {
    String oldHash = TokenUtil.sha256Base64Url(oldRtPlain);
    RefreshToken db =
        refreshTokenRepository
            .findByTokenHash(oldHash)
            .orElseThrow(() -> new IllegalArgumentException("RT not found"));

    // 驗狀態
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    if (db.isRevoked() || db.isUsed() || db.getExpiresAt().isBefore(now)) {
      throw new IllegalStateException("RT invalid (revoked/used/expired)");
    }
    if (!db.getClientId().equals(clientId)) {
      throw new IllegalStateException("Client mismatch");
    }

    // 標記舊 RT 已使用（Rotation）
    db.setUsed(true);
    refreshTokenRepository.save(db);

    // 發新一組（邏輯與 issueForMember 相同）
    return issueForMember(db.getOwnerId(), clientId, ip, ua);
  }

  /** 撤銷（登出）：撤銷該 member 在 clientId 下的所有 RT */
  @Transactional
  public void revokeAllForMember(UUID memberId, String clientId) {
    var list =
        refreshTokenRepository.findAllByOwnerIdAndClientIdAndRevokedFalse(memberId, clientId);
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    for (var rt : list) {
      rt.setRevoked(true);
      rt.setRevokedAt(now);
    }
    refreshTokenRepository.saveAll(list);
  }

  /** 登出（建議）：用 refreshToken 找到 ownerId + clientId，撤銷該平台的所有 RT。 */
  @Transactional
  public void logoutByRefreshToken(String rtPlain) {
    String hash = TokenUtil.sha256Base64Url(rtPlain);
    var db = refreshTokenRepository.findByTokenHash(hash).orElse(null);

    // 不回應是否存在，避免資訊洩漏：若查無則直接 return 視為 idempotent 成功
    if (db == null) return;

    var list =
        refreshTokenRepository.findAllByOwnerIdAndClientIdAndRevokedFalse(
            db.getOwnerId(), db.getClientId());

    var now = OffsetDateTime.now(ZoneOffset.UTC);
    for (var rt : list) {
      rt.setRevoked(true);
      rt.setRevokedAt(now);
    }
    refreshTokenRepository.saveAll(list);
  }

  /** 登出（只撤銷當前這一顆 RT）：若你想精準撤銷單顆，可改呼叫這個。 */
  @Transactional
  public void logoutOnlyThisRefreshToken(String rtPlain) {
    String hash = TokenUtil.sha256Base64Url(rtPlain);
    var db = refreshTokenRepository.findByTokenHash(hash).orElse(null);
    if (db == null) return; // 同上，做成冪等
    if (!db.isRevoked()) {
      db.setRevoked(true);
      db.setRevokedAt(OffsetDateTime.now(ZoneOffset.UTC));
      refreshTokenRepository.save(db);
    }
  }
}
