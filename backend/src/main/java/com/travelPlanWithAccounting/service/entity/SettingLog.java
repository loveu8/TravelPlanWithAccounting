package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Valid
@Data
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "setting_log")
public class SettingLog extends BaseEntity {

  @Column(name = "seqno", nullable = false)
  private Long seqno;

  @Column(name = "category", length = 64)
  private String category;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "code_name", length = 255)
  private String codeName;

  @Column(name = "code_desc", columnDefinition = "TEXT")
  private String codeDesc;

  @Column(name = "func", length = 1, nullable = false)
  private String func;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setting_id", nullable = false)
    private Setting setting;
}
