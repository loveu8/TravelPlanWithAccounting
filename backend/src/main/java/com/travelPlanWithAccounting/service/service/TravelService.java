package com.travelPlanWithAccounting.service.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.travelPlanWithAccounting.service.entity.TravelDate;
import com.travelPlanWithAccounting.service.entity.TravelDetail;
import com.travelPlanWithAccounting.service.entity.TravelMain;
import com.travelPlanWithAccounting.service.repository.TravelDateRepository;
import com.travelPlanWithAccounting.service.repository.TravelDetailRepository;
import com.travelPlanWithAccounting.service.repository.TravelMainRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TravelService {

    private final TravelMainRepository travelMainRepository;
    private final TravelDateRepository travelDateRepository;
    private final TravelDetailRepository travelDetailRepository;

    public TravelMain getTravelMainById(UUID id) {
        return travelMainRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TravelMain not found"));
    }

    public List<TravelDate> getDatesByMainId(UUID travelMainId) {
        return travelDateRepository.findByTravelMainId(travelMainId);
    }

    public List<TravelDetail> getDetailsByMainId(UUID travelMainId) {
        return travelDetailRepository.findByTravelMainId(travelMainId);
    }

    public List<TravelDetail> getDetailsByDateId(UUID travelDateId) {
        return travelDetailRepository.findByTravelDateId(travelDateId);
    }

    public TravelMain saveMain(TravelMain main) {
        return travelMainRepository.save(main);
    }
}
