package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Valid
@Data
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "travel_permissions")
public class TravelPermissions implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID) // 使用 GenerationType.UUID 讓JPA自動生成UUID
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "travel_main_id", nullable = false)
  private UUID travelMainId; // 不建立雙向關聯

  @Column(name = "member_id", nullable = false)
  private UUID memberId; // 不建立雙向關聯

  @Column(name = "email")
  private String email;

  @Column(name = "type", length = 3)
  private String type;

  @Column(name = "permissions")
  private Boolean permissions;

  @Column(name = "created_by")
  private UUID createdBy;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_by")
  private UUID updatedBy;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;
}
