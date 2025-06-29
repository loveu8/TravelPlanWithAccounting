package com.travelPlanWithAccounting.service.validator;

import static com.travelPlanWithAccounting.service.util.GooglePlaceConstants.*;

import com.travelPlanWithAccounting.service.dto.search.request.SearchRequest;
import org.springframework.stereotype.Component;

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
}
