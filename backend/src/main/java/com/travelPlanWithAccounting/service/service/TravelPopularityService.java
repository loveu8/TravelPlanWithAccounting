package com.travelPlanWithAccounting.service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travelPlanWithAccounting.service.dto.travelPlan.PopularTravelMeta;
import com.travelPlanWithAccounting.service.dto.travelPlan.PopularTravelResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.PopularTravelResult;
import com.travelPlanWithAccounting.service.exception.TravelException;
import com.travelPlanWithAccounting.service.repository.TravelFavRepository;
import com.travelPlanWithAccounting.service.repository.TravelMainRepository;
import com.travelPlanWithAccounting.service.repository.projection.PopularTravelAggregate;

/**
 * 提供依收藏數取得人氣行程的服務，支援 Top 及 Threshold 兩種策略。
 */
@Service
public class TravelPopularityService {

    private static final int POPULAR_LIMIT = 4;
    private static final int DEFAULT_MIN_FAVORITES = 5;

    private final TravelMainRepository travelMainRepository;
    private final TravelFavRepository travelFavRepository;

    public TravelPopularityService(TravelMainRepository travelMainRepository, TravelFavRepository travelFavRepository) {
        this.travelMainRepository = travelMainRepository;
        this.travelFavRepository = travelFavRepository;
    }

    @Transactional(readOnly = true)
    public PopularTravelResult getPopularTravels(String strategyParam, Integer minFavoritesParam, UUID memberId) {
        Strategy strategy = Strategy.fromParam(strategyParam);
        Integer effectiveMinFavorites = null;

        if (strategy == Strategy.THRESHOLD) {
            effectiveMinFavorites = resolveMinFavorites(minFavoritesParam);
        }

        List<PopularTravelAggregate> aggregates = travelMainRepository.findPopularPublicTravels();
        Set<UUID> favoritedTravelIds = resolveFavoritedTravelIds(memberId, aggregates);

        if (aggregates.isEmpty()) {
            return new PopularTravelResult(
                List.of(),
                new PopularTravelMeta(Strategy.TOP.value(), null, 0)
            );
        }

        if (strategy == Strategy.THRESHOLD) {
            int minFavorites = effectiveMinFavorites;
            List<PopularTravelAggregate> thresholdCandidates = aggregates.stream()
                .filter(item -> item.favoritesCount() >= minFavorites)
                .collect(Collectors.toCollection(ArrayList::new));
            int totalCandidates = thresholdCandidates.size();

            if (totalCandidates >= POPULAR_LIMIT) {
                Collections.shuffle(thresholdCandidates, ThreadLocalRandom.current());
                List<PopularTravelAggregate> selected = thresholdCandidates.subList(0, POPULAR_LIMIT);
                return new PopularTravelResult(
                    selected.stream().map(item -> toResponse(item, favoritedTravelIds)).collect(Collectors.toList()),
                    new PopularTravelMeta(strategy.value(), minFavorites, totalCandidates)
                );
            }
        }

        List<PopularTravelResponse> topTravels = aggregates.stream()
            .limit(POPULAR_LIMIT)
            .map(item -> toResponse(item, favoritedTravelIds))
            .collect(Collectors.toList());

        return new PopularTravelResult(
            topTravels,
            new PopularTravelMeta(Strategy.TOP.value(), null, aggregates.size())
        );
    }

    private int resolveMinFavorites(Integer minFavoritesParam) {
        int minFavorites = Objects.requireNonNullElse(minFavoritesParam, DEFAULT_MIN_FAVORITES);
        if (minFavorites < 0) {
            throw new TravelException.PopularMinFavoritesInvalid(minFavorites);
        }
        return minFavorites;
    }

    private PopularTravelResponse toResponse(PopularTravelAggregate aggregate, Set<UUID> favoritedTravelIds) {
        return new PopularTravelResponse(
            aggregate.travelMainId(),
            aggregate.title(),
            aggregate.startDate(),
            aggregate.endDate(),
            aggregate.visitPlace(),
            aggregate.favoritesCount(),
            Boolean.TRUE.equals(aggregate.isPrivate()),
            favoritedTravelIds.contains(aggregate.travelMainId())
        );
    }

    private Set<UUID> resolveFavoritedTravelIds(UUID memberId, List<PopularTravelAggregate> aggregates) {
        if (memberId == null || aggregates.isEmpty()) {
            return Collections.emptySet();
        }

        List<UUID> travelMainIds =
            aggregates.stream().map(PopularTravelAggregate::travelMainId).collect(Collectors.toList());

        return travelFavRepository.findByMember_IdAndTravelMain_IdIn(memberId, travelMainIds).stream()
            .map(fav -> fav.getTravelMain() != null ? fav.getTravelMain().getId() : null)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    private enum Strategy {
        TOP,
        THRESHOLD;

        private static Strategy fromParam(String value) {
            if (value == null || value.isBlank()) {
                return TOP;
            }
            try {
                return Strategy.valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new TravelException.PopularStrategyInvalid(value);
            }
        }

        private String value() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
