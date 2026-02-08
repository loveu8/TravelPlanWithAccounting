package com.travelPlanWithAccounting.service.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Valid
@Data
@Builder
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "setting")
public class Setting extends BaseEntity {

  @Column(name = "category", length = 64, nullable = false)
  private String category;

  @Column(name = "name", length = 255, nullable = false)
  private String name;

  @Column(name = "code_name", length = 255, nullable = false)
  private String codeName;

    @Column(name = "code_desc", columnDefinition = "TEXT")
    private String codeDesc;

    @OneToMany(mappedBy = "setting", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SettingLog> settingLogs;

    @OneToMany(mappedBy = "setting", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SettingI18n> settingI18ns;
}
