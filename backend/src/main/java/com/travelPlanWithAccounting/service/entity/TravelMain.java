package com.travelPlanWithAccounting.service.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Valid
@Data
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "travel_main")
public class TravelMain extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "is_private")
    private Boolean isPrivate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "visit_place", columnDefinition = "jsonb")
    private JsonNode visitPlace;

    @OneToMany(mappedBy = "travelMain", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TravelPermissions> travelPermissions;

    @OneToMany(mappedBy = "travelMain", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TravelDate> travelDates;

    @OneToMany(mappedBy = "travelMain", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TravelDetail> travelDetails;

}
