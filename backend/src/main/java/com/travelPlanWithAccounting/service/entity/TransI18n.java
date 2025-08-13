package com.travelPlanWithAccounting.service.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class TransI18n implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // 使用 GenerationType.UUID 讓JPA自動生成UUID
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "lang_type", nullable = false)
    private String langType;

    @Column(name = "start_detail_id", nullable = false)
    private UUID startDetailId; // 不建立雙向關聯

    @Column(name = "end_detail_id", nullable = false)
    private UUID endDetailId; // 不建立雙向關聯

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "infos_raw", columnDefinition = "JSONB")
    // 將 JSON 儲存為 String，您可以使用自定義類型以實現更結構化的訪問
    private String infosRaw;

    @Column(name = "trans_type", length = 50, nullable = false)
    private String transType = "";

    @Column(name = "trans_time", nullable = false)
    private LocalTime transTime;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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
