package com.travelPlanWithAccounting.service.dto.travelPlan;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelDayPlanCreateDTO {
    private LocalDate travelDate;
    private List<TravelDetailCreateDTO> details;
}
