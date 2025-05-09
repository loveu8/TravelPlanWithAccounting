package com.travelPlanWithAccounting.service.entity;

import java.time.LocalTime;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

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
@Table(name = "travel_detail")
public class TravelDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_main_id", nullable = false)
    private TravelMain travelMain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_date_id", nullable = false)
    private TravelDate travelDate;

    @Column(name = "type", length = 3)
    private String type;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "google_map_info", columnDefinition = "jsonb")
    private JsonNode googleMapInfo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
