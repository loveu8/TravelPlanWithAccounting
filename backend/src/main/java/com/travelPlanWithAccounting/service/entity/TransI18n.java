package com.travelPlanWithAccounting.service.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 多語系行程區段（起訖明細間）的翻譯／轉乘等資訊。<br>
 * I18n record for a segment between start/end detail, e.g. translation or transit info.
 */
@Entity
@Valid
@Data
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trans_i18n")
public class TransI18n implements Serializable {

  private static final long serialVersionUID = 1L;

  /** 預設轉乘／類型代碼（空字串表示未指定） */
  private static final String DEFAULT_TRANS_TYPE = "";

  // --- 主鍵 ---
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
  private UUID id;

  // --- 業務欄位 ---
  @Column(name = "lang_type", nullable = false)
  private String langType;

  @Column(name = "start_detail_id", nullable = false)
  private UUID startDetailId;

  @Column(name = "end_detail_id", nullable = false)
  private UUID endDetailId;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "infos_raw", columnDefinition = "JSONB")
  private String infosRaw;

  @Column(name = "trans_type", length = 50, nullable = false)
  @Builder.Default
  private String transType = DEFAULT_TRANS_TYPE;

  @Column(name = "trans_time", nullable = false)
  private LocalTime transTime;

  @Column(name = "summary", columnDefinition = "TEXT")
  private String summary;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  // --- 審計欄位 ---
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
