package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity @Table(name="poi_i18n", uniqueConstraints = @UniqueConstraint(name="uq_poi_i18n", columnNames={"poi_id","lang_type"}))
@Getter @Setter
public class PoiI18n {
  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="poi_id")
  private Poi poi;

  @Column(name="lang_type", length=5, nullable=false)
  private String langType; // 001,002

  @Column(length=128, nullable=false)
  private String name;

  @Column(columnDefinition="text")
  private String description;

  @Column(length=256)
  private String address;

  @Column(name="city_name", length=128)
  private String cityName;

  @Column(name="country_name", length=128)
  private String countryName;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name="weekday_descriptions")
  private String weekdayDescriptions;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name="infos_raw")
  private String infosRaw;

  @Column(name="created_at", updatable=false, insertable=false)
  private OffsetDateTime createdAt;
  @Column(name="updated_at", insertable=false)
  private OffsetDateTime updatedAt;
}