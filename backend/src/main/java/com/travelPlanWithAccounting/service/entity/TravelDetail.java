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

@Entity
@Valid
@Data
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "travel_detail")
public class TravelDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // 使用 GenerationType.UUID 讓JPA自動生成UUID
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    @Column(name = "travel_main_id", nullable = false)
    private UUID travelMainId; // 不建立雙向關聯

    @Column(name = "travel_date_id", nullable = false)
    private UUID travelDateId; // 不建立雙向關聯

    @Column(name = "type", nullable = false, length = 3)
    private String type;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "google_map_info", columnDefinition = "JSONB")
    // 將 JSON 儲存為 String，參考 Places API, Maps Geocoding API 等
    private String googleMapInfo;

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
