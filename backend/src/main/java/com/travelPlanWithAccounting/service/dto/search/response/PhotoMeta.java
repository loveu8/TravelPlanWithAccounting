package com.travelPlanWithAccounting.service.dto.search.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Photo metadata stored in poi.photo_urls JSONB. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PhotoMeta(
    String name,      // Google resource name: places/xxxx/photos/xxx
    Integer widthPx,
    Integer heightPx,
    String authorAttribution,
    String urlSmall   // optional prebuilt small url (maxWidthPx=400)
) {}
