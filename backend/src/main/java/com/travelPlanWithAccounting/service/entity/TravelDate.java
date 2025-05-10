package com.travelPlanWithAccounting.service.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = "travel_date")
public class TravelDate extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_main_id", nullable = false)
    private TravelMain travelMain;

    @Column(name = "travel_date")
    private LocalDate travelDate;

    @OneToMany(mappedBy = "travelDate", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<TravelDetail> travelDetails;

}
