package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.Setting;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingRepository extends JpaRepository<Setting, UUID> {

  /**
   * 根據類別查詢設定
   *
   * @param category 設定類別
   * @return List<Setting>
   */
  @Query("SELECT s FROM Setting s WHERE s.category = :category ORDER BY s.createdAt")
  List<Setting> findByCategory(@Param("category") String category);

  /**
   * 查詢所有語言類型設定
   *
   * @return List<Setting>
   */
  @Query("SELECT s FROM Setting s WHERE s.category = 'LANG_TYPE' ORDER BY s.createdAt")
  List<Setting> findAllLanguageTypes();
}
