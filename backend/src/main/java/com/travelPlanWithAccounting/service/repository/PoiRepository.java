package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.Poi;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PoiRepository extends JpaRepository<Poi, UUID> {

  Optional<Poi> findByExternalId(String externalId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from Poi p where p.externalId=:ext")
  Optional<Poi> lockByExternalId(@Param("ext") String externalId);

  /** 查 24h 內且含指定語系 i18n，一次帶出 */
  /** 回傳 24h 內的 infos_raw (JSON)；若無資料回 empty。 */
  @Query(
      value =
          """
          SELECT i.infos_raw
          FROM   poi       p
          JOIN   poi_i18n  i  ON i.poi_id = p.id
          WHERE  p.external_id = :placeId
            AND  i.lang_type   = :langType
            AND  p.updated_at >= (CURRENT_TIMESTAMP - INTERVAL '24 hours')
            AND  i.updated_at >= (CURRENT_TIMESTAMP - INTERVAL '24 hours')
          """,
      nativeQuery = true)
  Optional<String> findCachedRawJson(
      @Param("placeId") String placeId, @Param("langType") String langType);
}