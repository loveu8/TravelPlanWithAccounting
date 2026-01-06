package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "refresh_token")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {

  @Id
  @Column(name = "token_hash", length = 128, nullable = false)
  private String tokenHash;

  @Column(name = "owner_id", nullable = false, columnDefinition = "uuid")
  private UUID ownerId;

  @Column(name = "owner_type", length = 3, nullable = false)
  private String ownerType; // "001"=MEM, "002"=GUEST

  @Column(name = "client_id", length = 64, nullable = false)
  private String clientId;

  @Column(name = "issued_at", nullable = false, columnDefinition = "timestamptz")
  private OffsetDateTime issuedAt;

  @Column(name = "expires_at", nullable = false, columnDefinition = "timestamptz")
  private OffsetDateTime expiresAt;

  @Column(name = "used", nullable = false)
  private boolean used;

  @Column(name = "revoked", nullable = false)
  private boolean revoked;

  @Column(name = "revoked_at", columnDefinition = "timestamptz")
  private OffsetDateTime revokedAt;

  @Column(name = "ua", length = 255)
  private String ua;

  @Column(name = "ip", length = 64)
  private String ip;

  /* 審計欄位 */
  @Column(name = "created_by")
  private UUID createdBy;

  @Column(name = "created_at", columnDefinition = "timestamptz")
  private OffsetDateTime createdAt;

  @Column(name = "updated_by")
  private UUID updatedBy;

  @Column(name = "updated_at", columnDefinition = "timestamptz")
  private OffsetDateTime updatedAt;

  @PrePersist
  void prePersist() {
    if (createdAt == null) createdAt = OffsetDateTime.now();
    if (updatedAt == null) updatedAt = createdAt;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
