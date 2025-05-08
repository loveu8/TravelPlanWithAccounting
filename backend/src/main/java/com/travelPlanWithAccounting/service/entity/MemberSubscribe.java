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
@Table(name = "member_subscribe")
public class MemberSubscribe extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Schema(description = "開始日期")
    @Column(name = "start_dt", columnDefinition = "timestamp with time zone not null")
    private Instant startDt;

    @Schema(description = "結束日期")
    @Column(name = "end_dt", columnDefinition = "timestamp with time zone not null")
    private Instant endDt;

}
