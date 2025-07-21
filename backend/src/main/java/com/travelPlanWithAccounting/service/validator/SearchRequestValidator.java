package com.travelPlanWithAccounting.service.validator;

import static com.travelPlanWithAccounting.service.util.GooglePlaceConstants.*;

import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import com.travelPlanWithAccounting.service.dto.search.request.TextSearchRequest;
import com.travelPlanWithAccounting.service.exception.SearchRequestException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SearchRequestValidator {

  public void validateNearby(SearchRequest req) {
    Integer max = req.getMaxResultCount();
    if (max != null && (max < MIN_RESULT_COUNT || max > MAX_RESULT_COUNT)) {
      throw new SearchRequestException.MaxResultRange();
    }

    String rank = req.getRankPreference();
    if (rank != null && !(RANK_RELEVANCE.equals(rank) || RANK_DISTANCE.equals(rank))) {
      throw new SearchRequestException.RankPreferenceError();
    }
  }

  /* ---------- TextSearch ---------- */
  public void validateText(TextSearchRequest req) {

    /* 1. textQuery 必填 */
    if (!StringUtils.hasText(req.getTextQuery())) {
      throw new SearchRequestException.TextQueryRequired();
    }

    /* 2. maxResultCount 範圍檢查 (5–20) */
    Integer max = req.getMaxResultCount();
    if (max != null && (max < MIN_RESULT_COUNT || max > MAX_RESULT_COUNT)) {
      throw new SearchRequestException.MaxResultRange();
    }

    /* 3. rankPreference 合法值檢查 */
    String rank = req.getRankPreference();
    if (rank != null && !(RANK_RELEVANCE.equals(rank) || RANK_DISTANCE.equals(rank))) {
      throw new SearchRequestException.RankPreferenceError();
    }

    /* 4. includedTypes 最多只能一筆 */
    if (req.getIncludedTypes() != null && req.getIncludedTypes().size() > 1) {
      throw new SearchRequestException.IncludedTypeLimit();
    }
  }
}
