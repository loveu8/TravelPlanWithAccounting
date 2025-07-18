package com.travelPlanWithAccounting.service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Poi / PoiI18n 同步結果（不含 member_poi）。
 */
@AllArgsConstructor
@Getter
@Setter
public class TxPoiResult {

    private final UUID  poiId;         // 主鍵
    private final boolean poiCreated;  // 此次是否新建 Poi
    private final boolean langInserted;// 此語系 i18n 是否第一次插入
}

