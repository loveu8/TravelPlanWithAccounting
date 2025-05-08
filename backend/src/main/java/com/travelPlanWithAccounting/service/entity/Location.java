package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.geo.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(
    name = "location",
    uniqueConstraints = @UniqueConstraint(name = "uq_location_code", columnNames = "code"))
public class Location {

  /* ---------- 基本欄位 ---------- */
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @EqualsAndHashCode.Include
  @Column(updatable = false, nullable = false)
  private UUID id;

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
  @Column(name = "lat", precision = 10, scale = 6)
  private BigDecimal lat;

  @Column(name = "lon", precision = 10, scale = 6)
  private BigDecimal lon;

  // 若使用 PostGIS，可保留下列欄位
  @Column(columnDefinition = "GEOGRAPHY(Point,4326)")
  private Point geom;

  /* ---------- Audit ---------- */
  private String createdBy;
  @CreationTimestamp private Date createdAt;
  private String updatedBy;
  @UpdateTimestamp private Date updatedAt;

  /* ---------- 關聯集合 ---------- */
  @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LocationMetadata> metadata = new ArrayList<>();

  @OneToMany(mappedBy = "location", orphanRemoval = true)
  private List<LocationGroupMapping> groupMappings = new ArrayList<>();
}
