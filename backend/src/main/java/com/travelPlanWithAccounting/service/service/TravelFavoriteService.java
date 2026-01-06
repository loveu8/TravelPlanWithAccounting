package com.travelPlanWithAccounting.service.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.travelPlanWithAccounting.service.dto.system.PageMeta;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelFavoriteListRequest;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelFavoriteListResponse;
import com.travelPlanWithAccounting.service.dto.travelPlan.TravelFavoriteSummary;
import com.travelPlanWithAccounting.service.entity.TravelFav;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.exception.TravelException;
import com.travelPlanWithAccounting.service.repository.TravelFavRepository;

@Service
public class TravelFavoriteService {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final TravelFavRepository travelFavRepository;

    public TravelFavoriteService(TravelFavRepository travelFavRepository) {
        this.travelFavRepository = travelFavRepository;
    }

    public TravelFavoriteListResponse listTravelFavorites(UUID memberId, TravelFavoriteListRequest request) {
        if (memberId == null) {
            throw new TravelException.TravelMemberIdRequired();
        }

        int page = resolvePage(request != null ? request.getPage() : null);
        int size = resolveSize(request != null ? request.getSize() : null);

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TravelFav> favoritesPage =
        travelFavRepository.findByMember_IdAndTravelMain_IsPrivateFalse(memberId, pageable);

        List<TravelFavoriteSummary> list =
        favoritesPage.getContent().stream().map(this::toSummary).filter(Objects::nonNull).toList();

        PageMeta meta = buildMeta(favoritesPage);

        return TravelFavoriteListResponse.builder().list(list).meta(meta).build();
    }

    private TravelFavoriteSummary toSummary(TravelFav favorite) {
        TravelMain travelMain = favorite.getTravelMain();
        if (travelMain == null) {
            return null;
        }

        return TravelFavoriteSummary.builder()
            .travelMainId(travelMain.getId())
            .title(travelMain.getTitle())
            .startDate(travelMain.getStartDate())
            .endDate(travelMain.getEndDate())
            .isPrivate(travelMain.getIsPrivate())
            .createdAt(travelMain.getCreatedAt())
            .updatedAt(travelMain.getUpdatedAt())
            .favoritedAt(favorite.getCreatedAt())
            .ownerMemberId(travelMain.getMemberId())
            .build();
    }

    private int resolvePage(Integer page) {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        if (page < 1) {
            throw new TravelException.TravelListPageInvalid(page);
        }
        return page;
    }

    private int resolveSize(Integer size) {
        if (size == null) {
            return DEFAULT_SIZE;
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new TravelException.TravelListSizeInvalid(size);
        }
        return size;
    }

    private PageMeta buildMeta(Page<?> page) {
        return PageMeta.builder()
            .page(page.getNumber() + 1)
            .size(page.getSize())
            .totalPages(page.getTotalPages())
            .totalElements(page.getTotalElements())
            .hasNext(page.hasNext())
            .hasPrev(page.hasPrevious())
            .build();
    }
}
