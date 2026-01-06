package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "auth_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "member")
public class AuthInfo {

  @Id
  @GeneratedValue
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(length = 6, nullable = false)
  private String code;

  @Column(length = 255, nullable = false)
  private String email;

  /** 對應 member_id 外鍵（可為 null；註冊前沒有 member） */
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "member_id") // 對應你資料表的 member_id 欄位
  private Member member;

  /** 若想要直接讀取 UUID 而不 join，可保留唯讀欄位（可選） */
  @Column(name = "member_id", insertable = false, updatable = false)
  private UUID memberId;

  @Column(length = 3, nullable = false)
  private String action;

  @Column(nullable = false)
  private Boolean validation;

  @Column(name = "attempt_count", nullable = false)
  private Integer attemptCount;

  @Column(name = "last_sent_at", nullable = false, columnDefinition = "timestamptz")
  private OffsetDateTime lastSentAt;

  @Column(name = "verified_at", columnDefinition = "timestamptz")
  private OffsetDateTime verifiedAt;

  @Version
  private Long version;

  @Column(name = "expire_at", nullable = false, columnDefinition = "timestamptz")
  private OffsetDateTime expireAt;

  @Column(name = "created_by", columnDefinition = "uuid")
  private UUID createdBy;

  @Column(
      name = "created_at",
      columnDefinition = "timestamptz",
      insertable = false,
      updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_by", columnDefinition = "uuid")
  private UUID updatedBy;

  @Column(
      name = "updated_at",
      columnDefinition = "timestamptz",
      insertable = false,
      updatable = false)
  private OffsetDateTime updatedAt;
}
