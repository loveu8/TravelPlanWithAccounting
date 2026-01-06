package com.travelPlanWithAccounting.service.security;

import com.travelPlanWithAccounting.service.model.OwnerTypeCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** RS256 JWT 工具（Header 帶 kid；簽/驗） */
@Component
@RequiredArgsConstructor
public class JwtUtil {

  @Value("${auth.jwt.issuer:example-auth}")
  private String issuer;

  @Value("${auth.jwt.audience:example-web}")
  private String audience;

  @Value("${auth.jwt.access-ttl-seconds:900}")
  private long accessTtlSeconds;

  @Value("${auth.jwt.kid:local-dev}")
  private String kid;

  /** Base64(PKCS8) 私鑰（或改為從 KMS/Vault 載入） */
  @Value("${auth.jwt.private-key-pkcs8-base64:}")
  private String privateKeyPkcs8Base64;

  /** Base64(X509) 公鑰 */
  @Value("${auth.jwt.public-key-x509-base64:}")
  private String publicKeyX509Base64;

  private RSAPrivateKey privateKey() {
    try {
      byte[] bytes = Decoders.BASE64.decode(privateKeyPkcs8Base64);
      var spec = new PKCS8EncodedKeySpec(bytes);
      PrivateKey pk = KeyFactory.getInstance("RSA").generatePrivate(spec);
      return (RSAPrivateKey) pk;
    } catch (Exception e) {
      throw new IllegalStateException("Load RSA private key failed", e);
    }
  }

  private PublicKey publicKey() {
    try {
      byte[] bytes = Decoders.BASE64.decode(publicKeyX509Base64);
      var spec = new X509EncodedKeySpec(bytes);
      return KeyFactory.getInstance("RSA").generatePublic(spec);
    } catch (Exception e) {
      throw new IllegalStateException("Load RSA public key failed", e);
    }
  }

  /** 簽發 RS256 Access Token；role 這裡用 OwnerTypeCode 的名字（MEM/GUEST 對應你的 PRD） */
  public String signAccessToken(UUID sub, OwnerTypeCode role, String jti) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(accessTtlSeconds);
    return Jwts.builder()
        .header().add("kid", kid).and()
        .issuer(issuer)                // 0.12 新 API（也可用 setIssuer，但建議用新版語法）
        .audience().add(audience).and()
        .subject(sub.toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(Map.of("role", role.name(), "jti", jti))
        .signWith(privateKey(), Jwts.SIG.RS256)
        .compact();
  }

  /** 驗簽 + 解析（RS256），回傳 Jws<Claims>；統一用這一支 */
  public Jws<Claims> verify(String jwt) throws JwtException {
    return Jwts.parser()
        .requireIssuer(issuer)
        .requireAudience(audience)
        .verifyWith(publicKey())
        .build()
        .parseSignedClaims(jwt);
  }

  public long accessTtlSeconds() {
    return accessTtlSeconds;
  }
}
