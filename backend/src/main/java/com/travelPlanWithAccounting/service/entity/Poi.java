package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name="poi")
@Getter @Setter
public class Poi {
  @Id @GeneratedValue private UUID id;

  @Column(name="external_id", unique=true, length=128)
  private String externalId;

  @Column(name="poi_type", length=10, nullable=false)
  private String poiType;

  @Column(name = "rating")
  private BigDecimal rating;

  @Column(name="review_count")
  private Integer reviewCount;

  private String phone;
  private String website;

  @JdbcTypeCode(SqlTypes.JSON) // 可留可移，留著也可以
  @Column(name="opening_periods")
  private String openingPeriods;
  
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name="photo_urls")
  private String photoUrls;
  
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name="types")
  private String types;

  @Column(name = "lat")
  private BigDecimal lat;

  @Column(name = "lon")
  private BigDecimal lon;

  // 若使用 Hibernate Spatial，可改 Geometry 型別；此處以 Object + native SQL 處理
  @Column(name="geom", columnDefinition="geography(Point,4326)")
  private Object geom;

  @Column(name="order_index") private Short orderIndex;

  @Column(name="created_at", updatable=false, insertable=false)
  private OffsetDateTime createdAt;
  @Column(name="updated_at", insertable=false)
  private OffsetDateTime updatedAt;
}