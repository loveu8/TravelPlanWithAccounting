package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.MemberPoi;
import java.util.Optional;import java.util.UUID;
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
}