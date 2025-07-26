package com.travelPlanWithAccounting.service.service;

import java.util.UUID;

public record PoiSavedEvent(UUID poiId, String placeId, String savedLang) {}
