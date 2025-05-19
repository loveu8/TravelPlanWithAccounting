package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "location_groups",
    uniqueConstraints =
        @UniqueConstraint(
            name = "un_location_groups",
            columnNames = {"parent_id", "code"}))
public class LocationGroup extends BaseEntity {

  @Column(nullable = false, length = 32)
  private String code;

  /** 若分組還需要分層級 → 指向父 Group；否則改為 Location 如 TW */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private LocationGroup parent;

  @OneToMany(mappedBy = "parent", orphanRemoval = true)
  private List<LocationGroup> children = new ArrayList<>();

  @Column(name = "order_index")
  private Short orderIndex;

  /* 關聯 */
  @OneToMany(mappedBy = "locationGroup", orphanRemoval = true)
  private List<LocationGroupMapping> mappings = new ArrayList<>();

  @OneToMany(mappedBy = "locationGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LocationGroupMetadata> metadata = new ArrayList<>();
}
