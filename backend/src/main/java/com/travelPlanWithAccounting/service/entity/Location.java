package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.geo.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "location",
    uniqueConstraints =
        @UniqueConstraint(
            name = "un_location",
            columnNames = {"parent_id", "code", "level"}))
public class Location extends BaseEntity {

  @NaturalId
  @Column(nullable = false, length = 32)
  private String code;

  /* ---------- 階層關聯 ---------- */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Location parent;

  @OneToMany(mappedBy = "parent", orphanRemoval = true)
  private List<Location> children = new ArrayList<>();

  @Column(name = "level", nullable = false)
  private Short level;

  @Column(name = "order_index")
  private Short orderIndex;

  @Column(name = "iso_type", length = 3, nullable = false)
  private String isoType;

  /* ---------- 地理資訊 ---------- */
  @Column(name = "lat", precision = 10, scale = 7)
  private BigDecimal lat;

  @Column(name = "lon", precision = 10, scale = 7)
  private BigDecimal lon;

  // 若使用 PostGIS，可保留下列欄位
  @Column(columnDefinition = "GEOGRAPHY(Point,4326)")
  private Point geom;

  /* ---------- 關聯集合 ---------- */
  @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LocationMetadata> metadata = new ArrayList<>();

  @OneToMany(mappedBy = "location", orphanRemoval = true)
  private List<LocationGroupMapping> groupMappings = new ArrayList<>();
}
