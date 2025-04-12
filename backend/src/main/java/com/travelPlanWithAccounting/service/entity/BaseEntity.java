package com.travelPlanWithAccounting.service.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.travelPlanWithAccounting.service.uitl.UuidGeneratorUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Valid
@SuperBuilder
@NoArgsConstructor
@MappedSuperclass
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @JdbcTypeCode(SqlTypes.UUID)
    @Schema(description = "系統編號", example = "a2d19d0f-3a39-4768-97a7-165bfce4a9a6")
    @Column(name = "id", updatable = false)
    private UUID id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreatedBy
    @Schema(description = "輸入者", example = "a01")
    @Size(min = 1, max = 10)
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @CreatedDate
    @Schema(description = "輸入日期")
    @Column(name = "created_at", updatable = false, columnDefinition = "timestamp with time zone not null")
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @LastModifiedBy
    @Schema(description = "修改者", example = "a01")
    @Size(min = 1, max = 10)
    @Column(name = "updated_by")
    private String updatedBy;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @LastModifiedDate
    @Schema(description = "修改日期")
    @Column(name = "updated_at", columnDefinition = "timestamp with time zone not null")
    private Instant updatedAt;

    @PrePersist
    public void onPrePersist() {
        if (Objects.isNull(this.id)) {
            this.id = UuidGeneratorUtils.generateUuid();
        }
    }
}