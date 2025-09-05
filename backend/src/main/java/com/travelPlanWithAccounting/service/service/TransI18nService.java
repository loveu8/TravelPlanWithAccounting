package com.travelPlanWithAccounting.service.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.travelPlanWithAccounting.service.dto.travelPlan.TransI18nRequest;
import com.travelPlanWithAccounting.service.entity.TransI18n;
import com.travelPlanWithAccounting.service.entity.TravelDetail;
import com.travelPlanWithAccounting.service.exception.TravelException;
import com.travelPlanWithAccounting.service.repository.TransI18nRepository;
import com.travelPlanWithAccounting.service.repository.TravelDetailRepository;

import jakarta.transaction.Transactional;

@Service
public class TransI18nService {
    private final TransI18nRepository transI18nRepository;
    private final TravelDetailRepository travelDetailRepository;

    public TransI18nService(TransI18nRepository transI18nRepository,
                            TravelDetailRepository travelDetailRepository) {
        this.transI18nRepository = transI18nRepository;
        this.travelDetailRepository = travelDetailRepository;
    }

    @Transactional
    public TransI18n createOrUpdate(TransI18nRequest request) {
        TravelDetail start =
            travelDetailRepository
                .findById(request.getStartDetailId())
                .orElseThrow(TravelException.TravelDetailNotFound::new);
        TravelDetail end =
            travelDetailRepository
                .findById(request.getEndDetailId())
                .orElseThrow(TravelException.TravelDetailNotFound::new);
        if (!start.getTravelMainId().equals(end.getTravelMainId())) {
            throw new TravelException.DetailsNotSameTravel();
        }
        TransI18n trans;
        if (request.getId() != null) {
            trans = transI18nRepository.findById(request.getId()).orElse(new TransI18n());
        } else {
            trans = new TransI18n();
            trans.setCreatedBy(request.getCreatedBy());
        }
        trans.setLangType(request.getLangType());
        trans.setStartDetailId(request.getStartDetailId());
        trans.setEndDetailId(request.getEndDetailId());
        trans.setInfosRaw(request.getInfosRaw());
        trans.setTransType(request.getTransType());
        trans.setTransTime(request.getTransTime());
        trans.setSummary(request.getSummary());
        trans.setNotes(request.getNotes());
        if (request.getId() != null) {
            trans.setUpdatedBy(request.getCreatedBy());
        }
        return transI18nRepository.save(trans);
    }

    @Transactional
    public void delete(UUID id) {
        transI18nRepository.deleteById(id);
    }

    @Transactional
    public void deleteByDetailId(UUID detailId) {
        transI18nRepository.deleteByStartDetailIdOrEndDetailId(detailId, detailId);
    }

    public List<TransI18n> findByDetailId(UUID detailId) {
        return transI18nRepository.findAll().stream()
            .filter(t -> t.getStartDetailId().equals(detailId) || t.getEndDetailId().equals(detailId))
            .collect(Collectors.toList());
    }
}
