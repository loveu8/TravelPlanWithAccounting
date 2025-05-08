package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
    name = "location_metadata",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uq_location_meta",
            columnNames = {"location_id", "lang_type", "location_meta_type"}))
public class LocationMetadata {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(updatable = false, nullable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "location_id", nullable = false)
  private Location location;

  @Column(name = "lang_type", columnDefinition = "bpchar(5)", nullable = false)
  private String langType; // zh-TW / en-US …

  @Column(name = "location_meta_type", length = 3, nullable = false)
  private String locationMetaType; // 001 / 002 …

  @Column(name = "text_content", length = 256, nullable = false)
  private String textContent;

  private String description;

  /* Audit */
  private String createdBy;
  @CreationTimestamp private Date createdAt;
  private String updatedBy;
  @UpdateTimestamp private Date updatedAt;
}
