package com.travelPlanWithAccounting.service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import com.travelPlanWithAccounting.service.entity.Member;
import com.travelPlanWithAccounting.service.exception.TravelException;
import com.travelPlanWithAccounting.service.repository.MemberRepository;
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
    private static final String DEFAULT_POPULAR_IMAGE_URL = "/assets/images/popular-travel-default.png";

    private final TravelMainRepository travelMainRepository;
    private final TravelFavRepository travelFavRepository;
    private final MemberRepository memberRepository;
    private final SearchService searchService;

    public TravelPopularityService(
        TravelMainRepository travelMainRepository,
        TravelFavRepository travelFavRepository,
        MemberRepository memberRepository,
        SearchService searchService
    ) {
        this.travelMainRepository = travelMainRepository;
        this.travelFavRepository = travelFavRepository;
        this.memberRepository = memberRepository;
        this.searchService = searchService;
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
                    buildResponses(selected, favoritedTravelIds),
                    new PopularTravelMeta(strategy.value(), minFavorites, totalCandidates)
                );
            }
        }

        List<PopularTravelAggregate> topCandidates = aggregates.stream().limit(POPULAR_LIMIT).collect(Collectors.toList());
        List<PopularTravelResponse> topTravels = buildResponses(topCandidates, favoritedTravelIds);

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

    private List<PopularTravelResponse> buildResponses(
        List<PopularTravelAggregate> aggregates,
        Set<UUID> favoritedTravelIds
    ) {
        Map<UUID, String> creatorNames = resolveCreatorNames(aggregates);
        Map<UUID, String> locationNames = resolveLocationNames(aggregates);

        return aggregates.stream()
            .map(item -> toResponse(item, favoritedTravelIds, creatorNames, locationNames))
            .collect(Collectors.toList());
    }

    private PopularTravelResponse toResponse(
        PopularTravelAggregate aggregate,
        Set<UUID> favoritedTravelIds,
        Map<UUID, String> creatorNames,
        Map<UUID, String> locationNames
    ) {
        return new PopularTravelResponse(
            aggregate.travelMainId(),
            aggregate.title(),
            aggregate.startDate(),
            aggregate.endDate(),
            aggregate.visitPlace(),
            aggregate.favoritesCount(),
            Boolean.TRUE.equals(aggregate.isPrivate()),
            favoritedTravelIds.contains(aggregate.travelMainId()),
            creatorNames.getOrDefault(aggregate.travelMainId(), ""),
            locationNames.getOrDefault(aggregate.travelMainId(), ""),
            DEFAULT_POPULAR_IMAGE_URL
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

    private Map<UUID, String> resolveCreatorNames(List<PopularTravelAggregate> aggregates) {
        if (aggregates.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<UUID> memberIds = aggregates.stream()
            .map(PopularTravelAggregate::memberId)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (memberIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<UUID, Member> memberMap = memberRepository.findAllById(memberIds).stream()
            .collect(Collectors.toMap(Member::getId, member -> member));

        Map<UUID, String> creatorNames = new LinkedHashMap<>();
        for (PopularTravelAggregate aggregate : aggregates) {
            Member member = memberMap.get(aggregate.memberId());
            creatorNames.put(
                aggregate.travelMainId(),
                member != null ? resolveCreatorName(member) : ""
            );
        }
        return creatorNames;
    }

    private String resolveCreatorName(Member member) {
        String givenName = member.getGivenName() == null ? "" : member.getGivenName();
        String familyName = member.getFamilyName() == null ? "" : member.getFamilyName();
        String combinedName = (givenName + familyName).trim();
        if (!combinedName.isBlank()) {
            return combinedName;
        }
        String nickName = member.getNickName();
        return nickName == null ? "" : nickName;
    }

    private Map<UUID, String> resolveLocationNames(List<PopularTravelAggregate> aggregates) {
        if (aggregates.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<UUID, String> travelCodeMap = new LinkedHashMap<>();
        LinkedHashSet<String> uniqueCodes = new LinkedHashSet<>();
        for (PopularTravelAggregate aggregate : aggregates) {
            String code = resolveFirstLocationCode(aggregate.visitPlace());
            travelCodeMap.put(aggregate.travelMainId(), code);
            if (code != null && !code.isBlank()) {
                uniqueCodes.add(code);
            }
        }

        if (uniqueCodes.isEmpty()) {
            return travelCodeMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> "", (a, b) -> a, LinkedHashMap::new));
        }

        List<String> codes = new ArrayList<>(uniqueCodes);
        List<String> names = searchService.getLocationNamesByCodes(codes);
        Map<String, String> nameMap = new LinkedHashMap<>();
        for (int index = 0; index < codes.size(); index++) {
            String name = index < names.size() ? names.get(index) : null;
            nameMap.put(codes.get(index), name != null ? name : codes.get(index));
        }

        Map<UUID, String> locationNames = new LinkedHashMap<>();
        for (Map.Entry<UUID, String> entry : travelCodeMap.entrySet()) {
            String code = entry.getValue();
            String name = code == null || code.isBlank() ? "" : nameMap.getOrDefault(code, "");
            locationNames.put(entry.getKey(), name);
        }
        return locationNames;
    }

    private String resolveFirstLocationCode(List<String> visitPlace) {
        if (visitPlace == null || visitPlace.isEmpty()) {
            return null;
        }
        for (String code : visitPlace) {
            if (code != null && !code.isBlank()) {
                return code;
            }
        }
        return null;
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
