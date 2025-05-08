package com.travelPlanWithAccounting.service.entity;

import java.time.Instant;

import org.hibernate.annotations.DynamicUpdate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Valid
@Data
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "auth_info")
public class AuthInfo extends BaseEntity {

    @Column(name = "code", length = 6)
    private String code;

    @Column(name = "email", length = 255)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "action", length = 3)
    private String action;

    @Column(name = "validation")
    private Boolean validation;

    @Schema(description = "到期時間")
    @Column(name = "expire_at", columnDefinition = "timestamp with time zone not null")
    private Instant expireAt;
}
