package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.Location;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchAllCountryRepository extends JpaRepository<Location, UUID> {

  @Query(
      "SELECT l, lm.textContent AS countryName "
          + "FROM Location l "
          + "LEFT JOIN l.metadata lm ON lm.langType = :langType AND lm.locationMetaType = '001' "
          + "WHERE l.level = 1 AND l.parent IS NULL "
          + "ORDER BY l.code")
  List<Object[]> findCountriesByLangType(@Param("langType") String langType);
}
