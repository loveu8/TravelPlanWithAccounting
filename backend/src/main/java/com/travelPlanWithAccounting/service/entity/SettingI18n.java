package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(
    name = "setting_i18n",
    uniqueConstraints =
        @UniqueConstraint(name = "uq_setting_i18n", columnNames = {"setting_id", "lang_type"}))
public class SettingI18n extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "setting_id")
  @ToString.Exclude
  private Setting setting;

  @Column(name = "lang_type", length = 5)
  private String langType;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "code_desc", columnDefinition = "TEXT")
  private String codeDesc;
}

