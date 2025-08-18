package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.MemberPoi;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPoiRepository extends JpaRepository<MemberPoi,UUID> {
  boolean existsByMemberIdAndPoi_Id(UUID memberId, UUID poiId);
  Optional<MemberPoi> findByMemberIdAndPoi_Id(UUID memberId, UUID poiId);

  @Modifying
  @Query(value="INSERT INTO member_poi (member_id, poi_id) VALUES (:mid, :pid) ON CONFLICT (member_id, poi_id) DO NOTHING", nativeQuery=true)
  int insertIgnore(@Param("mid") UUID memberId, @Param("pid") UUID poiId);

  @Query(
      value =
          """
          SELECT p.external_id AS place_id,
                 mp.poi_id AS poi_id,
                 i.name,
                 i.city_name AS city,
                 (p.photo_urls::json ->> 0) AS photo_url,
                 p.rating::float AS rating
          FROM member_poi mp
          JOIN poi p ON mp.poi_id = p.id
          JOIN poi_i18n i ON i.poi_id = p.id AND i.lang_type = :langType
          WHERE mp.member_id = :memberId AND p.poi_type = :poiType
          ORDER BY mp.saved_at DESC
          """,
      countQuery =
          """
          SELECT COUNT(*)
          FROM member_poi mp
          JOIN poi p ON mp.poi_id = p.id
          WHERE mp.member_id = :memberId AND p.poi_type = :poiType
          """,
      nativeQuery = true)
  Page<MemberPoiProjection> findMemberPoiList(
      @Param("memberId") UUID memberId,
      @Param("poiType") String poiType,
      @Param("langType") String langType,
      Pageable pageable);

  interface MemberPoiProjection {
    UUID getPoiId();
    String getPlaceId();

    String getName();

    String getCity();

    String getPhotoUrl();

    Double getRating();
  }
}