package com.travelPlanWithAccounting.service.repository;

import com.travelPlanWithAccounting.service.entity.Location;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchAllLocationRepository extends JpaRepository<Location, UUID> {

  @Query(
      "SELECT l, lm.textContent AS countryName "
          + "FROM Location l "
          + "LEFT JOIN l.metadata lm ON lm.locationMetaType = '001' AND l.id = lm.location.id "
          + "WHERE l.level <= 2 "
          + "ORDER BY l.code")
  List<Object[]> findAllLocation();
}
