package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "location_metadata",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uq_location_meta",
            columnNames = {"location_id", "lang_type", "location_meta_type"}))
public class LocationMetadata extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @Column(name = "lang_type", nullable = false)
  private String langType; // zh-TW / en-US …

  @Column(name = "location_meta_type", length = 3, nullable = false)
  private String locationMetaType; // 001 / 002 …

  @Column(name = "text_content", length = 256, nullable = false)
  private String textContent;

  private String description;
}
