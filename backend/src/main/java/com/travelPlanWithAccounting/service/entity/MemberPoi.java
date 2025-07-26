package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.*;

@Entity @Table(name="member_poi", uniqueConstraints = @UniqueConstraint(name="uq_member_poi", columnNames={"member_id","poi_id"}))
@Getter @Setter
public class MemberPoi {
  @Id @GeneratedValue private UUID id;

  @Column(name="member_id", nullable=false)
  private UUID memberId;

  @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="poi_id", nullable=false)
  private Poi poi;

  @Column(name="saved_at", insertable=false, updatable=false)
  private OffsetDateTime savedAt;

  @Column(name="created_at", updatable=false, insertable=false)
  private OffsetDateTime createdAt;
  @Column(name="updated_at", insertable=false)
  private OffsetDateTime updatedAt;
}