package com.travelPlanWithAccounting.service.service;

import com.travelPlanWithAccounting.service.dto.auth.AccessTokenResponse;
import com.travelPlanWithAccounting.service.dto.auth.AuthResponse;
import com.travelPlanWithAccounting.service.dto.auth.AuthResponse.TokenNode;
import com.travelPlanWithAccounting.service.dto.auth.VerifyTokenRequest;
import com.travelPlanWithAccounting.service.dto.auth.VerifyTokenResponse;
import com.travelPlanWithAccounting.service.entity.RefreshToken;
import com.travelPlanWithAccounting.service.exception.RefreshTokenException;
import com.travelPlanWithAccounting.service.model.OwnerTypeCode;
import com.travelPlanWithAccounting.service.repository.RefreshTokenRepository;
import com.travelPlanWithAccounting.service.security.JwtUtil;
import com.travelPlanWithAccounting.service.util.TokenUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 會員 Refresh-Token 發放 / 旋轉 / 撤銷（目前「不」檢查 clientId） */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtil jwtUtil;

  @Value("${auth.refresh.mem-ttl-seconds:1209600}") // 14 d
  private long memRtTtlSeconds;

  // ---------- 發放 ----------

  /** 發放一組 AT / RT，不驗 clientId，但仍會寫入資料庫方便日後擴充。 */
  @Transactional
  public AuthResponse issueForMember(
      UUID memberId,
      String clientId, // 仍保留欄位（預設 "web"）
      String ip,
      String ua) {

    /* 1) 產生 RT 明文 + 雜湊（只存雜湊） */
    String rtPlain = TokenUtil.randomRefreshToken();
    String rtHash = TokenUtil.sha256Base64Url(rtPlain);

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime exp = now.plusSeconds(memRtTtlSeconds);

    /* 2) 寫入 refresh_token table（clientId 先存起來，暫不使用） */
    refreshTokenRepository.save(
        RefreshToken.builder()
            .tokenHash(rtHash)
            .ownerId(memberId)
            .ownerType(OwnerTypeCode.MEMBER.code()) // "001"
            .clientId(clientId)
            .issuedAt(now)
            .expiresAt(exp)
            .used(false)
            .revoked(false)
            .ip(ip)
            .ua(ua)
            .build());

    /* 3) Access-Token */
    String jti = UUID.randomUUID().toString();
    String at = jwtUtil.signAccessToken(memberId, OwnerTypeCode.MEMBER, jti);

    /* 4) 回傳單層 AuthResponse */
    Map<String, TokenNode> cookies = new HashMap<>();
    cookies.put("access_token", new TokenNode(at, jwtUtil.accessTtlSeconds()));
    cookies.put("refresh_token", new TokenNode(rtPlain, memRtTtlSeconds));

    return new AuthResponse(memberId, OwnerTypeCode.MEMBER.code(), cookies);
  }

  // ---------- 旋轉 ----------

  /**
   * Rotate：只靠 refreshToken 定位資料列，不再比對 clientId。<br>
   * *流程*：<br>
   * ① 找到資料列 → ② 檢查 revoked / used / expired → ③ 將舊 RT 標記 used → ④ 發新 AT/RT
   */
  @Transactional
  public AuthResponse rotateForMember(
      String oldRtPlain,
      String clientIdIgnored, // 預留，不再驗證
      String ip,
      String ua) {

    String oldHash = TokenUtil.sha256Base64Url(oldRtPlain);
    RefreshToken db =
        refreshTokenRepository
            .findByTokenHash(oldHash)
            .orElseThrow(RefreshTokenException.NotFound::new);

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    if (db.isRevoked() || db.isUsed() || db.getExpiresAt().isBefore(now)) {
      throw new RefreshTokenException.Invalid();
    }

    /* 只標記 used，不檢查 clientId */
    db.setUsed(true);
    refreshTokenRepository.save(db);

    /* 用 DB 紀錄的 clientId 再發一組（之後若要重新啟用 clientId 驗證只需加回檢查） */
    return issueForMember(db.getOwnerId(), db.getClientId(), ip, ua);
  }

  // ---------- 撤銷 ----------

  /** 依 memberId + clientId 撤銷（保留原功能，暫不在前端使用） */
  @Transactional
  public void revokeAllForMember(UUID memberId, String clientId) {
    var list =
        refreshTokenRepository.findAllByOwnerIdAndClientIdAndRevokedFalse(memberId, clientId);
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    list.forEach(
        rt -> {
          rt.setRevoked(true);
          rt.setRevokedAt(now);
        });
    refreshTokenRepository.saveAll(list);
  }

  @Transactional
  public boolean logoutByRefreshToken(String rtPlain) {
    String hash = TokenUtil.sha256Base64Url(rtPlain);
    RefreshToken first = refreshTokenRepository.findByTokenHash(hash).orElse(null);

    // 查無資料直接回 false（你也可以選擇回 true -> 冪等）
    if (first == null) return false;

    UUID memberId = first.getOwnerId(); // ＝ updated_by 欄位值
    String clientId = first.getClientId(); // 保持相同平台

    var list =
        refreshTokenRepository.findAllByOwnerIdAndClientIdAndRevokedFalse(memberId, clientId);

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    list.forEach(
        rt -> {
          rt.setRevoked(true);
          rt.setRevokedAt(now);
          rt.setUpdatedBy(memberId); // ★ 這行：寫 updated_by
          rt.setUpdatedAt(now); // 若你想同步 updated_at
        });

    refreshTokenRepository.saveAll(list);
    return true;
  }

  /** 若只想撤銷這一顆 RT，可呼叫此法（現階段前端未用到） */
  @Transactional
  public void logoutOnlyThisRefreshToken(String rtPlain) {
    String hash = TokenUtil.sha256Base64Url(rtPlain);
    RefreshToken db = refreshTokenRepository.findByTokenHash(hash).orElse(null);
    if (db != null && !db.isRevoked()) {
      db.setRevoked(true);
      db.setRevokedAt(OffsetDateTime.now(ZoneOffset.UTC));
      refreshTokenRepository.save(db);
    }
  }

  @Transactional
  public AccessTokenResponse refreshAccessToken(String rtPlain, String ip, String ua) {

    String hash = TokenUtil.sha256Base64Url(rtPlain);
    RefreshToken db =
        refreshTokenRepository
            .findByTokenHash(hash)
            .orElseThrow(RefreshTokenException.NotFound::new);

    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
    if (db.isRevoked() || db.getExpiresAt().isBefore(now)) {
      throw new RefreshTokenException.Invalid();
    }

    /* 若想做 reuse-detection，可在此檢查 db.isUsed() → revoke ××× */
    // db.setLastUsedAt(now);            // 你可以加一個字段記錄
    db.setUpdatedAt(now);
    db.setUpdatedBy(db.getOwnerId()); // optional：誰刷新就寫誰
    refreshTokenRepository.save(db);

    /* 簽新的 Access-Token，Refresh-Token 繼續沿用 */
    String newAt =
        jwtUtil.signAccessToken(
            db.getOwnerId(), OwnerTypeCode.MEMBER, UUID.randomUUID().toString());

    return new AccessTokenResponse(newAt, jwtUtil.accessTtlSeconds());
  }

  // ---------- 驗證 ----------

  @Transactional
  public VerifyTokenResponse verifyToken(VerifyTokenRequest req) {
    String tokenTypeStr =
        req != null && req.getTokenType() != null ? String.valueOf(req.getTokenType()) : null;
    if (req == null
        || req.getTokenType() == null
        || req.getToken() == null
        || req.getToken().isBlank()) {
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType(tokenTypeStr)
          .reason("REQ_INVALID")
          .build();
    }
    return switch (req.getTokenType()) {
      case ACCESS -> verifyAccessToken(req.getToken());
      case REFRESH -> verifyRefreshToken(req.getToken(), req.getClientId());
      default ->
          VerifyTokenResponse.builder()
              .valid(false)
              .tokenType(String.valueOf(req.getTokenType()))
              .reason("UNSUPPORTED_TYPE")
              .build();
    };
  }

  private VerifyTokenResponse verifyAccessToken(String token) {
    try {
      Jws<Claims> jws = jwtUtil.verify(token);
      Claims c = jws.getPayload();
      return VerifyTokenResponse.builder()
          .valid(true)
          .tokenType("ACCESS")
          .sub(UUID.fromString(c.getSubject()))
          .role(c.get("role", String.class))
          .exp(c.getExpiration().toInstant().getEpochSecond())
          .build();
    } catch (Exception e) {
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType("ACCESS")
          .reason("JWT_INVALID: " + e.getClass().getSimpleName())
          .build();
    }
  }

  private VerifyTokenResponse verifyRefreshToken(String token, String clientId) {
    String hash = TokenUtil.sha256Base64Url(token);
    Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHash(hash);
    if (opt.isEmpty()) {
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType("REFRESH")
          .reason("NOT_FOUND")
          .build();
    }
    RefreshToken rt = opt.get();
    OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

    if (rt.isRevoked())
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType("REFRESH")
          .reason("REVOKED")
          .build();
    if (rt.isUsed())
      return VerifyTokenResponse.builder().valid(false).tokenType("REFRESH").reason("USED").build();
    if (rt.getExpiresAt().isBefore(now))
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType("REFRESH")
          .reason("EXPIRED")
          .build();
    if (clientId != null && !clientId.isBlank() && !rt.getClientId().equals(clientId))
      return VerifyTokenResponse.builder()
          .valid(false)
          .tokenType("REFRESH")
          .reason("CLIENT_MISMATCH")
          .build();
    return VerifyTokenResponse.builder()
        .valid(true)
        .tokenType("REFRESH")
        .sub(rt.getOwnerId())
        .build();
  }
}
