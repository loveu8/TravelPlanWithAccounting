package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.dto.setting.SettingResponse;
import com.travelPlanWithAccounting.service.entity.Setting;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, UUID> {

  /**
   * 根據類別與語系查詢設定
   *
   * @param category 設定類別
   * @param langType 語系內碼
   * @param defaultLang 預設語系內碼
   * @return List<SettingResponse>
   */
  @Query(
      "SELECT new com.travelPlanWithAccounting.service.dto.setting.SettingResponse(" +
      "s.category, s.codeName, s.codeDesc, " +
      "COALESCE(siLang.name, siDefault.name), " +
      "COALESCE(siLang.codeDesc, siDefault.codeDesc)) " +
      "FROM Setting s " +
      "LEFT JOIN SettingI18n siLang ON siLang.setting = s AND siLang.langType = :langType " +
      "LEFT JOIN SettingI18n siDefault ON siDefault.setting = s AND siDefault.langType = :defaultLang " +
      "WHERE s.category = :category ORDER BY s.createdAt")
  List<SettingResponse> findByCategoryWithLang(
      @Param("category") String category,
      @Param("langType") String langType,
      @Param("defaultLang") String defaultLang);

  @Query("SELECT s FROM Setting s WHERE s.category = :category ORDER BY s.createdAt")
  List<Setting> findByCategory(@Param("category") String category);

  Optional<Setting> findByCategoryAndName(String category, String name);

  Optional<Setting> findByCategoryAndCodeName(String category, String codeName);
}
