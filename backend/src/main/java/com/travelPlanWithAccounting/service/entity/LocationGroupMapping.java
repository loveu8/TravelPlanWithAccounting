package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "location_group_mappings")
public class LocationGroupMapping {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "location_id", nullable = false)
  private UUID locationId;

  @Column(name = "location_group_id", nullable = false)
  private UUID locationGroupId;

  @MapsId("locationId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @MapsId("locationGroupId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "location_group_id", nullable = false)
  private LocationGroup locationGroup;

  private Short seqno;

  /* Audit */
  private UUID createdBy;
  @CreationTimestamp private Date createdAt;
  private UUID updatedBy;
  @UpdateTimestamp private Date updatedAt;
}
