package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.Location;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchLocationByCodeRepository extends JpaRepository<Location, UUID> {

  /**
   * 根據 code 查詢單一 Location
   *
   * @param code Location 代碼
   * @return Optional<Location>
   */
  Optional<Location> findByCode(String code);

  /**
   * 根據 code 和 level 查詢 Location
   *
   * @param code Location 代碼
   * @param level 層級 (1=國家 2=州省 3=城市)
   * @return Optional<Location>
   */
  Optional<Location> findByCodeAndLevel(String code, Short level);

  /**
   * 根據 parent_id 查詢子 Location
   *
   * @param parentId 父 Location ID
   * @return List<Location>
   */
  List<Location> findByParentIdOrderByOrderIndex(UUID parentId);

  /**
   * 根據 level 查詢所有該層級的 Location
   *
   * @param level 層級 (1=國家 2=州省 3=城市)
   * @return List<Location>
   */
  List<Location> findByLevelOrderByOrderIndex(Short level);

  /**
   * 查詢所有國家 (level = 1 且 parent_id 為 null)
   *
   * @return List<Location>
   */
  @Query("SELECT l FROM Location l WHERE l.level = 1 AND l.parent IS NULL ORDER BY l.orderIndex")
  List<Location> findAllCountries();

  /**
   * 根據國家代碼查詢該國家的所有州省 (level = 2)
   *
   * @param countryCode 國家代碼
   * @return List<Location>
   */
  @Query(
      "SELECT l FROM Location l WHERE l.level = 2 AND l.parent.code = :countryCode ORDER BY"
          + " l.orderIndex")
  List<Location> findProvincesByCountryCode(@Param("countryCode") String countryCode);

  /**
   * 根據州省代碼查詢該州省的所有城市 (level = 3)
   *
   * @param provinceCode 州省代碼
   * @return List<Location>
   */
  @Query(
      "SELECT l FROM Location l WHERE l.level = 3 AND l.parent.code = :provinceCode ORDER BY"
          + " l.orderIndex")
  List<Location> findCitiesByProvinceCode(@Param("provinceCode") String provinceCode);

  /**
   * 根據經緯度範圍查詢附近的 Location
   *
   * @param minLat 最小緯度
   * @param maxLat 最大緯度
   * @param minLon 最小經度
   * @param maxLon 最大經度
   * @return List<Location>
   */
  @Query(
      "SELECT l FROM Location l WHERE l.lat BETWEEN :minLat AND :maxLat AND l.lon BETWEEN :minLon"
          + " AND :maxLon")
  List<Location> findLocationsByCoordinates(
      @Param("minLat") Double minLat,
      @Param("maxLat") Double maxLat,
      @Param("minLon") Double minLon,
      @Param("maxLon") Double maxLon);

  /**
   * 根據 ISO 類型查詢 Location
   *
   * @param isoType ISO 類型 (001: ISO-3166-1, 002: ISO-3166-2)
   * @return List<Location>
   */
  List<Location> findByIsoTypeOrderByOrderIndex(String isoType);
}
