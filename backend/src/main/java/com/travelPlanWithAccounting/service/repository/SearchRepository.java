package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.LocationGroup;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends JpaRepository<LocationGroup, UUID> {

  @Query(
      "SELECT g, m.textContent AS regionName, lgm, loc, lm.textContent AS cityName FROM"
          + " LocationGroup g LEFT JOIN g.metadata m ON m.langType = :langType AND"
          + " m.locationMetaType = '001' JOIN g.mappings lgm JOIN lgm.location loc LEFT JOIN"
          + " loc.metadata lm ON lm.langType = :langType AND lm.locationMetaType = '001' WHERE"
          + " g.parent.id = (SELECT l.id FROM Location l WHERE l.code = :countryCode AND l.parent"
          + " IS NULL) AND g.code <> :countryCode"
          + " ORDER BY g.orderIndex, lgm.seqno")
  List<Object[]> findRegionsAndCities(
      @Param("countryCode") String countryCode, @Param("langType") String langType);
}
