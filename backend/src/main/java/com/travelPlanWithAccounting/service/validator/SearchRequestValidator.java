package com.travelPlanWithAccounting.service.validator;

import static com.travelPlanWithAccounting.service.util.GooglePlaceConstants.*;

import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.TextSearchRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SearchRequestValidator {

  public void validateNearby(SearchRequest req) {
    Integer max = req.getMaxResultCount();
    if (max != null && (max < MIN_RESULT_COUNT || max > MAX_RESULT_COUNT)) {
      throw new IllegalArgumentException(
          "maxResultCount 必須介於 " + MIN_RESULT_COUNT + "–" + MAX_RESULT_COUNT + " 之間");
    }

    String rank = req.getRankPreference();
    if (rank != null && !(RANK_RELEVANCE.equals(rank) || RANK_DISTANCE.equals(rank))) {
      throw new IllegalArgumentException("rankPreference 只能是 RELEVANCE 或 DISTANCE");
    }
  }

  /* ---------- TextSearch ---------- */
  public void validateText(TextSearchRequest req) {

    /* 1. textQuery 必填 */
    if (!StringUtils.hasText(req.getTextQuery())) {
      throw new IllegalArgumentException("textQuery 不可為空");
    }

    /* 2. maxResultCount 範圍檢查 (5–20) */
    Integer max = req.getMaxResultCount();
    if (max != null && (max < MIN_RESULT_COUNT || max > MAX_RESULT_COUNT)) {
      throw new IllegalArgumentException(
          "maxResultCount 必須介於 " + MIN_RESULT_COUNT + "–" + MAX_RESULT_COUNT + " 之間");
    }

    /* 3. rankPreference 合法值檢查 */
    String rank = req.getRankPreference();
    if (rank != null && !(RANK_RELEVANCE.equals(rank) || RANK_DISTANCE.equals(rank))) {
      throw new IllegalArgumentException("rankPreference 只能是 RELEVANCE 或 DISTANCE");
    }

    /* 4. includedTypes 最多只能一筆 */
    if (req.getIncludedTypes() != null && req.getIncludedTypes().size() > 1) {
      throw new IllegalArgumentException("TextSearch 只支援單一 includedType，請僅傳入一筆");
    }
  }
}
