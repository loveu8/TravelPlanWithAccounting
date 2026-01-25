package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.travelPlanWithAccounting.service.entity.TravelDate;
import com.travelPlanWithAccounting.service.entity.TravelMain;

import lombok.Data;


@Data
public class TravelMainResponse  implements Serializable {
    private UUID id;
    private UUID memberId;
    private Boolean isPrivate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String title;
    private String notes;
    private List<String> visitPlace;
    private Instant createdAt;

    private List<TravelDate> generatedTravelDates;

    public TravelMainResponse(TravelMain travelMain, List<TravelDate> travelDates) {
        this.id = travelMain.getId();
        this.memberId = travelMain.getMemberId();
        this.isPrivate = travelMain.getIsPrivate();
        this.startDate = travelMain.getStartDate();
        this.endDate = travelMain.getEndDate();
        this.title = travelMain.getTitle();
        this.notes = travelMain.getNotes();
        this.visitPlace = travelMain.getVisitPlace();
        this.createdAt = travelMain.getCreatedAt();
        this.generatedTravelDates = travelDates;
    }
    
}
