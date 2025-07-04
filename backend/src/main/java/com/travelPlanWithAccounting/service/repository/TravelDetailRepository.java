package com.travelPlanWithAccounting.service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.travelPlanWithAccounting.service.entity.TravelDetail;

@Repository
public interface TravelDetailRepository extends JpaRepository<TravelDetail, UUID> {
    // 新增此方法，用於根據 travelDateId 刪除所有相關的 TravelDetail
    @Modifying // 表示這是一個修改操作（INSERT, UPDATE, DELETE）
    @Query("DELETE FROM TravelDetail td WHERE td.travelDateId = :travelDateId")
    void deleteByTravelDateId(@Param("travelDateId") UUID travelDateId);

    List<TravelDetail> findByTravelDateId(UUID travelDateId);
}
